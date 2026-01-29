package vn.edu.hcmuaf.fit.demo1.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.dao.ShowtimeDao;
import vn.edu.hcmuaf.fit.demo1.dao.BookedSeatDao;
import vn.edu.hcmuaf.fit.demo1.model.Showtime;
import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.service.BookingService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/reserve-seat")
public class ReserveSeatController extends HttpServlet {

    private final BookingService bookingService = new BookingService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set response headers for CORS
        setCorsHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();

        System.out.println("\n=== RESERVE SEAT CONTROLLER START ===");
        System.out.println("📥 Request URL: " + request.getRequestURL());
        System.out.println("📥 Remote Address: " + request.getRemoteAddr());
        System.out.println("📥 Method: " + request.getMethod());

        try {
            // Đọc tất cả parameters từ request
            request.setCharacterEncoding("UTF-8");

            // Debug: Hiển thị tất cả parameters
            System.out.println("📋 Request Parameters:");
            Map<String, String[]> paramMap = request.getParameterMap();

            if (paramMap.isEmpty()) {
                System.out.println("⚠ No parameters in parameter map!");

                // Thử đọc từ body
                StringBuilder requestBody = new StringBuilder();
                BufferedReader reader = request.getReader();
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }

                System.out.println("📝 Raw request body: " + requestBody.toString());

                // Parse từ body nếu có
                if (requestBody.length() > 0) {
                    String bodyStr = requestBody.toString();
                    // Parse application/x-www-form-urlencoded
                    String[] pairs = bodyStr.split("&");
                    for (String pair : pairs) {
                        int equalsIndex = pair.indexOf("=");
                        if (equalsIndex > 0) {
                            String key = pair.substring(0, equalsIndex);
                            String value = pair.substring(equalsIndex + 1);
                            // URL decode
                            value = java.net.URLDecoder.decode(value, "UTF-8");

                            System.out.println("  " + key + " = " + value);

                            // Set attribute cho các tham số chính
                            if ("showtimeId".equals(key)) {
                                request.setAttribute("showtimeId", value);
                            } else if ("seatId".equals(key)) {
                                request.setAttribute("seatId", value);
                            } else if ("action".equals(key)) {
                                request.setAttribute("action", value);
                            }
                        }
                    }
                }
            } else {
                paramMap.forEach((key, values) -> {
                    System.out.println("  " + key + " = " + Arrays.toString(values));
                });
            }

            // Lấy parameters - ưu tiên từ parameter map trước
            String action = request.getParameter("action");
            String showtimeIdStr = request.getParameter("showtimeId");
            String seatIdStr = request.getParameter("seatId");

            // Nếu không có trong parameter map, thử lấy từ attribute
            if (showtimeIdStr == null) {
                showtimeIdStr = (String) request.getAttribute("showtimeId");
            }
            if (seatIdStr == null) {
                seatIdStr = (String) request.getAttribute("seatId");
            }
            if (action == null) {
                action = (String) request.getAttribute("action");
            }

            System.out.println("\n🔍 Final Parameters:");
            System.out.println("  action: '" + action + "'");
            System.out.println("  showtimeId: '" + showtimeIdStr + "'");
            System.out.println("  seatId: '" + seatIdStr + "'");

            // Validate required parameters
            if (showtimeIdStr == null || showtimeIdStr.trim().isEmpty()) {
                System.err.println("❌ ERROR: showtimeId is null or empty");
                result.put("success", false);
                result.put("message", "Thiếu thông tin showtimeId");
                sendResponse(response, result);
                return;
            }

            if (seatIdStr == null || seatIdStr.trim().isEmpty()) {
                System.err.println("❌ ERROR: seatId is null or empty");
                result.put("success", false);
                result.put("message", "Thiếu thông tin seatId");
                sendResponse(response, result);
                return;
            }

            int showtimeId;
            int seatId;

            try {
                showtimeId = Integer.parseInt(showtimeIdStr.trim());
                seatId = Integer.parseInt(seatIdStr.trim());
                System.out.println("✅ Parsed IDs:");
                System.out.println("  showtimeId: " + showtimeId);
                System.out.println("  seatId: " + seatId);
            } catch (NumberFormatException e) {
                System.err.println("❌ NumberFormatException: " + e.getMessage());
                result.put("success", false);
                result.put("message", "ID không hợp lệ: " + e.getMessage());
                sendResponse(response, result);
                return;
            }

