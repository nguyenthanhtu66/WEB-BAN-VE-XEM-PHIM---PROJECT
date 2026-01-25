package vn.edu.hcmuaf.fit.demo1.controller;

import vn.edu.hcmuaf.fit.demo1.service.CartService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "CartController", urlPatterns = {
        "/cart",
        "/cart/add",
        "/cart/update",
        "/cart/remove",
        "/cart/clear",
        "/cart/apply-promo",
        "/cart/update-seats"
})
public class CartController extends HttpServlet {

    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/cart".equals(path)) {
            // Hiển thị trang giỏ hàng
            showCartPage(request, response);
        } else if ("/cart/remove".equals(path)) {
            // Xử lý xóa item (có thể dùng GET cho link xóa)
            handleRemoveFromCart(request, response);
        } else if ("/cart/clear".equals(path)) {
            // Xóa toàn bộ giỏ hàng
            handleClearCart(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {
            case "/cart/add":
                handleAddToCart(request, response);
                break;

            case "/cart/update":
                handleUpdateCart(request, response);
                break;

            case "/cart/apply-promo":
                handleApplyPromo(request, response);
                break;

            case "/cart/update-seats":
                handleUpdateSeats(request, response);
                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showCartPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Đã có giỏ hàng trong session, chỉ cần forward đến JSP
        request.getRequestDispatcher("/Gio-hang.jsp").forward(request, response);
    }

    private void handleAddToCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) userId = 0;

        try {
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String ticketType = request.getParameter("ticketType");
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            // Parse seats
            String seatsParam = request.getParameter("seats");
            List<String> seatCodes = null;
            if (seatsParam != null && !seatsParam.trim().isEmpty()) {
                seatCodes = Arrays.stream(seatsParam.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }

            var result = cartService.addToCart(session, movieId, showtimeId, roomId,
                    ticketType, quantity, seatCodes, userId);

            if (Boolean.TRUE.equals(result.get("success"))) {
                // Thành công, redirect về trang trước đó hoặc giỏ hàng
                String redirectUrl = request.getParameter("redirectUrl");
                if (redirectUrl != null && !redirectUrl.isEmpty()) {
                    response.sendRedirect(redirectUrl);
                } else {
                    response.sendRedirect(request.getContextPath() + "/cart");
                }
            } else {
                // Thất bại, hiển thị thông báo lỗi
                request.setAttribute("error", result.get("message"));
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Tham số không hợp lệ");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleUpdateCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) userId = 0;

        try {
            String itemId = request.getParameter("itemId");
            int newQuantity = Integer.parseInt(request.getParameter("quantity"));

            var result = cartService.updateQuantity(session, itemId, newQuantity);

            if (Boolean.TRUE.equals(result.get("success"))) {
                // Cập nhật thành công, reload trang giỏ hàng
                response.sendRedirect(request.getContextPath() + "/cart");
            } else {
                request.setAttribute("error", result.get("message"));
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Số lượng không hợp lệ");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleRemoveFromCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) userId = 0;

        String itemId = request.getParameter("id");

        if (itemId == null || itemId.trim().isEmpty()) {
            request.setAttribute("error", "Thiếu thông tin itemId");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        try {
            var result = cartService.removeFromCart(session, itemId, userId);

            if (Boolean.TRUE.equals(result.get("success"))) {
                // Xóa thành công, redirect về giỏ hàng
                response.sendRedirect(request.getContextPath() + "/cart");
            } else {
                request.setAttribute("error", result.get("message"));
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleClearCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) userId = 0;

        try {
            var result = cartService.clearCart(session, userId);

            if (Boolean.TRUE.equals(result.get("success"))) {
                // Xóa thành công, redirect về trang chủ
                response.sendRedirect(request.getContextPath() + "/home");
            } else {
                request.setAttribute("error", result.get("message"));
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleApplyPromo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String promoCode = request.getParameter("code");

        if (promoCode == null || promoCode.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mã khuyến mãi");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        try {
            var result = cartService.applyPromoCode(session, promoCode);

            if (Boolean.TRUE.equals(result.get("success"))) {
                // Áp dụng thành công, redirect về giỏ hàng
                response.sendRedirect(request.getContextPath() + "/cart");
            } else {
                // Lưu thông báo lỗi vào session để hiển thị trên trang giỏ hàng
                session.setAttribute("promoMessage", result.get("message"));
                session.setAttribute("promoMessageType", "error");
                response.sendRedirect(request.getContextPath() + "/cart");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleUpdateSeats(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) userId = 0;

        try {
            String itemId = request.getParameter("itemId");
            String seatsParam = request.getParameter("seats");

            List<String> seatCodes = null;
            if (seatsParam != null && !seatsParam.trim().isEmpty()) {
                seatCodes = Arrays.stream(seatsParam.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }

            var result = cartService.updateSeats(session, itemId, seatCodes, userId);

            if (Boolean.TRUE.equals(result.get("success"))) {
                // Cập nhật thành công, redirect về giỏ hàng
                response.sendRedirect(request.getContextPath() + "/cart");
            } else {
                request.setAttribute("error", result.get("message"));
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}