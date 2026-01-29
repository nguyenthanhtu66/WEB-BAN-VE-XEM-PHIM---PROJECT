package vn.edu.hcmuaf.fit.demo1.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.dao.TicketWarehouseDao;
import vn.edu.hcmuaf.fit.demo1.dao.BookedSeatDao;
import vn.edu.hcmuaf.fit.demo1.dao.OrderDao;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/cancel-ticket")
public class CancelTicketController extends HttpServlet {
    private final Gson gson = new Gson();
    private final TicketWarehouseDao ticketWarehouseDao = new TicketWarehouseDao();
    private final BookedSeatDao bookedSeatDao = new BookedSeatDao();
    private final OrderDao orderDao = new OrderDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession(false);

        System.out.println("\n=== CANCEL TICKET CONTROLLER START ===");

        try {
            // 1. KIỂM TRA ĐĂNG NHẬP
            if (session == null) {
                System.out.println("❌ No session found");
                result.put("success", false);
                result.put("message", "Phiên làm việc đã hết hạn.");
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
                result.put("message", "Vui lòng đăng nhập.");
                sendResponse(response, result);
                return;
            }

            int userId = user.getId();
            System.out.println("👤 User ID: " + userId);

            // 2. LẤY THÔNG TIN TỪ REQUEST
            String ticketIdStr = request.getParameter("ticketId");
            String showtimeIdStr = request.getParameter("showtimeId");
            String seatIdStr = request.getParameter("seatId");

            if (ticketIdStr == null || ticketIdStr.isEmpty()) {
                result.put("success", false);
                result.put("message", "Thiếu thông tin vé.");
                sendResponse(response, result);
                return;
            }

            int ticketId = Integer.parseInt(ticketIdStr);
            System.out.println("🎫 Canceling ticket ID: " + ticketId);

            // 3. KIỂM TRA VÉ CÓ THUỘC VỀ USER NÀY KHÔNG
            boolean isOwner = ticketWarehouseDao.isTicketOwnedByUser(ticketId, userId);

            if (!isOwner) {
                System.out.println("❌ Ticket does not belong to user");
                result.put("success", false);
                result.put("message", "Vé không thuộc quyền sở hữu của bạn.");
                sendResponse(response, result);
                return;
            }

            // 4. KIỂM TRA TRẠNG THÁI VÉ
            String currentStatus = ticketWarehouseDao.getTicketStatus(ticketId);
            System.out.println("📊 Current ticket status: " + currentStatus);

            if (!"valid".equals(currentStatus)) {
                result.put("success", false);
                result.put("message", "Chỉ có thể hủy vé còn hiệu lực. Trạng thái hiện tại: " +
                        getStatusText(currentStatus));
                sendResponse(response, result);
                return;
            }

            // 5. LẤY THÔNG TIN SHOWTIME VÀ SEAT (nếu không có trong request)
            int showtimeId = 0;
            int seatId = 0;

            if (showtimeIdStr != null && seatIdStr != null) {
                showtimeId = Integer.parseInt(showtimeIdStr);
                seatId = Integer.parseInt(seatIdStr);
            } else {
                // Lấy từ database nếu không có trong request
                showtimeId = ticketWarehouseDao.getShowtimeId(ticketId);
                seatId = ticketWarehouseDao.getSeatId(ticketId);
            }

            System.out.println("📍 Showtime ID: " + showtimeId + ", Seat ID: " + seatId);

            // 6. THỰC HIỆN HỦY VÉ
            System.out.println("🔄 Cancelling ticket and releasing seat...");

            boolean success = ticketWarehouseDao.cancelTicket(ticketId);

            if (success) {
                // 7. RELEASE SEAT TRONG booked_seats - QUAN TRỌNG!
                if (showtimeId > 0 && seatId > 0) {
                    // Cập nhật status thành 'released' thay vì xóa
                    boolean seatReleased = bookedSeatDao.releaseSeat(showtimeId, seatId);
                    if (seatReleased) {
                        System.out.println("✅ Seat status changed to 'released': showtime=" + showtimeId + ", seat=" + seatId);

                        // Gửi event để cập nhật real-time seat map
                        sendSeatUpdateEvent(showtimeId, seatId, "available");
                    } else {
                        System.out.println("⚠️ Could not update seat status, but ticket was cancelled");
                    }
                }

                // 8. CẬP NHẬT ORDER STATUS (tùy chọn)
                int orderId = ticketWarehouseDao.getOrderId(ticketId);
                if (orderId > 0) {
                    // Có thể update order status
                    System.out.println("📦 Order ID: " + orderId);
                }

                System.out.println("✅ Ticket cancelled successfully");
                result.put("success", true);
                result.put("message", "Hủy vé thành công! Ghế đã được giải phóng.");
                result.put("ticketId", ticketId);
                result.put("showtimeId", showtimeId);
                result.put("seatId", seatId);

            } else {
                System.out.println("❌ Failed to cancel ticket");
                result.put("success", false);
                result.put("message", "Hủy vé thất bại. Vui lòng thử lại.");
            }

        } catch (NumberFormatException e) {
            System.err.println("❌ NumberFormatException: " + e.getMessage());
            result.put("success", false);
            result.put("message", "Dữ liệu không hợp lệ.");
        } catch (Exception e) {
            System.err.println("❌ Exception in CancelTicketController: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống: " + e.getMessage());
        }

        System.out.println("=== CANCEL TICKET CONTROLLER END ===\n");
        sendResponse(response, result);
    }

    private void sendSeatUpdateEvent(int showtimeId, int seatId, String status) {
        // Đây là nơi bạn có thể gửi event real-time
        // Có thể dùng WebSocket, Server-Sent Events, hoặc polling
        System.out.println("📢 Sending seat update event:");
        System.out.println("   Showtime ID: " + showtimeId);
        System.out.println("   Seat ID: " + seatId);
        System.out.println("   New Status: " + status);

        // TODO: Implement WebSocket hoặc SSE để cập nhật real-time
        // socketService.sendSeatUpdate(showtimeId, seatId, status);
    }

    private String getStatusText(String status) {
        switch (status) {
            case "valid": return "Còn hiệu lực";
            case "used": return "Đã sử dụng";
            case "expired": return "Hết hạn";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }

    private void sendResponse(HttpServletResponse response, Map<String, Object> result) throws IOException {
        response.getWriter().write(gson.toJson(result));
    }
}