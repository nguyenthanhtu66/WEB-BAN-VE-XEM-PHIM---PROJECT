package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import vn.edu.hcmuaf.fit.demo1.model.Booking;
import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.service.BookingService;
import vn.edu.hcmuaf.fit.demo1.service.MovieService;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "BookingServlet", value = {"/booking", "/process-booking", "/my-bookings"})
public class BookingServlet extends HttpServlet {
    private BookingService bookingService;
    private MovieService movieService;

    @Override
    public void init() {
        this.bookingService = new BookingService();
        this.movieService = new MovieService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("my-bookings".equals(action)) {
            showMyBookings(request, response);
        } else {
            showBookingPage(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processBooking(request, response);
    }

    private void showBookingPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            // Chưa đăng nhập, chuyển đến trang đăng nhập
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Lấy thông tin phim từ parameter
        String movieIdStr = request.getParameter("movie");
        if (movieIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            int movieId = Integer.parseInt(movieIdStr);
            var movie = movieService.getMovieById(movieId);

            if (movie == null) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            // Lấy thông tin rạp và giờ chiếu từ parameter hoặc mặc định
            String cinemaRoom = request.getParameter("cinema");
            String showtime = request.getParameter("time");

            if (cinemaRoom == null) cinemaRoom = "Phòng A";
            if (showtime == null) showtime = "19:00";

            // Tạo dữ liệu ghế mẫu
            List<List<String>> seatLayout = createSampleSeatLayout();

            request.setAttribute("movie", movie);
            request.setAttribute("cinemaRoom", cinemaRoom);
            request.setAttribute("showtime", showtime);
            request.setAttribute("seatLayout", seatLayout);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/booking.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    private void showMyBookings(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Lấy danh sách booking của user
        List<Booking> bookings = bookingService.getBookingsByUserId(user.getId());
        request.setAttribute("bookings", bookings);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/my-bookings.jsp");
        dispatcher.forward(request, response);
    }

    private void processBooking(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Lấy thông tin từ form
        String movieIdStr = request.getParameter("movieId");
        String movieTitle = request.getParameter("movieTitle");
        String[] selectedSeats = request.getParameterValues("selectedSeats");
        String totalPriceStr = request.getParameter("totalPrice");

        if (movieIdStr == null || selectedSeats == null || selectedSeats.length == 0) {
            request.setAttribute("error", "Vui lòng chọn ghế!");
            showBookingPage(request, response);
            return;
        }

        try {
            int movieId = Integer.parseInt(movieIdStr);
            double totalPrice = Double.parseDouble(totalPriceStr);

            // Chuyển mảng seats thành List
            List<String> seats = Arrays.asList(selectedSeats);

            // Tạo booking
            Booking booking = bookingService.createBooking(
                    user.getId(), movieId, movieTitle, seats, totalPrice
            );

            // Lấy thêm thông tin từ form
            String cinemaRoom = request.getParameter("cinemaRoom");
            String showtime = request.getParameter("showtime");

            if (cinemaRoom != null) booking.setCinemaRoom(cinemaRoom);

            // Chuyển đến trang xác nhận
            request.setAttribute("booking", booking);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/booking-confirm.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Dữ liệu không hợp lệ!");
            showBookingPage(request, response);
        }
    }

    private List<List<String>> createSampleSeatLayout() {
        List<List<String>> seatLayout = new ArrayList<>();

        // Hàng A: 8 ghế
        List<String> rowA = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            rowA.add("A" + (i < 10 ? "0" + i : i));
        }
        seatLayout.add(rowA);

        // Hàng B: 8 ghế
        List<String> rowB = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            rowB.add("B" + (i < 10 ? "0" + i : i));
        }
        seatLayout.add(rowB);

        // Hàng C: 10 ghế
        List<String> rowC = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            rowC.add("C" + (i < 10 ? "0" + i : i));
        }
        seatLayout.add(rowC);

        // Hàng D: 10 ghế
        List<String> rowD = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            rowD.add("D" + (i < 10 ? "0" + i : i));
        }
        seatLayout.add(rowD);

        return seatLayout;
    }
}