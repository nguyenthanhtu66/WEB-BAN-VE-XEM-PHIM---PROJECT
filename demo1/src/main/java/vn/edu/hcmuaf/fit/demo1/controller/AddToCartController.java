package vn.edu.hcmuaf.fit.demo1.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.model.Cart;
import vn.edu.hcmuaf.fit.demo1.model.CartItem;
import vn.edu.hcmuaf.fit.demo1.service.BookingService;
import vn.edu.hcmuaf.fit.demo1.dao.ShowtimeDao;
import vn.edu.hcmuaf.fit.demo1.dao.SeatDao;
import vn.edu.hcmuaf.fit.demo1.dao.TicketTypeDao;
import vn.edu.hcmuaf.fit.demo1.dao.BookedSeatDao;
import vn.edu.hcmuaf.fit.demo1.model.Showtime;
import vn.edu.hcmuaf.fit.demo1.model.Seat;
import vn.edu.hcmuaf.fit.demo1.model.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/add-to-cart")
public class AddToCartController extends HttpServlet {

    private final BookingService bookingService = new BookingService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();

        System.out.println("\n=== ADD TO CART START ===");

        try {
            // Parse parameters
            String movieIdStr = request.getParameter("movieId");
            String showtimeIdStr = request.getParameter("showtimeId");
            String seatIdStr = request.getParameter("seatId");
            String ticketTypeIdStr = request.getParameter("ticketTypeId");

            System.out.println("📥 Parameters received:");
            System.out.println("  movieId: " + movieIdStr);
            System.out.println("  showtimeId: " + showtimeIdStr);
            System.out.println("  seatId: " + seatIdStr);
            System.out.println("  ticketTypeId: " + ticketTypeIdStr);

            // Validate required parameters
            if (movieIdStr == null || showtimeIdStr == null || seatIdStr == null || ticketTypeIdStr == null) {
                result.put("success", false);
                result.put("message", "Thiếu thông tin bắt buộc");
                sendResponse(response, result);
                return;
            }

            int movieId = Integer.parseInt(movieIdStr);
            int showtimeId = Integer.parseInt(showtimeIdStr);
            int seatId = Integer.parseInt(seatIdStr);
            int ticketTypeId = Integer.parseInt(ticketTypeIdStr);

            // Lấy session và sessionId
            HttpSession session = request.getSession(false);
            String sessionId = session != null ? session.getId() : null;
            Integer userId = null;

            if (session != null) {
                User user = (User) session.getAttribute("user");
                if (user != null) {
                    userId = user.getId();
                    System.out.println("👤 User ID from session: " + userId);
                } else {
                    System.out.println("👤 Anonymous user");
                }
                System.out.println("🔑 Session ID: " + sessionId);
            } else {
                System.out.println("⚠ No session found");
            }

            // Basic validation
            boolean basicValidation = validateBasicBooking(movieId, showtimeId, ticketTypeId, seatId);
            System.out.println("✅ Basic validation: " + basicValidation);

            if (!basicValidation) {
                result.put("success", false);
                result.put("message", "Thông tin đặt vé không hợp lệ");
                sendResponse(response, result);
                return;
            }

            // Kiểm tra xem ghế có đang được reserve bởi session/user này không
            BookedSeatDao bookedSeatDao = new BookedSeatDao();
            boolean isReservedByThisUser = false;

            if (userId != null) {
                // Đã login: kiểm tra theo user_id
                isReservedByThisUser = bookedSeatDao.isSeatReservedByUser(showtimeId, seatId, userId);
                System.out.println("Is reserved by user " + userId + ": " + isReservedByThisUser);
            } else if (sessionId != null) {
                // Chưa login: kiểm tra theo session_id
                isReservedByThisUser = bookedSeatDao.isSeatReservedBySession(showtimeId, seatId, sessionId);
                System.out.println("Is reserved by session " + sessionId + ": " + isReservedByThisUser);
            }

            // Nếu ghế KHÔNG đang được reserve bởi user/session này
            if (!isReservedByThisUser) {
                // Kiểm tra xem ghế có available không
                boolean isAvailable;
                if (userId != null) {
                    isAvailable = bookedSeatDao.isSeatAvailableForUser(showtimeId, seatId, userId);
                } else if (sessionId != null) {
                    isAvailable = bookedSeatDao.isSeatAvailable(showtimeId, seatId, sessionId);
                } else {
                    isAvailable = bookedSeatDao.isSeatAvailable(showtimeId, seatId);
                }

                System.out.println("Seat available: " + isAvailable);

                if (!isAvailable) {
                    result.put("success", false);
                    result.put("message", "Ghế này đã được đặt hoặc giữ bởi người khác");
                    sendResponse(response, result);
                    return;
                }
            }

            // Tạo CartItem
            System.out.println("🛒 Creating cart item...");
            CartItem cartItem = bookingService.createCartItem(movieId, showtimeId, seatId, ticketTypeId);
            if (cartItem == null) {
                result.put("success", false);
                result.put("message", "Không thể tạo vé");
                sendResponse(response, result);
                return;
            }

            System.out.println("✅ CartItem created: " + cartItem);

            // Lấy hoặc tạo cart trong session
            HttpSession cartSession = request.getSession(true);
            Cart cart = (Cart) cartSession.getAttribute("cart");
            if (cart == null) {
                cart = new Cart();
                System.out.println("🆕 Created new cart");
            }

            // Kiểm tra xem ghế đã có trong cart chưa
            boolean alreadyInCart = cart.containsSeat(showtimeId, seatId);
            if (alreadyInCart) {
                result.put("success", false);
                result.put("message", "Ghế này đã có trong giỏ hàng của bạn");
                sendResponse(response, result);
                return;
            }

            // Thêm item vào cart
            cart.addItem(cartItem);
            cartSession.setAttribute("cart", cart);
            System.out.println("✅ Added to cart. Total items: " + cart.getTotalItems());

            // Reserve seat với status đặc biệt để phân biệt
            boolean reserveSuccess;
            if (userId != null) {
                // Đã login: reserve theo user_id
                reserveSuccess = bookedSeatDao.reserveSeatForUser(showtimeId, seatId, userId);
                System.out.println("🔒 Seat reserved for user " + userId + ": " + reserveSuccess);
            } else {
                // Chưa login: reserve theo session_id
                reserveSuccess = bookedSeatDao.reserveSeatForSession(showtimeId, seatId, sessionId);
                System.out.println("🔒 Seat reserved for session " + sessionId + ": " + reserveSuccess);
            }

            // Cập nhật số lượng cart cho tất cả session
            updateCartCountInAllSessions(request, cart);

            result.put("success", true);
            result.put("message", "Đã thêm vé vào giỏ hàng");
            result.put("cartSize", cart.getTotalItems());
            result.put("totalAmount", cart.getTotalAmount());
            result.put("item", Map.of(
                    "movieTitle", cartItem.getMovieTitle(),
                    "seatCode", cartItem.getSeatCode(),
                    "showDate", cartItem.getShowDate().toString(),
                    "showTime", cartItem.getShowTime().toString(),
                    "price", cartItem.getPrice()
            ));

            System.out.println("📊 Cart summary:");
            System.out.println("  Items: " + cart.getTotalItems());
            System.out.println("  Total amount: " + cart.getTotalAmount());
            System.out.println("  Reserved seat: " + reserveSuccess);

        } catch (NumberFormatException e) {
            System.err.println("❌ NumberFormatException: " + e.getMessage());
            result.put("success", false);
            result.put("message", "Dữ liệu không hợp lệ: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Exception in AddToCart: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi server: " + e.getMessage());
        }