            // Kiểm tra showtime có tồn tại không
            ShowtimeDao showtimeDao = new ShowtimeDao();
            Showtime showtime = showtimeDao.getShowtimeById(showtimeId);

            if (showtime == null) {
                System.err.println("❌ Showtime not found in database: " + showtimeId);
                result.put("success", false);
                result.put("message", "Suất chiếu không tồn tại (ID: " + showtimeId + ")");
                sendResponse(response, result);
                return;
            }

            System.out.println("✅ Showtime found:");
            System.out.println("  ID: " + showtime.getId());
            System.out.println("  Movie ID: " + showtime.getMovieId());
            System.out.println("  Room ID: " + showtime.getRoomId());
            System.out.println("  Date: " + showtime.getShowDate());
            System.out.println("  Time: " + showtime.getShowTime());
            System.out.println("  Active: " + showtime.isActive());

            if (!showtime.isActive()) {
                System.err.println("❌ Showtime is inactive: " + showtimeId);
                result.put("success", false);
                result.put("message", "Suất chiếu không còn hoạt động");
                sendResponse(response, result);
                return;
            }

            // Lấy user ID từ session (nếu có)
            HttpSession session = request.getSession(false);
            Integer userId = null;
            String sessionId = null;
            if (session != null) {
                sessionId = session.getId();
                System.out.println("Session ID: " + sessionId);

                User user = (User) session.getAttribute("user");
                if (user != null) {
                    userId = user.getId();
                    System.out.println("✅ User ID from session: " + userId);
                } else {
                    System.out.println("⚠ No user in session (anonymous user)");
                }
            } else {
                System.out.println("⚠ No session found");
            }

            boolean success = false;
            String message = "";

            if ("release".equals(action)) {
                // Release ghế
                System.out.println("\n🔓 Releasing seat...");
                success = bookingService.releaseSeat(showtimeId, seatId);
                message = success ? "Đã hủy giữ ghế" : "Không thể hủy giữ ghế";
                System.out.println("Release result: " + success + " - " + message);
            } else {
                // Giữ ghế (mặc định)
                System.out.println("\n🔒 Reserving seat...");

                // Kiểm tra ghế có khả dụng CHO USER/SESSION NÀY không
                BookedSeatDao bookedSeatDao = new BookedSeatDao();
                boolean isAvailable;

                if (userId != null) {
                    // Đã login: kiểm tra cho user
                    isAvailable = bookedSeatDao.isSeatAvailableForUser(showtimeId, seatId, userId);
                } else if (sessionId != null) {
                    // Chưa login: kiểm tra cho session
                    isAvailable = bookedSeatDao.isSeatAvailable(showtimeId, seatId, sessionId);
                } else {
                    // Không có session: dùng phương thức cũ
                    isAvailable = bookedSeatDao.isSeatAvailable(showtimeId, seatId);
                }

                System.out.println("Seat availability check: " + isAvailable);

                if (!isAvailable) {
                    System.err.println("❌ Seat not available: showtimeId=" + showtimeId + ", seatId=" + seatId);
                    result.put("success", false);
                    result.put("message", "Ghế này đã được đặt hoặc giữ");
                    sendResponse(response, result);
                    return;
                }

                // Thực hiện giữ ghế
                success = bookingService.reserveSeat(showtimeId, seatId, userId, sessionId);
                message = success ? "Ghế đã được giữ trong 5 phút" : "Không thể giữ ghế";
                System.out.println("Reserve result: " + success + " - " + message);
            }

            result.put("success", success);
            result.put("message", message);
            if (success) {
                result.put("showtimeId", showtimeId);
                result.put("seatId", seatId);
            }

        } catch (NumberFormatException e) {
            System.err.println("❌ NumberFormatException: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Dữ liệu không hợp lệ: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ General Exception: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi server: " + e.getMessage());
        }

        System.out.println("\n📤 Sending response: " + result);
        System.out.println("=== RESERVE SEAT CONTROLLER END ===\n");

        sendResponse(response, result);
    }

    private void sendResponse(HttpServletResponse response, Map<String, Object> result) throws IOException {
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(result));
        out.flush();
    }

    // Handle OPTIONS request for CORS
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
}