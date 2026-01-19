package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.model.*;
import vn.edu.hcmuaf.fit.demo1.service.*;
import vn.edu.hcmuaf.fit.demo1.util.JsonUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(name = "CartController", urlPatterns = {
        "/cart",
        "/cart/add",
        "/cart/update",
        "/cart/remove",
        "/cart/clear"
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

    // Thêm vào giỏ hàng
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

            // Tạo cart item
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

            // Thêm vào giỏ hàng
            boolean success = cartService.addToCart(session, cartItem);

            if (success) {
                ShoppingCart cart = cartService.getOrCreateCart(session);

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "Đã thêm vào giỏ hàng");
                result.put("cartItemCount", cart.getTotalItems());
                result.put("cartTotal", cart.getGrandTotal());

                out.print(JsonUtils.toJson(result));
            } else {
                sendErrorResponse(out, "Không thể thêm vào giỏ hàng. Ghế có thể đã được đặt.");
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

            boolean success = cartService.updateQuantity(session, itemId, newQuantity);

            if (success) {
                ShoppingCart cart = cartService.getOrCreateCart(session);

                // Tìm item để lấy tổng tiền của item
                double itemTotal = 0;
                for (CartItem item : cart.getItems()) {
                    if (item.getId().equals(itemId)) {
                        itemTotal = item.getTotal();
                        break;
                    }
                }

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("cartItemCount", cart.getTotalItems());
                result.put("cartTotal", cart.getGrandTotal());
                result.put("itemTotal", itemTotal);
                result.put("subtotal", cart.getSubtotal());
                result.put("serviceFee", cart.getServiceFee());
                result.put("grandTotal", cart.getGrandTotal());

                out.print(JsonUtils.toJson(result));
            } else {
                sendErrorResponse(out, "Không thể cập nhật số lượng");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Có lỗi xảy ra");
        }
    }

    // Xóa item khỏi giỏ hàng
    private void removeCartItem(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession();
        String itemId = request.getParameter("id");

        if (itemId != null) {
            boolean success = cartService.removeItem(session, itemId);

            if (success) {
                // Nếu là AJAX request
                if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    ShoppingCart cart = cartService.getOrCreateCart(session);

                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("cartItemCount", cart.getTotalItems());
                    result.put("cartTotal", cart.getGrandTotal());

                    response.getWriter().print(JsonUtils.toJson(result));
                } else {
                    // Redirect về trang giỏ hàng
                    response.sendRedirect(request.getContextPath() + "/cart");
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Không tìm thấy vé");
            }
        }
    }

    // Xóa toàn bộ giỏ hàng
    private void clearCart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        cartService.clearCart(session);

        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private void sendErrorResponse(PrintWriter out, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        out.print(JsonUtils.toJson(error));
    }
}