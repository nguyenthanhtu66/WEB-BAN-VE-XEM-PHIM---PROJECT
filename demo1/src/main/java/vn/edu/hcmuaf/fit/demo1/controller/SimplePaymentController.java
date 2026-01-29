package vn.edu.hcmuaf.fit.demo1.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.model.Cart;
import vn.edu.hcmuaf.fit.demo1.model.CartItem;
import vn.edu.hcmuaf.fit.demo1.model.Order;
import vn.edu.hcmuaf.fit.demo1.model.OrderDetail;
import vn.edu.hcmuaf.fit.demo1.model.TicketWarehouse;
import vn.edu.hcmuaf.fit.demo1.dao.OrderDao;
import vn.edu.hcmuaf.fit.demo1.dao.OrderDetailDao;
import vn.edu.hcmuaf.fit.demo1.dao.BookedSeatDao;
import vn.edu.hcmuaf.fit.demo1.dao.TicketWarehouseDao;
import vn.edu.hcmuaf.fit.demo1.service.BookingService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@WebServlet("/api/simple-payment")
public class SimplePaymentController extends HttpServlet {
    private final Gson gson = new Gson();
    private final BookingService bookingService = new BookingService();
    private final OrderDao orderDao = new OrderDao();
    private final OrderDetailDao orderDetailDao = new OrderDetailDao();
    private final BookedSeatDao bookedSeatDao = new BookedSeatDao();
    private final TicketWarehouseDao ticketWarehouseDao = new TicketWarehouseDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Cấu hình CORS
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession(false);

        System.out.println("\n=== SIMPLE PAYMENT CONTROLLER START ===");
        System.out.println("💰 Processing payment...");
        System.out.println("📥 Request Parameters:");
        request.getParameterMap().forEach((key, values) -> {
            System.out.println("  " + key + ": " + Arrays.toString(values));
        });

