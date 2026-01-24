package vn.edu.hcmuaf.fit.demo1.controller;

import vn.edu.hcmuaf.fit.demo1.service.BookingService;
import vn.edu.hcmuaf.fit.demo1.service.CartService;
import vn.edu.hcmuaf.fit.demo1.dao.*;
import vn.edu.hcmuaf.fit.demo1.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet(name = "BookingController", urlPatterns = {
        "/booking",
        "/booking/reserve-seats",
        "/booking/confirm",
        "/booking/cancel",
        "/booking/check-seats",
        "/booking/quick-booking"
})
public class BookingController extends HttpServlet {

    private final BookingService bookingService = new BookingService();
    private final CartService cartService = new CartService();
    private final MovieDao movieDao = new MovieDao();
    private final RoomDao roomDao = new RoomDao();
    private final ShowtimeDao showtimeDao = new ShowtimeDao();
    private final SeatDao seatDao = new SeatDao();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String action = request.getParameter("action");

        if ("/booking".equals(path)) {
            // Hiển thị trang booking
            showBookingPage(request, response);
        } else if ("/booking/check-seats".equals(path)) {
            // Kiểm tra trạng thái ghế
            checkSeatStatus(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        HttpSession session = request.getSession();

        // Lấy userId từ session
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            userId = 0; // Guest user
        }

        String sessionId = session.getId();

        try {
            switch (path) {
                case "/booking/reserve-seats":
                    reserveSeats(request, response, session, userId, sessionId);
                    break;

                case "/booking/confirm":
                    confirmBooking(request, response, session, userId);
                    break;

                case "/booking/cancel":
                    cancelReservation(request, response, userId);
                    break;

                case "/booking/quick-booking":
                    processQuickBooking(request, response, session, userId);
                    break;

                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void showBookingPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String showtimeStr = request.getParameter("showtime");

            // Lấy thông tin phim
            Movie movie = movieDao.getMovieById(movieId);
            if (movie == null) {
                request.setAttribute("error", "Phim không tồn tại");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // Lấy thông tin phòng
            Room room = roomDao.getRoomById(roomId);
            if (room == null) {
                request.setAttribute("error", "Phòng chiếu không tồn tại");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // Parse showtime
            String[] parts = showtimeStr.split("T");
            LocalDate showDate = LocalDate.parse(parts[0]);
            LocalTime showTime = LocalTime.parse(parts[1]);

            // Tìm hoặc tạo showtime
            Integer showtimeId = showtimeDao.createShowtime(movieId, roomId, showDate, showTime);
            if (showtimeId == null || showtimeId <= 0) {
                request.setAttribute("error", "Không thể tạo suất chiếu");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // Lấy danh sách ghế của phòng
            List<Seat> seats = seatDao.getSeatsByRoom(roomId);

            // Lấy ghế đã được đặt/giữ
            List<String> bookedSeats = seatDao.getUnavailableSeatCodes(showtimeId,
                    seats.stream().map(Seat::getId).collect(java.util.stream.Collectors.toList()));

            // Chuẩn bị dữ liệu cho view
            request.setAttribute("movie", movie);
            request.setAttribute("room", room);
            request.setAttribute("showtimeId", showtimeId);
            request.setAttribute("showtimeStr", showtimeStr);
            request.setAttribute("seats", seats);
            request.setAttribute("bookedSeats", bookedSeats);
            request.setAttribute("showDate", showDate.format(DATE_FORMATTER));
            request.setAttribute("showTime", showTime.format(TIME_FORMATTER));

            // Forward đến trang booking
            request.getRequestDispatcher("/booking.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void reserveSeats(HttpServletRequest request, HttpServletResponse response,
                              HttpSession session, int userId, String sessionId)
            throws ServletException, IOException {

        try {
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String[] seatCodes = request.getParameterValues("seatCodes");

            if (seatCodes == null || seatCodes.length == 0) {
                request.setAttribute("error", "Vui lòng chọn ít nhất một ghế");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            List<String> seatCodeList = Arrays.asList(seatCodes);

            // Gọi service để giữ ghế
            Map<String, Object> result = bookingService.reserveSeats(
                    showtimeId, seatCodeList, roomId, userId, sessionId);

            if (Boolean.TRUE.equals(result.get("success"))) {
                // Lưu reservation vào session
                session.setAttribute("reservationId", result.get("reservationId"));
                session.setAttribute("reservedSeats", seatCodeList);
                session.setAttribute("reservedShowtimeId", showtimeId);
                session.setAttribute("reservedRoomId", roomId);
                session.setAttribute("reservationExpiry", result.get("expiryTime"));

                // Redirect đến trang xác nhận hoặc trở lại
                String redirectUrl = request.getParameter("redirectUrl");
                if (redirectUrl != null && !redirectUrl.isEmpty()) {
                    response.sendRedirect(redirectUrl);
                } else {
                    response.sendRedirect(request.getContextPath() + "/booking-confirmation.jsp");
                }
            } else {
                request.setAttribute("error", result.get("message"));
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void confirmBooking(HttpServletRequest request, HttpServletResponse response,
                                HttpSession session, int userId)
            throws ServletException, IOException {

        try {
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String[] seatCodes = request.getParameterValues("seatCodes");
            String ticketType = request.getParameter("ticketType");
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            int movieId = Integer.parseInt(request.getParameter("movieId"));

            if (seatCodes == null || seatCodes.length == 0) {
                request.setAttribute("error", "Vui lòng chọn ghế");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            List<String> seatCodeList = Arrays.asList(seatCodes);

            // Generate order ID (tạm thời)
            int orderId = (int) (System.currentTimeMillis() % 1000000);

            // Xác nhận booking
            Map<String, Object> result = bookingService.confirmBooking(
                    showtimeId, seatCodeList, roomId, userId, orderId);

            if (Boolean.TRUE.equals(result.get("success"))) {
                // Thêm vào giỏ hàng
                Map<String, Object> cartResult = cartService.addToCart(
                        session, movieId, showtimeId, roomId, ticketType,
                        quantity, seatCodeList, userId);

                if (Boolean.TRUE.equals(cartResult.get("success"))) {
                    // Xóa reservation khỏi session
                    session.removeAttribute("reservationId");
                    session.removeAttribute("reservedSeats");

                    // Redirect đến trang thành công hoặc giỏ hàng
                    response.sendRedirect(request.getContextPath() + "/cart");
                } else {
                    request.setAttribute("error", cartResult.get("message"));
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("error", result.get("message"));
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void cancelReservation(HttpServletRequest request, HttpServletResponse response,
                                   int userId) throws ServletException, IOException {

        try {
            String reservationId = request.getParameter("reservationId");

            if (reservationId == null || reservationId.trim().isEmpty()) {
                request.setAttribute("error", "Thiếu reservationId");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            Map<String, Object> result = bookingService.cancelReservation(reservationId, userId);

            if (Boolean.TRUE.equals(result.get("success"))) {
                response.sendRedirect(request.getContextPath() + "/home");
            } else {
                request.setAttribute("error", result.get("message"));
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void checkSeatStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));

            Map<String, Object> result = bookingService.checkSeatStatus(showtimeId, roomId);

            if (Boolean.TRUE.equals(result.get("success"))) {
                request.setAttribute("seatsInfo", result.get("seats"));
                request.getRequestDispatcher("/seat-status.jsp").forward(request, response);
            } else {
                request.setAttribute("error", result.get("message"));
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void processQuickBooking(HttpServletRequest request, HttpServletResponse response,
                                     HttpSession session, int userId)
            throws ServletException, IOException {

        try {
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String showtimeStr = request.getParameter("showtime");

            // Xử lý quick booking
            Map<String, Object> result = cartService.processQuickBooking(
                    movieId, roomId, showtimeStr, userId);

            if (Boolean.TRUE.equals(result.get("success"))) {
                // Lưu thông tin vào session để sử dụng trong modal
                session.setAttribute("quickBookingMovieId", movieId);
                session.setAttribute("quickBookingRoomId", roomId);
                session.setAttribute("quickBookingShowtimeId", result.get("showtimeId"));
                session.setAttribute("quickBookingShowtime", result.get("showtime"));

                // Redirect trở lại trang chủ với thông báo thành công
                request.setAttribute("quickBookingSuccess", true);
                request.setAttribute("quickBookingMessage", "Sẵn sàng đặt vé!");
                request.setAttribute("quickBookingData", result);

                // Forward đến trang chủ
                request.getRequestDispatcher("/home").forward(request, response);
            } else {
                request.setAttribute("error", result.get("message"));
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        bookingService.shutdown();
        super.destroy();
    }
}