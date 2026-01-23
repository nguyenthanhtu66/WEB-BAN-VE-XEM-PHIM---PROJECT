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

@WebServlet(name = "BookingController", urlPatterns = {
        "/booking/reserve-seats",
        "/booking/release-seats",
        "/booking/check-seat-status",
        "/booking/quick-booking"
})
public class BookingController extends HttpServlet {

    private final BookingService bookingService = new BookingService();
    private final SeatService seatService = new SeatService();
    private final ShowtimeService showtimeService = new ShowtimeService();
    private final RoomService roomService = new RoomService();
    private final MovieService movieService = new MovieService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/booking/reserve-seats".equals(path)) {
            reserveSeats(request, response);
        } else if ("/booking/release-seats".equals(path)) {
            releaseSeats(request, response);
        } else if ("/booking/quick-booking".equals(path)) {
            handleQuickBooking(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/booking/check-seat-status".equals(path)) {
            checkSeatStatus(request, response);
        }
    }

    // Giữ ghế tạm thời (5 phút)
    private void reserveSeats(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Lấy thông tin từ request
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String seatCodesStr = request.getParameter("seatCodes");

            if (seatCodesStr == null || seatCodesStr.trim().isEmpty()) {
                sendErrorResponse(out, "Vui lòng chọn ghế");
                return;
            }

            String[] seatCodesArray = seatCodesStr.split(",");
            List<String> seatCodes = new ArrayList<>();
            for (String code : seatCodesArray) {
                if (!code.trim().isEmpty()) {
                    seatCodes.add(code.trim());
                }
            }

            if (seatCodes.isEmpty()) {
                sendErrorResponse(out, "Vui lòng chọn ghế");
                return;
            }

            HttpSession session = request.getSession();
            Integer userId = null;

            // Lấy userId nếu đã đăng nhập
            User user = (User) session.getAttribute("user");
            if (user != null) {
                userId = user.getId();
            }

            // Gọi service để giữ ghế
            Map<String, Object> reservationResult = bookingService.reserveSeats(
                    showtimeId, roomId, seatCodes, userId);

            if ((Boolean) reservationResult.get("success")) {
                // Lưu reservationId vào session
                String reservationId = (String) reservationResult.get("reservationId");
                List<String> reservations = (List<String>) session.getAttribute("reservations");
                if (reservations == null) {
                    reservations = new ArrayList<>();
                    session.setAttribute("reservations", reservations);
                }
                reservations.add(reservationId);

                // Tạo JSON response thủ công
                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append("\"success\": true,");
                json.append("\"reservationId\": \"").append(reservationId).append("\",");
                json.append("\"seatCount\": ").append(reservationResult.get("seatCount")).append(",");
                json.append("\"reservedUntil\": \"").append(reservationResult.get("reservedUntil")).append("\",");
                json.append("\"reservationTime\": ").append(reservationResult.get("reservationTime"));
                json.append("}");

                out.print(json.toString());
            } else {
                sendErrorResponse(out, (String) reservationResult.get("message"));
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(out, "Dữ liệu không hợp lệ");
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

        try {
            String reservationId = request.getParameter("reservationId");

            if (reservationId != null && !reservationId.isEmpty()) {
                boolean success = bookingService.releaseSeats(reservationId);

                // Tạo JSON response thủ công
                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append("\"success\": ").append(success).append(",");
                json.append("\"message\": \"").append(success ? "Đã hủy giữ ghế" : "Không tìm thấy đặt chỗ").append("\"");
                json.append("}");

                out.print(json.toString());
            } else {
                sendErrorResponse(out, "Thiếu thông tin đặt chỗ");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Có lỗi xảy ra");
        }
    }

    // Kiểm tra trạng thái ghế
    private void checkSeatStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));

            // Lấy trạng thái của tất cả ghế
            Map<String, String> seatStatusMap = bookingService.getAllSeatStatus(showtimeId, roomId);

            // Tạo JSON response thủ công
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"success\": true,");
            json.append("\"seatStatus\": {");

            boolean first = true;
            for (Map.Entry<String, String> entry : seatStatusMap.entrySet()) {
                if (!first) {
                    json.append(",");
                }
                first = false;

                json.append("\"").append(entry.getKey()).append("\": ");
                json.append("\"").append(entry.getValue()).append("\"");
            }

            json.append("}}");

            out.print(json.toString());

        } catch (NumberFormatException e) {
            sendErrorResponse(out, "Dữ liệu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Có lỗi xảy ra");
        }
    }

    // Xử lý đặt vé nhanh
    private void handleQuickBooking(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String showtimeParam = request.getParameter("showtime");

            // Lấy thông tin phim
            Movie movie = movieService.getMovieById(movieId);
            if (movie == null) {
                sendErrorResponse(out, "Phim không tồn tại");
                return;
            }

            // Lấy thông tin phòng
            Room room = roomService.getRoomById(roomId);
            if (room == null) {
                sendErrorResponse(out, "Phòng không tồn tại");
                return;
            }

            // Tìm hoặc tạo suất chiếu dựa trên thời gian
            Showtime showtime = showtimeService.findOrCreateShowtime(movieId, roomId, showtimeParam);
            if (showtime == null) {
                sendErrorResponse(out, "Không thể tạo suất chiếu");
                return;
            }

            // Tạo JSON response thủ công
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"success\": true,");
            json.append("\"movieId\": ").append(movieId).append(",");
            json.append("\"movieTitle\": \"").append(escapeJsonString(movie.getTitle())).append("\",");
            json.append("\"moviePoster\": \"").append(escapeJsonString(movie.getPosterUrl())).append("\",");
            json.append("\"roomId\": ").append(roomId).append(",");
            json.append("\"roomName\": \"").append(escapeJsonString(room.getRoomName())).append("\",");
            json.append("\"showtimeId\": ").append(showtime.getId()).append(",");
            json.append("\"showtime\": \"").append(escapeJsonString(showtime.getFormattedDateTime())).append("\"");
            json.append("}");

            out.print(json.toString());

        } catch (NumberFormatException e) {
            sendErrorResponse(out, "Dữ liệu không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Có lỗi xảy ra: " + e.getMessage());
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