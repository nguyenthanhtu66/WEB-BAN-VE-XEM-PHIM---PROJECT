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
import java.io.PrintWriter;
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
        "/booking/quick-booking",
        "/booking/get-showtimes",
        "/booking/get-seat-map",
        "/booking/check-seat-availability"
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

        try {
            if ("/booking".equals(path)) {
                showBookingPage(request, response);
            } else if ("/booking/check-seats".equals(path)) {
                checkSeatStatus(request, response);
            } else if ("/booking/get-showtimes".equals(path)) {
                handleGetShowtimes(request, response);
            } else if ("/booking/get-seat-map".equals(path)) {
                handleGetSeatMap(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Lỗi hệ thống");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        HttpSession session = request.getSession();

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) userId = 0;

        try {
            if ("/booking/reserve-seats".equals(path)) {
                reserveSeats(request, response, session, userId);
            } else if ("/booking/confirm".equals(path)) {
                confirmBooking(request, response, session, userId);
            } else if ("/booking/cancel".equals(path)) {
                cancelReservation(request, response, userId);
            } else if ("/booking/quick-booking".equals(path)) {
                processQuickBooking(request, response, session, userId);
            } else if ("/booking/check-seat-availability".equals(path)) {
                checkSeatAvailability(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Lỗi hệ thống");
        }
    }

    // ==================== MAIN METHODS ====================

    private void showBookingPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String showtimeStr = request.getParameter("showtime");

            // Validate movie
            Movie movie = movieDao.getMovieById(movieId);
            if (movie == null) {
                forwardError(request, response, "Phim không tồn tại");
                return;
            }

            // Validate room
            Room room = roomDao.getRoomById(roomId);
            if (room == null) {
                forwardError(request, response, "Phòng chiếu không tồn tại");
                return;
            }

            // Parse showtime
            String[] parts = showtimeStr.split("T");
            LocalDate showDate = LocalDate.parse(parts[0]);
            LocalTime showTime = LocalTime.parse(parts[1]);

            // Create or get showtime
            Integer showtimeId = showtimeDao.createShowtime(movieId, roomId, showDate, showTime);
            if (showtimeId == null || showtimeId <= 0) {
                forwardError(request, response, "Không thể tạo suất chiếu");
                return;
            }

            // Get seats
            List<Seat> seats = seatDao.getSeatsByRoom(roomId);
            List<Integer> seatIds = new ArrayList<>();
            for (Seat seat : seats) {
                seatIds.add(seat.getId());
            }

            // Get booked seats
            List<String> bookedSeats = seatDao.getUnavailableSeatCodes(showtimeId, seatIds);

            // Prepare data for view
            request.setAttribute("movie", movie);
            request.setAttribute("room", room);
            request.setAttribute("showtimeId", showtimeId);
            request.setAttribute("showtimeStr", showtimeStr);
            request.setAttribute("seats", seats);
            request.setAttribute("bookedSeats", bookedSeats);
            request.setAttribute("showDate", showDate.format(DATE_FORMATTER));
            request.setAttribute("showTime", showTime.format(TIME_FORMATTER));

            // Forward to booking page
            request.getRequestDispatcher("/booking.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            forwardError(request, response, "Lỗi: " + e.getMessage());
        }
    }

    private void handleGetShowtimes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            Movie movie = movieDao.getMovieById(movieId);

            if (movie == null) {
                out.println("<div class='error'>Phim không tồn tại</div>");
                return;
            }

            // Get showtimes for this movie
            List<Showtime> showtimes = showtimeDao.getShowtimesByMovie(movieId);

            // Group by date
            Map<LocalDate, List<Showtime>> showtimesByDate = new TreeMap<>();
            for (Showtime showtime : showtimes) {
                if (!showtime.getIsActive()) continue;
                if (showtime.getShowDate().isBefore(LocalDate.now())) continue;

                showtimesByDate.computeIfAbsent(showtime.getShowDate(),
                        k -> new ArrayList<>()).add(showtime);
            }

            // Generate HTML
            StringBuilder html = new StringBuilder();
            html.append("<div class='showtimes-content'>");
            html.append("<div class='movie-header'>");
            html.append("<h3>").append(escapeHtml(movie.getTitle())).append("</h3>");
            html.append("<p>").append(movie.getDuration()).append(" phút</p>");
            html.append("</div>");

            if (showtimesByDate.isEmpty()) {
                html.append("<div class='no-showtimes'>");
                html.append("<p>Hiện chưa có suất chiếu</p>");
                html.append("</div>");
            } else {
                for (Map.Entry<LocalDate, List<Showtime>> entry : showtimesByDate.entrySet()) {
                    html.append("<div class='day-group'>");
                    html.append("<h4 class='day-title'>")
                            .append(entry.getKey().format(DATE_FORMATTER))
                            .append("</h4>");
                    html.append("<div class='time-slots'>");

                    for (Showtime showtime : entry.getValue()) {
                        Room room = roomDao.getRoomById(showtime.getRoomId());
                        String roomName = room != null ? room.getRoomName() : "Phòng " + showtime.getRoomId();
                        String showTime = showtime.getShowTime().format(TIME_FORMATTER);

                        html.append("<button type='button' class='time-slot' ")
                                .append("onclick=\"selectShowtime(")
                                .append(showtime.getId()).append(",")
                                .append(showtime.getRoomId()).append(",'")
                                .append(escapeHtml(roomName)).append("','")
                                .append(showTime).append("')\">")
                                .append("<span class='time'>").append(showTime).append("</span>")
                                .append("<span class='room'>").append(escapeHtml(roomName)).append("</span>")
                                .append("</button>");
                    }

                    html.append("</div></div>");
                }
            }

            html.append("</div>");
            out.println(html.toString());

        } catch (NumberFormatException e) {
            out.println("<div class='error'>ID phim không hợp lệ</div>");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<div class='error'>Lỗi hệ thống</div>");
        }
    }

    private void handleGetSeatMap(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));

            // Get all seats for this room
            List<Seat> allSeats = seatDao.getSeatsByRoom(roomId);
            if (allSeats.isEmpty()) {
                out.println("<div class='error'>Phòng này chưa có ghế</div>");
                return;
            }

            // Get booked seats
            List<Integer> seatIds = new ArrayList<>();
            for (Seat seat : allSeats) {
                seatIds.add(seat.getId());
            }
            List<String> bookedSeats = seatDao.getUnavailableSeatCodes(showtimeId, seatIds);

            // Get selected seats from session
            HttpSession session = request.getSession();
            List<String> selectedSeats = (List<String>) session.getAttribute("selectedSeats");
            if (selectedSeats == null) {
                selectedSeats = new ArrayList<>();
            }

            // Generate seat map HTML
            StringBuilder html = new StringBuilder();
            html.append("<div class='seat-map'>");
            html.append("<div class='screen'>MÀN HÌNH</div>");

            // Group seats by row
            Map<String, List<Seat>> seatsByRow = new TreeMap<>();
            for (Seat seat : allSeats) {
                seatsByRow.computeIfAbsent(seat.getRowNumber(), k -> new ArrayList<>()).add(seat);
            }

            // Render each row
            for (Map.Entry<String, List<Seat>> entry : seatsByRow.entrySet()) {
                String row = entry.getKey();
                List<Seat> rowSeats = entry.getValue();

                // Sort by seat number
                rowSeats.sort(Comparator.comparingInt(Seat::getSeatNumber));

                html.append("<div class='seat-row'>");
                html.append("<span class='row-label'>").append(row).append("</span>");

                for (Seat seat : rowSeats) {
                    String seatCode = seat.getSeatCode();
                    String seatClass = "seat";
                    boolean isBooked = bookedSeats.contains(seatCode);
                    boolean isSelected = selectedSeats.contains(seatCode);

                    if (isBooked) {
                        seatClass += " booked";
                    } else if (isSelected) {
                        seatClass += " selected";
                    } else {
                        seatClass += " available";
                    }

                    html.append("<button type='button' class='").append(seatClass).append("'")
                            .append(" data-seat='").append(seatCode).append("'")
                            .append(" onclick='selectSeat(\"").append(seatCode).append("\")'")
                            .append(" title='Ghế ").append(seatCode).append("'>")
                            .append(seat.getSeatNumber())
                            .append("</button>");
                }

                html.append("</div>");
            }

            html.append("</div>");

            // Add legend
            html.append("<div class='seat-legend'>");
            html.append("<div class='legend-item'><div class='legend-box available'></div><span>Trống</span></div>");
            html.append("<div class='legend-item'><div class='legend-box selected'></div><span>Đang chọn</span></div>");
            html.append("<div class='legend-item'><div class='legend-box booked'></div><span>Đã đặt</span></div>");
            html.append("</div>");

            out.println(html.toString());

        } catch (NumberFormatException e) {
            out.println("<div class='error'>Tham số không hợp lệ</div>");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<div class='error'>Lỗi tải sơ đồ ghế</div>");
        }
    }

    private void reserveSeats(HttpServletRequest request, HttpServletResponse response,
                              HttpSession session, int userId) throws IOException {

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        try {
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String[] seatCodes = request.getParameterValues("seatCodes");

            if (seatCodes == null || seatCodes.length == 0) {
                out.println("ERROR:Vui lòng chọn ghế");
                return;
            }

            List<String> seatCodeList = Arrays.asList(seatCodes);
            String sessionId = session.getId();

            // Save selected seats to session
            session.setAttribute("selectedSeats", seatCodeList);

            // Call service to reserve seats
            Map<String, Object> result = bookingService.reserveSeats(
                    showtimeId, seatCodeList, roomId, userId, sessionId);

            if (Boolean.TRUE.equals(result.get("success"))) {
                // Save reservation info to session
                session.setAttribute("reservationId", result.get("reservationId"));
                session.setAttribute("reservedSeats", seatCodeList);
                session.setAttribute("reservedShowtimeId", showtimeId);
                session.setAttribute("reservedRoomId", roomId);

                out.println("SUCCESS:" + result.get("reservationId"));
            } else {
                out.println("ERROR:" + result.get("message"));
            }

        } catch (NumberFormatException e) {
            out.println("ERROR:Tham số không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("ERROR:Lỗi hệ thống");
        }
    }

    private void confirmBooking(HttpServletRequest request, HttpServletResponse response,
                                HttpSession session, int userId) throws IOException {

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        try {
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String[] seatCodes = request.getParameterValues("seatCodes");
            String ticketType = request.getParameter("ticketType");
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            int movieId = Integer.parseInt(request.getParameter("movieId"));

            if (seatCodes == null || seatCodes.length == 0) {
                out.println("ERROR:Vui lòng chọn ghế");
                return;
            }

            List<String> seatCodeList = Arrays.asList(seatCodes);
            int orderId = (int) (System.currentTimeMillis() % 1000000);

            // Confirm booking
            Map<String, Object> result = bookingService.confirmBooking(
                    showtimeId, seatCodeList, roomId, userId, orderId);

            if (Boolean.TRUE.equals(result.get("success"))) {
                // Add to cart
                Map<String, Object> cartResult = cartService.addToCart(
                        session, movieId, showtimeId, roomId, ticketType,
                        quantity, seatCodeList, userId);

                if (Boolean.TRUE.equals(cartResult.get("success"))) {
                    // Clear session
                    session.removeAttribute("reservationId");
                    session.removeAttribute("reservedSeats");
                    session.removeAttribute("selectedSeats");

                    out.println("SUCCESS:" + orderId);
                } else {
                    out.println("ERROR:" + cartResult.get("message"));
                }
            } else {
                out.println("ERROR:" + result.get("message"));
            }

        } catch (NumberFormatException e) {
            out.println("ERROR:Tham số không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("ERROR:Lỗi hệ thống");
        }
    }

    private void cancelReservation(HttpServletRequest request, HttpServletResponse response,
                                   int userId) throws IOException {

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        try {
            String reservationId = request.getParameter("reservationId");

            if (reservationId == null || reservationId.trim().isEmpty()) {
                out.println("ERROR:Thiếu reservationId");
                return;
            }

            Map<String, Object> result = bookingService.cancelReservation(reservationId, userId);

            if (Boolean.TRUE.equals(result.get("success"))) {
                out.println("SUCCESS");
            } else {
                out.println("ERROR:" + result.get("message"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("ERROR:Lỗi hệ thống");
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
                forwardError(request, response, (String) result.get("message"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            forwardError(request, response, "Lỗi hệ thống");
        }
    }


    private void processQuickBooking(HttpServletRequest request, HttpServletResponse response,
                                     HttpSession session, int userId) throws IOException {

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        try {
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String showtimeStr = request.getParameter("showtime");

            Map<String, Object> result = cartService.processQuickBooking(
                    movieId, roomId, showtimeStr, userId);

            if (Boolean.TRUE.equals(result.get("success"))) {
                // Save to session
                session.setAttribute("quickBookingMovieId", movieId);
                session.setAttribute("quickBookingRoomId", roomId);
                session.setAttribute("quickBookingShowtimeId", result.get("showtimeId"));

                out.println("SUCCESS:" + result.get("showtimeId"));
            } else {
                out.println("ERROR:" + result.get("message"));
            }

        } catch (NumberFormatException e) {
            out.println("ERROR:Tham số không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("ERROR:Lỗi hệ thống");
        }
    }

    private void checkSeatAvailability(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        try {
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String seatCode = request.getParameter("seatCode");

            // Get seat
            Seat seat = seatDao.getSeatByCode(roomId, seatCode);
            if (seat == null) {
                out.println("UNAVAILABLE");
                return;
            }

            // Check availability
            boolean isAvailable = seatDao.isSeatAvailable(showtimeId, seat.getId());
            out.println(isAvailable ? "AVAILABLE" : "UNAVAILABLE");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("ERROR");
        }
    }


    // ==================== HELPER METHODS ====================

    private void forwardError(HttpServletRequest request, HttpServletResponse response,
                              String message) throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/plain");
        response.getWriter().write("ERROR:" + message);
    }

    private String escapeHtml(Object obj) {
        if (obj == null) return "";
        String str = obj.toString();
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    @Override
    public void destroy() {
        bookingService.shutdown();
        super.destroy();
    }
}