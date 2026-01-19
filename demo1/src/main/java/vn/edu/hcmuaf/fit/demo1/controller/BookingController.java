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

@WebServlet(name = "BookingController", urlPatterns = {
        "/booking/check-seats",
        "/booking/reserve-seats",
        "/booking/release-seats",
        "/booking/get-seat-map"
})
public class BookingController extends HttpServlet {

    private final BookingService bookingService = new BookingService();
    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/booking/check-seats".equals(path)) {
            checkSeatAvailability(request, response);
        } else if ("/booking/get-seat-map".equals(path)) {
            getSeatMap(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/booking/reserve-seats".equals(path)) {
            reserveSeats(request, response);
        } else if ("/booking/release-seats".equals(path)) {
            releaseSeats(request, response);
        }
    }

    // Kiểm tra ghế có sẵn không
    private void checkSeatAvailability(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String seatCode = request.getParameter("seatCode");

            boolean isAvailable = bookingService.checkSeatAvailability(showtimeId, roomId, seatCode);

            Map<String, Object> result = new HashMap<>();
            result.put("available", isAvailable);
            result.put("seatCode", seatCode);

            out.print(JsonUtils.toJson(result));

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Có lỗi xảy ra");
        }
    }

    // Lấy sơ đồ ghế
    private void getSeatMap(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));

            Map<String, Object> seatMap = bookingService.getSeatMap(showtimeId, roomId);

            if (seatMap != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("seatMap", seatMap);
                out.print(JsonUtils.toJson(result));
            } else {
                sendErrorResponse(out, "Không tìm thấy thông tin phòng");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Có lỗi xảy ra");
        }
    }

    // Giữ ghế tạm thời (khi mở modal đặt vé)
    private void reserveSeats(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();

        try {
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String seatCodesParam = request.getParameter("seatCodes");

            if (seatCodesParam == null || seatCodesParam.trim().isEmpty()) {
                sendErrorResponse(out, "Vui lòng chọn ghế");
                return;
            }

            String[] seatCodes = seatCodesParam.split(",");

            // Lấy userId từ session
            User user = (User) session.getAttribute("user");
            int userId = user != null ? user.getId() : 0;

            // Giữ ghế trong 5 phút
            boolean success = bookingService.reserveSeatsTemporarily(
                    showtimeId, roomId, seatCodes, userId);

            if (success) {
                // Lưu thông tin giữ ghế vào session
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> reservations =
                        (List<Map<String, Object>>) session.getAttribute("seatReservations");

                if (reservations == null) {
                    reservations = new ArrayList<>();
                }

                Map<String, Object> reservation = new HashMap<>();
                reservation.put("showtimeId", showtimeId);
                reservation.put("roomId", roomId);
                reservation.put("seatCodes", Arrays.asList(seatCodes));
                reservation.put("reservedAt", System.currentTimeMillis());

                reservations.add(reservation);
                session.setAttribute("seatReservations", reservations);

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "Đã giữ ghế thành công. Bạn có 5 phút để hoàn tất đặt vé.");
                result.put("reservationTime", 5 * 60); // 5 phút tính bằng giây

                out.print(JsonUtils.toJson(result));
            } else {
                sendErrorResponse(out, "Không thể giữ ghế. Vui lòng chọn ghế khác.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    // Hủy giữ ghế
    private void releaseSeats(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();

        try {
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            String seatCodesParam = request.getParameter("seatCodes");

            if (seatCodesParam != null) {
                String[] seatCodes = seatCodesParam.split(",");
                bookingService.releaseSeats(showtimeId, seatCodes);
            }

            // Xóa khỏi session
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> reservations =
                    (List<Map<String, Object>>) session.getAttribute("seatReservations");

            if (reservations != null) {
                reservations.removeIf(res -> {
                    Integer storedShowtimeId = (Integer) res.get("showtimeId");
                    return storedShowtimeId != null && storedShowtimeId == showtimeId;
                });
                session.setAttribute("seatReservations", reservations);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đã hủy giữ ghế");

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