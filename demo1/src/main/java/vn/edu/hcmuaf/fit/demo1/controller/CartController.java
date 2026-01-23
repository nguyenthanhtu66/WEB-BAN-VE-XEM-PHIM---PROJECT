package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.model.*;
import vn.edu.hcmuaf.fit.demo1.service.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(name = "CartController", urlPatterns = {
        "/cart",
        "/cart/add",
        "/cart/update",
        "/cart/remove",
        "/cart/clear",
        "/cart/apply-promo"
})
public class CartController extends HttpServlet {

    private final CartService cartService = new CartService();
    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/cart".equals(path)) {
            showCartPage(request, response);
        } else if (path.startsWith("/cart/remove")) {
            removeCartItem(request, response);
        } else if ("/cart/clear".equals(path)) {
            clearCart(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/cart/add".equals(path)) {
            addToCart(request, response);
        } else if ("/cart/update".equals(path)) {
            updateCartItem(request, response);
        } else if ("/cart/apply-promo".equals(path)) {
            applyPromoCode(request, response);
        }
    }

    // Hiển thị trang giỏ hàng
    private void showCartPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        ShoppingCart cart = cartService.getOrCreateCart(session);

        request.setAttribute("cart", cart);
        request.setAttribute("user", session.getAttribute("user"));

        request.getRequestDispatcher("/Gio-hang.jsp").forward(request, response);
    }

    // Thêm vào giỏ hàng với giữ ghế
    private void addToCart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();

        try {
            // Lấy thông tin từ request
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String ticketType = request.getParameter("ticketType");
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            String seats = request.getParameter("seats");

            if (seats == null || seats.trim().isEmpty()) {
                sendErrorResponse(out, "Vui lòng chọn ghế");
                return;
            }

            // Lấy thông tin phim và suất chiếu
            Movie movie = cartService.getMovieInfo(movieId);
            Showtime showtime = cartService.getShowtimeInfo(showtimeId);
            Room room = cartService.getRoomInfo(roomId);

            if (movie == null || showtime == null || room == null) {
                sendErrorResponse(out, "Thông tin không hợp lệ");
                return;
            }

            // Kiểm tra ghế có còn trống không
            String[] seatArray = seats.split(", ");
            List<String> seatCodes = new ArrayList<>();
            for (String seatCode : seatArray) {
                if (!seatCode.trim().isEmpty()) {
                    seatCodes.add(seatCode.trim());
                }
            }

            // Validate ghế còn trống
            boolean seatsAvailable = bookingService.validateSeatsAvailable(
                    showtimeId, roomId, seatCodes);

            if (!seatsAvailable) {
                sendErrorResponse(out, "Một số ghế đã được đặt. Vui lòng chọn ghế khác.");
                return;
            }

            // Lấy userId nếu đã đăng nhập
            Integer userId = null;
            User user = (User) session.getAttribute("user");
            if (user != null) {
                userId = user.getId();

                // Kiểm tra số ghế tối đa
                if (!bookingService.validateMaxSeats(showtimeId, userId, seatCodes.size())) {
                    sendErrorResponse(out, "Bạn đã giữ quá nhiều ghế. Tối đa 10 ghế mỗi người.");
                    return;
                }
            }

            // Giữ ghế tạm thời trước khi thêm vào giỏ hàng
            Map<String, Object> reservationResult = bookingService.reserveSeats(
                    showtimeId, roomId, seatCodes, userId);

            if (!(Boolean) reservationResult.get("success")) {
                sendErrorResponse(out, (String) reservationResult.get("message"));
                return;
            }

            String reservationId = (String) reservationResult.get("reservationId");

            // Lưu reservationId vào session để có thể hủy sau
            List<String> reservations = (List<String>) session.getAttribute("reservations");
            if (reservations == null) {
                reservations = new ArrayList<>();
                session.setAttribute("reservations", reservations);
            }
            reservations.add(reservationId);

            // Tạo cart item với reservationId
            CartItem cartItem = new CartItem();
            cartItem.setId(UUID.randomUUID().toString());
            cartItem.setMovieId(movieId);
            cartItem.setMovieTitle(movie.getTitle());
            cartItem.setPosterUrl(movie.getPosterUrl());
            cartItem.setShowtimeId(showtimeId);
            cartItem.setShowtime(showtime.getFormattedDateTime());
            cartItem.setRoomId(roomId);
            cartItem.setRoom(room.getRoomName());
            cartItem.setTicketType(ticketType);
            cartItem.setQuantity(quantity);
            cartItem.setSeats(seats);
            cartItem.setUnitPrice(cartService.calculateTicketPrice(ticketType));
            cartItem.setTotal(cartItem.getUnitPrice() * quantity);
            cartItem.setReservationId(reservationId);

            // Thêm vào giỏ hàng
            boolean success = cartService.addToCart(session, cartItem);

            if (success) {
                ShoppingCart cart = cartService.getOrCreateCart(session);

                // Tạo JSON response thủ công
                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append("\"success\": true,");
                json.append("\"message\": \"Đã thêm vào giỏ hàng\",");
                json.append("\"cartItemCount\": ").append(cart.getTotalItems()).append(",");
                json.append("\"cartTotal\": ").append(cart.getGrandTotal()).append(",");
                json.append("\"reservationId\": \"").append(reservationId).append("\",");
                json.append("\"reservationTime\": ").append(reservationResult.get("reservationTime"));
                json.append("}");

                out.print(json.toString());
            } else {
                // Nếu thêm vào giỏ hàng thất bại, hủy giữ ghế
                bookingService.releaseSeats(reservationId);
                sendErrorResponse(out, "Không thể thêm vào giỏ hàng");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(out, "Dữ liệu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    // Cập nhật số lượng
    private void updateCartItem(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();

        try {
            String itemId = request.getParameter("itemId");
            int newQuantity = Integer.parseInt(request.getParameter("quantity"));

            // Lấy cart item hiện tại
            ShoppingCart cart = cartService.getOrCreateCart(session);
            CartItem currentItem = null;
            for (CartItem item : cart.getItems()) {
                if (item.getId().equals(itemId)) {
                    currentItem = item;
                    break;
                }
            }

            if (currentItem == null) {
                sendErrorResponse(out, "Không tìm thấy vé");
                return;
            }

            // Kiểm tra nếu số lượng ghế thay đổi
            if (newQuantity != currentItem.getQuantity()) {
                String[] currentSeats = currentItem.getSeats().split(", ");

                if (newQuantity > currentSeats.length) {
                    sendErrorResponse(out, "Không thể tăng số lượng vượt quá số ghế đã chọn");
                    return;
                }

                if (newQuantity < currentSeats.length) {
                    // Giảm số lượng ghế
                    String[] newSeatsArray = Arrays.copyOf(currentSeats, newQuantity);
                    String newSeats = String.join(", ", newSeatsArray);

                    // Cập nhật ghế trong cart item
                    currentItem.setSeats(newSeats);
                }
            }

            // Cập nhật số lượng
            boolean success = cartService.updateQuantity(session, itemId, newQuantity);

            if (success) {
                cart = cartService.getOrCreateCart(session);

                // Tìm item để lấy tổng tiền của item
                double itemTotal = 0;
                for (CartItem item : cart.getItems()) {
                    if (item.getId().equals(itemId)) {
                        itemTotal = item.getTotal();
                        break;
                    }
                }

                // Tạo JSON response thủ công
                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append("\"success\": true,");
                json.append("\"cartItemCount\": ").append(cart.getTotalItems()).append(",");
                json.append("\"cartTotal\": ").append(cart.getGrandTotal()).append(",");
                json.append("\"itemTotal\": ").append(itemTotal).append(",");
                json.append("\"subtotal\": ").append(cart.getSubtotal()).append(",");
                json.append("\"serviceFee\": ").append(cart.getServiceFee()).append(",");
                json.append("\"grandTotal\": ").append(cart.getGrandTotal());
                json.append("}");

                out.print(json.toString());
            } else {
                sendErrorResponse(out, "Không thể cập nhật số lượng");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Có lỗi xảy ra");
        }
    }

    // Xóa item khỏi giỏ hàng (hủy giữ ghế)
    private void removeCartItem(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession();
        String itemId = request.getParameter("id");

        if (itemId != null) {
            // Lấy thông tin item trước khi xóa
            ShoppingCart cart = cartService.getOrCreateCart(session);
            CartItem itemToRemove = null;
            for (CartItem item : cart.getItems()) {
                if (item.getId().equals(itemId)) {
                    itemToRemove = item;
                    break;
                }
            }

            if (itemToRemove != null) {
                // Hủy giữ ghế nếu có reservationId
                if (itemToRemove.getReservationId() != null) {
                    bookingService.releaseSeats(itemToRemove.getReservationId());

                    // Xóa reservationId khỏi session
                    List<String> reservations = (List<String>) session.getAttribute("reservations");
                    if (reservations != null) {
                        reservations.remove(itemToRemove.getReservationId());
                    }
                }

                // Xóa khỏi giỏ hàng
                boolean success = cartService.removeItem(session, itemId);

                if (success) {
                    // Nếu là AJAX request
                    if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");

                        cart = cartService.getOrCreateCart(session);

                        // Tạo JSON response thủ công
                        StringBuilder json = new StringBuilder();
                        json.append("{");
                        json.append("\"success\": true,");
                        json.append("\"cartItemCount\": ").append(cart.getTotalItems()).append(",");
                        json.append("\"cartTotal\": ").append(cart.getGrandTotal());
                        json.append("}");

                        response.getWriter().print(json.toString());
                    } else {
                        // Redirect về trang giỏ hàng
                        response.sendRedirect(request.getContextPath() + "/cart");
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Không tìm thấy vé");
                }
            }
        }
    }

    // Xóa toàn bộ giỏ hàng (hủy tất cả giữ ghế)
    private void clearCart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        ShoppingCart cart = cartService.getOrCreateCart(session);

        // Hủy tất cả reservations
        List<String> reservations = (List<String>) session.getAttribute("reservations");
        if (reservations != null) {
            for (String reservationId : reservations) {
                bookingService.releaseSeats(reservationId);
            }
            reservations.clear();
        }

        // Xóa tất cả reservations trong cart items
        for (CartItem item : cart.getItems()) {
            if (item.getReservationId() != null) {
                bookingService.releaseSeats(item.getReservationId());
            }
        }

        // Xóa giỏ hàng
        cartService.clearCart(session);

        response.sendRedirect(request.getContextPath() + "/cart");
    }

    // Áp dụng mã khuyến mãi
    private void applyPromoCode(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String promoCode = request.getParameter("promoCode");
            HttpSession session = request.getSession();

            if (promoCode == null || promoCode.trim().isEmpty()) {
                sendErrorResponse(out, "Vui lòng nhập mã khuyến mãi");
                return;
            }

            // Áp dụng mã khuyến mãi
            Map<String, Object> result = cartService.applyPromoCode(session, promoCode.trim());

            // Chuyển Map thành JSON string
            StringBuilder json = new StringBuilder();
            json.append("{");
            boolean first = true;
            for (Map.Entry<String, Object> entry : result.entrySet()) {
                if (!first) {
                    json.append(",");
                }
                first = false;

                json.append("\"").append(entry.getKey()).append("\": ");

                Object value = entry.getValue();
                if (value instanceof String) {
                    json.append("\"").append(escapeJsonString(value.toString())).append("\"");
                } else if (value instanceof Boolean) {
                    json.append(value);
                } else {
                    json.append(value);
                }
            }
            json.append("}");

            out.print(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Có lỗi xảy ra");
        }
    }

    private void sendErrorResponse(PrintWriter out, String message) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"success\": false,");
        json.append("\"message\": \"").append(escapeJsonString(message)).append("\"");
        json.append("}");
        out.print(json.toString());
    }

    // Helper method để escape JSON string
    private String escapeJsonString(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}