package vn.edu.hcmuaf.fit.demo1.controller;

import vn.edu.hcmuaf.fit.demo1.service.*;
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
import java.util.stream.Collectors;

@WebServlet(name = "QuickBookingController", urlPatterns = {
        "/quick-booking",
        "/quick-booking/process",
        "/quick-booking/seat-map",
        "/quick-booking/reserve",
        "/quick-booking/add-to-cart"
})
public class QuickBookingController extends HttpServlet {

    private final QuickBookingService quickBookingService = new QuickBookingService();
    private final MovieService movieService = new MovieService();
    private final RoomDao roomDao = new RoomDao();
    private final ShowtimeDao showtimeDao = new ShowtimeDao();
    private final SeatDao seatDao = new SeatDao();
    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        try {
            if ("/quick-booking".equals(path)) {
                showQuickBookingPage(request, response);
            } else if ("/quick-booking/seat-map".equals(path)) {
                getSeatMap(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Lỗi hệ thống: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        HttpSession session = request.getSession();

        try {
            if ("/quick-booking/process".equals(path)) {
                processQuickBooking(request, response, session);
            } else if ("/quick-booking/reserve".equals(path)) {
                reserveSeats(request, response, session);
            } else if ("/quick-booking/add-to-cart".equals(path)) {
                addToCartFromQuickBooking(request, response, session);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void showQuickBookingPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Movie> showingMovies = movieService.getMoviesByStatus("Dang+chieu");
            List<Room> activeRooms = roomDao.getAllActiveRooms();

            request.setAttribute("showingMovies", showingMovies);
            request.setAttribute("rooms", activeRooms);
            request.setAttribute("today", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            request.setAttribute("tomorrow", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            request.getRequestDispatcher("/quick-booking.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            forwardError(request, response, "Lỗi: " + e.getMessage());
        }
    }

    private void processQuickBooking(HttpServletRequest request, HttpServletResponse response,
                                     HttpSession session) throws IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String showtimeStr = request.getParameter("showtime");

            Map<String, Object> result = quickBookingService.processQuickBooking(
                    movieId, roomId, showtimeStr);

            if (Boolean.TRUE.equals(result.get("success"))) {
                session.setAttribute("quickBookingData", result);
                session.setAttribute("quickBookingMovieId", movieId);
                session.setAttribute("quickBookingRoomId", roomId);
                session.setAttribute("quickBookingShowtimeId", result.get("showtimeId"));
                session.setAttribute("quickBookingShowtimeStr", showtimeStr);

                out.println("{\"success\":true,\"showtimeId\":" + result.get("showtimeId") + "}");
            } else {
                out.println("{\"success\":false,\"message\":\"" + result.get("message") + "\"}");
            }

        } catch (NumberFormatException e) {
            out.println("{\"success\":false,\"message\":\"Tham số không hợp lệ\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("{\"success\":false,\"message\":\"Lỗi hệ thống\"}");
        }
    }

    private void getSeatMap(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));

            Map<String, Object> seatStatus = bookingService.checkSeatStatus(showtimeId, roomId);

            if (Boolean.TRUE.equals(seatStatus.get("success"))) {
                out.println("{\"success\":true,\"seats\":" + toJsonArray(seatStatus.get("seats")) + "}");
            } else {
                out.println("{\"success\":false,\"message\":\"" + seatStatus.get("message") + "\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("{\"success\":false,\"message\":\"Lỗi hệ thống\"}");
        }
    }

    private void reserveSeats(HttpServletRequest request, HttpServletResponse response,
                              HttpSession session) throws IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String[] seatCodes = request.getParameterValues("seatCodes");
            String sessionId = session.getId();

            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) userId = 0;

            if (seatCodes == null || seatCodes.length == 0) {
                out.println("{\"success\":false,\"message\":\"Vui lòng chọn ghế\"}");
                return;
            }

            List<String> seatCodeList = Arrays.asList(seatCodes);

            Map<String, Object> result = bookingService.reserveSeats(
                    showtimeId, seatCodeList, roomId, userId, sessionId);

            if (Boolean.TRUE.equals(result.get("success"))) {
                session.setAttribute("quickBookingReservationId", result.get("reservationId"));
                session.setAttribute("quickBookingSelectedSeats", seatCodeList);

                out.println("{\"success\":true,\"reservationId\":\"" +
                        result.get("reservationId") + "\"}");
            } else {
                out.println("{\"success\":false,\"message\":\"" + result.get("message") + "\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("{\"success\":false,\"message\":\"Lỗi hệ thống\"}");
        }
    }

    private void addToCartFromQuickBooking(HttpServletRequest request, HttpServletResponse response,
                                           HttpSession session) throws IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            String ticketType = request.getParameter("ticketType");
            String[] seatCodes = request.getParameterValues("seatCodes");

            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) userId = 0;

            if (seatCodes == null || seatCodes.length == 0) {
                out.println("{\"success\":false,\"message\":\"Vui lòng chọn ghế\"}");
                return;
            }

            List<String> seatCodeList = Arrays.asList(seatCodes);

            CartService cartService = new CartService();
            Map<String, Object> result = cartService.addToCart(
                    session, movieId, showtimeId, roomId, ticketType,
                    seatCodeList.size(), seatCodeList, userId);

            if (Boolean.TRUE.equals(result.get("success"))) {
                session.removeAttribute("quickBookingReservationId");
                session.removeAttribute("quickBookingSelectedSeats");
                session.removeAttribute("quickBookingData");

                out.println("{\"success\":true,\"message\":\"Đã thêm vào giỏ hàng\"}");
            } else {
                out.println("{\"success\":false,\"message\":\"" + result.get("message") + "\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("{\"success\":false,\"message\":\"Lỗi hệ thống\"}");
        }
    }

    private void forwardError(HttpServletRequest request, HttpServletResponse response,
                              String message) throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write("{\"success\":false,\"message\":\"" + message + "\"}");
    }

    private String toJsonArray(Object obj) {
        if (obj == null) return "[]";
        return obj.toString().replace("=", ":").replace("\\\"", "\"");
    }
}