        try {
            // 1. KIỂM TRA ĐĂNG NHẬP
            if (session == null) {
                System.out.println("❌ No session found");
                result.put("success", false);
                result.put("message", "Phiên làm việc đã hết hạn. Vui lòng đăng nhập lại.");
                sendResponse(response, result);
                return;
            }

            User user = (User) session.getAttribute("user");
            if (user == null) {
                user = (User) session.getAttribute("loggedUser");
            }

            if (user == null) {
                System.out.println("❌ User not logged in");
                result.put("success", false);
                result.put("message", "Vui lòng đăng nhập để thanh toán");
                result.put("redirect", request.getContextPath() + "/login.jsp?redirect=thanh-toan.jsp");
                sendResponse(response, result);
                return;
            }

            int userId = user.getId();
            System.out.println("✅ User authenticated - ID: " + userId + ", Name: " + user.getFullName());
            System.out.println("📥 Session ID: " + session.getId());
            System.out.println("📥 Session attributes: " + getSessionAttributes(session));

            // 2. LẤY THÔNG TIN THANH TOÁN
            String paymentType = request.getParameter("type");
            String paymentMethod = request.getParameter("paymentMethod");
            String note = request.getParameter("note");

            System.out.println("📋 Payment details:");
            System.out.println("  - Type: " + paymentType);
            System.out.println("  - Method: " + (paymentMethod != null ? paymentMethod : "Not specified"));
            System.out.println("  - Note: " + (note != null && !note.isEmpty() ? note : "None"));

            // 3. LẤY DANH SÁCH VÉ CẦN THANH TOÁN
            List<CartItem> itemsToProcess = new ArrayList<>();

            if ("payNow".equals(paymentType)) {
                // Thanh toán từ modal - lấy từ session
                System.out.println("🛒 Processing payment from modal");
                System.out.println("📥 Checking payment data in session...");

                Map<String, Object> paymentData = (Map<String, Object>) session.getAttribute("paymentData");

                if (paymentData == null) {
                    System.out.println("❌ No payment data in session");

                    // THỬ LẤY TỪ REQUEST PARAMETERS (fallback)
                    String movieIdStr = request.getParameter("movieId");
                    String showtimeIdStr = request.getParameter("showtimeId");
                    String seatIdStr = request.getParameter("seatId");
                    String ticketTypeIdStr = request.getParameter("ticketTypeId");

                    if (movieIdStr != null && showtimeIdStr != null &&
                            seatIdStr != null && ticketTypeIdStr != null) {
                        System.out.println("🔄 Trying to create payment data from parameters...");

                        try {
                            CartItem cartItem = bookingService.createCartItem(
                                    Integer.parseInt(movieIdStr),
                                    Integer.parseInt(showtimeIdStr),
                                    Integer.parseInt(seatIdStr),
                                    Integer.parseInt(ticketTypeIdStr)
                            );

                            if (cartItem != null) {
                                paymentData = new HashMap<>();
                                paymentData.put("movieId", cartItem.getMovieId());
                                paymentData.put("showtimeId", cartItem.getShowtimeId());
                                paymentData.put("seatId", cartItem.getSeatId());
                                paymentData.put("ticketTypeId", cartItem.getTicketTypeId());
                                paymentData.put("movieTitle", cartItem.getMovieTitle());
                                paymentData.put("seatCode", cartItem.getSeatCode());
                                paymentData.put("showDate", cartItem.getShowDate().toString());
                                paymentData.put("showTime", cartItem.getShowTime().toString());
                                paymentData.put("roomName", cartItem.getRoomName());
                                paymentData.put("ticketTypeName", cartItem.getTicketTypeName());
                                paymentData.put("price", cartItem.getPrice());
                                paymentData.put("roomId", cartItem.getRoomId());

                                // Lưu vào session
                                session.setAttribute("paymentData", paymentData);
                                System.out.println("✅ Created and saved payment data from parameters");
                            }
                        } catch (Exception e) {
                            System.err.println("❌ Error creating payment data from params: " + e.getMessage());
                        }
                    }

                    if (paymentData == null) {
                        result.put("success", false);
                        result.put("message", "Không tìm thấy thông tin thanh toán. Vui lòng đặt vé lại.");
                        sendResponse(response, result);
                        return;
                    }
                } else {
                    System.out.println("✅ Found payment data in session");
                    System.out.println("📦 Payment data: " + paymentData);
                }

                // Tạo CartItem từ paymentData
                try {
                    System.out.println("🔄 Creating cart item from payment data...");

                    CartItem cartItem = bookingService.createCartItem(
                            ((Number) paymentData.get("movieId")).intValue(),
                            ((Number) paymentData.get("showtimeId")).intValue(),
                            ((Number) paymentData.get("seatId")).intValue(),
                            ((Number) paymentData.get("ticketTypeId")).intValue()
                    );

                    if (cartItem != null) {
                        itemsToProcess.add(cartItem);
                        System.out.println("✅ Created cart item from payment data:");
                        System.out.println("  - Movie: " + cartItem.getMovieTitle());
                        System.out.println("  - Seat: " + cartItem.getSeatCode());
                        System.out.println("  - Showtime: " + cartItem.getShowDate() + " " + cartItem.getShowTime());
                        System.out.println("  - Price: " + cartItem.getPrice());
                        System.out.println("  - Showtime ID: " + cartItem.getShowtimeId());
                        System.out.println("  - Seat ID: " + cartItem.getSeatId());
                    } else {
                        System.out.println("❌ Failed to create cart item");
                    }

                } catch (Exception e) {
                    System.err.println("❌ Error creating cart item: " + e.getMessage());
                    e.printStackTrace();
                    result.put("success", false);
                    result.put("message", "Lỗi xử lý thông tin vé: " + e.getMessage());
                    sendResponse(response, result);
                    return;
                }

                // Xóa paymentData khỏi session sau khi xử lý
                session.removeAttribute("paymentData");
                System.out.println("✅ Removed paymentData from session");

            } else if ("cart".equals(paymentType)) {
                // Thanh toán từ giỏ hàng
                System.out.println("🛒 Processing payment from cart");

                Cart cart = (Cart) session.getAttribute("cart");
                if (cart == null || cart.isEmpty()) {
                    System.out.println("❌ Cart is empty");
                    result.put("success", false);
                    result.put("message", "Giỏ hàng trống");
                    sendResponse(response, result);
                    return;
                }

                itemsToProcess.addAll(cart.getItems());
                System.out.println("✅ Found " + itemsToProcess.size() + " items in cart");

            } else {
                System.out.println("❌ Invalid payment type: " + paymentType);
                result.put("success", false);
                result.put("message", "Loại thanh toán không hợp lệ");
                sendResponse(response, result);
                return;
            }

            if (itemsToProcess.isEmpty()) {
                System.out.println("❌ No items to process");
                result.put("success", false);
                result.put("message", "Không có vé nào để thanh toán");
                sendResponse(response, result);
                return;
            }

            // 4. KIỂM TRA TÍNH KHẢ DỤNG - DÙNG PHƯƠNG THỨC ĐẶC BIỆT
            System.out.println("🔍 CHECKING SEAT AVAILABILITY FOR PAYMENT...");
            boolean allSeatsAvailable = true;
            String failedSeat = null;
            String failureReason = null;

            for (CartItem item : itemsToProcess) {
                System.out.println("\n🎯 Checking seat: " + item.getSeatCode());

                // DÙNG PHƯƠNG THỨC MỚI - ĐƠN GIẢN HƠN
                boolean isAvailable = bookedSeatDao.isSeatAvailableForPayment(
                        item.getShowtimeId(),
                        item.getSeatId(),
                        userId
                );

                if (!isAvailable) {
                    allSeatsAvailable = false;
                    failedSeat = item.getSeatCode();

                    // Lấy thêm thông tin để debug
                    Map<String, Object> seatStatus = bookedSeatDao.getSeatStatus(
                            item.getShowtimeId(),
                            item.getSeatId()
                    );
                    System.out.println("❌ Seat " + item.getSeatCode() + " not available. Status: " + seatStatus);
                    break;
                }

                System.out.println("✅ Seat " + item.getSeatCode() + " is available");

                // Gia hạn reservation ngay lập tức
                bookedSeatDao.extendReservation(item.getShowtimeId(), item.getSeatId(), userId);
            }

            if (!allSeatsAvailable) {
                result.put("success", false);
                result.put("message", "Ghế " + failedSeat + " đã được đặt bởi người khác. Vui lòng chọn ghế khác.");
                sendResponse(response, result);
                return;
            }

            System.out.println("✅ ALL SEATS AVAILABLE FOR PAYMENT!");

            // 5. XỬ LÝ THANH TOÁN
            System.out.println("💳 Processing payment for " + itemsToProcess.size() + " items...");

            boolean allSuccess = true;
            List<String> successMessages = new ArrayList<>();
            List<String> errorMessages = new ArrayList<>();
            List<Integer> createdOrderIds = new ArrayList<>();

            // Nhóm items theo showtimeId (mỗi showtime một order)
            Map<Integer, List<CartItem>> itemsByShowtime = new HashMap<>();
            for (CartItem item : itemsToProcess) {
                itemsByShowtime
                        .computeIfAbsent(item.getShowtimeId(), k -> new ArrayList<>())
                        .add(item);
            }

            System.out.println("📊 Grouped into " + itemsByShowtime.size() + " showtime(s)");

            // Xử lý từng showtime
            for (Map.Entry<Integer, List<CartItem>> entry : itemsByShowtime.entrySet()) {
                int showtimeId = entry.getKey();
                List<CartItem> showtimeItems = entry.getValue();

                System.out.println("\n🎬 Processing showtime ID: " + showtimeId);
                System.out.println("   Items: " + showtimeItems.size());

                // 5.1 TÍNH TỔNG TIỀN
                double totalAmount = calculateTotalAmount(showtimeItems);
                System.out.println("   Total amount: " + totalAmount);

                // 5.2 TẠO ORDER
                Order order = new Order();
                order.setUserId(userId);
                order.setShowtimeId(showtimeId);
                order.setTotalQuantity(showtimeItems.size());
                order.setTotalAmount(totalAmount);
                order.setFinalAmount(totalAmount);
                order.setStatus("paid");
                order.setBookingDate(LocalDateTime.now());
                order.setPaymentDate(LocalDateTime.now());
                order.setNotes(note);

                System.out.println("   Creating order...");
                int orderId = orderDao.createSimpleOrder(order);

                if (orderId <= 0) {
                    System.err.println("❌ Failed to create order for showtime " + showtimeId);
                    errorMessages.add("Không thể tạo đơn hàng cho suất chiếu");
                    allSuccess = false;
                    continue;
                }

                createdOrderIds.add(orderId);
                System.out.println("✅ Order created - ID: " + orderId);

                // 5.3 XỬ LÝ TỪNG ITEM TRONG ORDER
                int successfulItems = 0;

                for (CartItem item : showtimeItems) {
                    try {
                        System.out.println("   Processing seat: " + item.getSeatCode());
                        System.out.println("     Showtime ID: " + item.getShowtimeId());
                        System.out.println("     Seat ID: " + item.getSeatId());

                        // Tạo order detail
                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setOrderId(orderId);
                        orderDetail.setSeatId(item.getSeatId());
                        orderDetail.setTicketTypeId(item.getTicketTypeId());
                        orderDetail.setPrice(item.getPrice());

                        int orderDetailId = orderDetailDao.createOrderDetail(orderDetail);
                        if (orderDetailId <= 0) {
                            System.err.println("❌ Failed to create order detail for seat " + item.getSeatCode());
                            errorMessages.add("Không thể tạo chi tiết cho ghế " + item.getSeatCode());
                            allSuccess = false;
                            continue;
                        }

                        System.out.println("     Order detail created - ID: " + orderDetailId);

                        // BOOK SEAT - Sử dụng phương thức mới
                        boolean seatBooked = bookedSeatDao.bookSeatForPayment(
                                item.getShowtimeId(),
                                item.getSeatId(),
                                orderId,
                                userId
                        );

                        if (!seatBooked) {
                            // Fallback: Kiểm tra lại trạng thái
                            Map<String, Object> seatStatus = bookedSeatDao.getSeatStatus(item.getShowtimeId(), item.getSeatId());
                            System.out.println("❌ Failed to book seat " + item.getSeatCode() + ", current status: " + seatStatus);

                            // Nếu ghế đã được book bởi chính user này (trùng)
                            if ("booked".equals(seatStatus.get("status")) &&
                                    userId == ((Integer) seatStatus.get("user_id"))) {
                                System.out.println("ℹ️ Seat already booked by same user - continuing");
                                // Vẫn tiếp tục xử lý
                            } else {
                                errorMessages.add("Không thể đặt ghế " + item.getSeatCode());
                                allSuccess = false;
                                continue;
                            }
                        }

                        System.out.println("✅ Seat booked: " + item.getSeatCode());

                        // THÊM VÀO KHO VÉ
                        TicketWarehouse ticket = new TicketWarehouse();
                        ticket.setUserId(userId);
                        ticket.setOrderId(orderId);
                        ticket.setOrderDetailId(orderDetailId);
                        ticket.setTicketCode(generateTicketCode(orderId, orderDetailId));
                        ticket.setMovieId(item.getMovieId());
                        ticket.setMovieTitle(item.getMovieTitle());
                        ticket.setShowtimeId(item.getShowtimeId());
                        ticket.setShowDate(item.getShowDate());
                        ticket.setShowTime(item.getShowTime());
                        ticket.setRoomId(item.getRoomId());
                        ticket.setRoomName(item.getRoomName());
                        ticket.setSeatId(item.getSeatId());
                        ticket.setSeatCode(item.getSeatCode());
                        ticket.setTicketStatus("valid");

                        boolean ticketCreated = ticketWarehouseDao.createTicket(ticket);
                        if (!ticketCreated) {
                            System.err.println("❌ Failed to add ticket to warehouse: " + item.getSeatCode());
                            errorMessages.add("Không thể lưu vé vào kho cho ghế " + item.getSeatCode());
                            allSuccess = false;
                            continue;
                        }

                        System.out.println("✅ Ticket added to warehouse: " + ticket.getTicketCode());
                        successfulItems++;

                        // Thêm thông báo thành công
                        String successMsg = String.format("Vé %s - %s (%s)",
                                item.getSeatCode(),
                                item.getMovieTitle(),
                                item.getShowDate() + " " + item.getShowTime()
                        );
                        successMessages.add(successMsg);

                    } catch (Exception e) {
                        System.err.println("❌ Error processing seat " + item.getSeatCode() + ": " + e.getMessage());
                        e.printStackTrace();
                        errorMessages.add("Lỗi xử lý ghế " + item.getSeatCode());
                        allSuccess = false;
                    }
                }

                System.out.println("   Successful items in this order: " + successfulItems + "/" + showtimeItems.size());
            }

            // 6. XỬ LÝ SAU THANH TOÁN
            if (allSuccess) {
                // Xóa cart nếu thanh toán từ cart
                if ("cart".equals(paymentType)) {
                    Cart cart = (Cart) session.getAttribute("cart");
                    if (cart != null) {
                        // Release tất cả seats trong cart
                        for (CartItem item : cart.getItems()) {
                            bookedSeatDao.releaseSeat(item.getShowtimeId(), item.getSeatId());
                        }

                        cart.clear();
                        session.setAttribute("cart", cart);
                        System.out.println("✅ Cart cleared after successful payment");
                    }
                }

                // Cập nhật seat map real-time
                updateSeatMapRealTime(itemsToProcess);

                // Tạo thông báo thành công
                StringBuilder successMessage = new StringBuilder();
                successMessage.append("Thanh toán thành công! ");

                if (successMessages.size() == 1) {
                    successMessage.append(successMessages.get(0));
                } else {
                    successMessage.append("Đã mua ").append(successMessages.size()).append(" vé.");
                }

                // Cập nhật số lượng order
                if (!createdOrderIds.isEmpty()) {
                    successMessage.append(" Mã đơn hàng: ");
                    for (int i = 0; i < createdOrderIds.size(); i++) {
                        if (i > 0) successMessage.append(", ");
                        successMessage.append("#").append(createdOrderIds.get(i));
                    }
                }

                result.put("success", true);
                result.put("message", successMessage.toString());
                result.put("orderIds", createdOrderIds);
                result.put("ticketCount", successMessages.size());
                result.put("redirect", request.getContextPath() + "/ticket-warehouse?paymentSuccess=true");

                System.out.println("🎉 PAYMENT SUCCESSFUL!");
                System.out.println("   Orders created: " + createdOrderIds.size());
                System.out.println("   Tickets purchased: " + successMessages.size());

            } else {
                // ROLLBACK: Hủy các order đã tạo nếu có lỗi
                if (!createdOrderIds.isEmpty()) {
                    System.out.println("🔄 Rolling back created orders due to errors...");
                    for (int orderId : createdOrderIds) {
                        try {
                            // Xóa order details trước
                            orderDetailDao.deleteByOrderId(orderId);
                            // Xóa order
                            orderDao.deleteOrder(orderId);
                            System.out.println("   Rolled back order: " + orderId);
                        } catch (Exception e) {
                            System.err.println("   Failed to rollback order " + orderId + ": " + e.getMessage());
                        }
                    }
                }

                // Tạo thông báo lỗi
                StringBuilder errorMessage = new StringBuilder();
                errorMessage.append("Thanh toán không thành công. ");

                if (!errorMessages.isEmpty()) {
                    errorMessage.append(String.join(", ", errorMessages));
                } else {
                    errorMessage.append("Có lỗi xảy ra trong quá trình thanh toán.");
                }

                result.put("success", false);
                result.put("message", errorMessage.toString());

                System.out.println("❌ PAYMENT FAILED!");
                System.out.println("   Errors: " + String.join(", ", errorMessages));
            }

        } catch (Exception e) {
            System.err.println("❌ UNEXPECTED ERROR in SimplePaymentController: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống: " + e.getMessage());
        }

