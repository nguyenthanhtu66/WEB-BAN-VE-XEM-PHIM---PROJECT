package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.service.*;
import vn.edu.hcmuaf.fit.demo1.util.JsonUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(name = "ModalBookingController", urlPatterns = {
        "/modal/booking-info",
        "/modal/add-to-cart-from-modal"
})
public class ModalBookingController extends HttpServlet {

    private final BookingService bookingService = new BookingService();
    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/modal/booking-info".equals(path)) {
            getBookingInfo(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/modal/add-to-cart-from-modal".equals(path)) {
            addToCartFromModal(request, response);
        }
    }

    // Lấy thông tin đặt vé cho modal
    private void getBookingInfo(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));

            // Lấy thông tin chi tiết
            Map<String, Object> showtimeDetails = bookingService.getShowtimeDetails(showtimeId);

            if (showtimeDetails != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("showtimeDetails", showtimeDetails);

                out.print(JsonUtils.toJson(result));
            } else {
                sendErrorResponse(out, "Không tìm thấy thông tin suất chiếu");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Có lỗi xảy ra");
        }
    }

    // Thêm vào giỏ hàng từ modal
    private void addToCartFromModal(HttpServletRequest request, HttpServletResponse response)
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

            // Thực hiện thêm vào giỏ hàng thông qua CartService
            // (giống như trong CartController)

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đã thêm vào giỏ hàng từ modal");
            result.put("redirectUrl", request.getContextPath() + "/cart");

            out.print(JsonUtils.toJson(result));

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Có lỗi xảy ra");
        }
    }

    private void sendErrorResponse(PrintWriter out, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        out.print(JsonUtils.toJson(error));
    }
}