        System.out.println("📤 Response: " + result);
        System.out.println("=== ADD TO CART END ===\n");

        sendResponse(response, result);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            HttpSession session = request.getSession(false);
            Cart cart = null;

            if (session != null) {
                cart = (Cart) session.getAttribute("cart");
            }

            if (cart == null) {
                cart = new Cart();
            }

            result.put("success", true);
            result.put("cartSize", cart.getTotalItems());
            result.put("totalAmount", cart.getTotalAmount());
            result.put("items", cart.getItems());

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Lỗi khi lấy thông tin giỏ hàng");
        }

        sendResponse(response, result);
    }

    // Kiểm tra xem ghế có đang được reserve bởi user không
    private boolean isSeatReservedByUser(int showtimeId, int seatId, int userId) {
        try {
            BookedSeatDao bookedSeatDao = new BookedSeatDao();
            return bookedSeatDao.isSeatReservedByUser(showtimeId, seatId, userId);
        } catch (Exception e) {
            System.err.println("Error checking user reservation: " + e.getMessage());
            return false;
        }
    }

    // Phương thức validate cơ bản (không kiểm tra availability)
    private boolean validateBasicBooking(int movieId, int showtimeId, int ticketTypeId, int seatId) {
        try {
            // Kiểm tra showtime
            ShowtimeDao showtimeDao = new ShowtimeDao();
            Showtime showtime = showtimeDao.getShowtimeById(showtimeId);
            if (showtime == null || !showtime.isActive()) {
                System.out.println("❌ Showtime invalid or inactive");
                return false;
            }

            // Kiểm tra movie match
            if (showtime.getMovieId() != movieId) {
                System.out.println("❌ Movie mismatch. Showtime movie: " + showtime.getMovieId() + ", Request movie: " + movieId);
                return false;
            }

            // Kiểm tra ticket type
            TicketTypeDao ticketTypeDao = new TicketTypeDao();
            if (!ticketTypeDao.isTicketTypeValid(ticketTypeId)) {
                System.out.println("❌ Ticket type invalid: " + ticketTypeId);
                return false;
            }

            // Kiểm tra seat tồn tại
            SeatDao seatDao = new SeatDao();
            Seat seat = seatDao.getSeatById(seatId);
            if (seat == null || !seat.isActive()) {
                System.out.println("❌ Seat invalid or inactive: " + seatId);
                return false;
            }

            // Kiểm tra seat có thuộc đúng phòng không
            if (seat.getRoomId() != showtime.getRoomId()) {
                System.out.println("❌ Seat room mismatch. Seat room: " + seat.getRoomId() + ", Showtime room: " + showtime.getRoomId());
                return false;
            }

            System.out.println("✅ Basic validation passed");
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error in basic validation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật cart count trong tất cả session (cho real-time update)
    private void updateCartCountInAllSessions(HttpServletRequest request, Cart cart) {
        try {
            // Lấy application scope để lưu cart count
            request.getServletContext().setAttribute("cartCount", cart.getTotalItems());

            // Cập nhật session attribute
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("cart", cart);
            }

            System.out.println("🔄 Updated cart count in application scope: " + cart.getTotalItems());
        } catch (Exception e) {
            System.err.println("Error updating cart count: " + e.getMessage());
        }
    }

    private void sendResponse(HttpServletResponse response, Map<String, Object> result) throws IOException {
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(result));
        out.flush();
    }
}