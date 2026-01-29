package vn.edu.hcmuaf.fit.demo1.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.hcmuaf.fit.demo1.dao.BookedSeatDao;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/check-seat-status") // URL khác với refresh-seat-status
public class CheckSeatStatusController extends HttpServlet {
    private final Gson gson = new Gson();
    private final BookedSeatDao bookedSeatDao = new BookedSeatDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            String showtimeIdStr = request.getParameter("showtimeId");
            String seatIdStr = request.getParameter("seatId");

            if (showtimeIdStr == null || seatIdStr == null) {
                result.put("success", false);
                result.put("message", "Missing parameters");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            int showtimeId = Integer.parseInt(showtimeIdStr);
            int seatId = Integer.parseInt(seatIdStr);

            System.out.println("🔍 Checking seat status - Showtime: " + showtimeId + ", Seat: " + seatId);

            // Kiểm tra trạng thái hiện tại của ghế
            Map<String, Object> seatStatus = bookedSeatDao.getSeatStatus(showtimeId, seatId);

            System.out.println("📊 Seat status: " + seatStatus);

            boolean isBooked = "booked".equals(seatStatus.get("status"));

            result.put("success", true);
            result.put("seatStatus", seatStatus);
            result.put("isBooked", isBooked);

        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid parameters");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Server error: " + e.getMessage());
        }

        response.getWriter().write(gson.toJson(result));
    }
}