        System.out.println("📤 Sending response: " + ((boolean) result.get("success") ? "SUCCESS" : "FAILURE"));
        System.out.println("=== SIMPLE PAYMENT CONTROLLER END ===\n");

        sendResponse(response, result);
    }

    // ========== HELPER METHODS ==========

    private double calculateTotalAmount(List<CartItem> items) {
        return items.stream()
                .mapToDouble(CartItem::getPrice)
                .sum();
    }

    private String generateTicketCode(int orderId, int orderDetailId) {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(7, 13);
        String random = String.format("%03d", (int) (Math.random() * 1000));
        return String.format("VE-%s-%06d-%s", timestamp, orderDetailId, random);
    }

    private void updateSeatMapRealTime(List<CartItem> items) {
        // Ghi log để debug - thực tế có thể implement WebSocket hoặc client polling
        System.out.println("🔄 Updating seat map for booked seats:");

        for (CartItem item : items) {
            System.out.println("   - Showtime " + item.getShowtimeId() +
                    ", Seat " + item.getSeatCode() +
                    " → STATUS: BOOKED");

            // Gửi event real-time nếu có WebSocket
            // sendSeatUpdateEvent(item.getShowtimeId(), item.getSeatId(), "booked");
        }
    }

    private void sendResponse(HttpServletResponse response, Map<String, Object> result) throws IOException {
        response.getWriter().write(gson.toJson(result));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chỉ xử lý POST request
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "Method not allowed");
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        sendResponse(response, result);
    }

    private String getSessionAttributes(HttpSession session) {
        if (session == null) return "No session";

        StringBuilder sb = new StringBuilder();
        java.util.Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            Object value = session.getAttribute(name);
            sb.append(name).append("=").append(value).append(", ");
        }
        return sb.toString();
    }
}