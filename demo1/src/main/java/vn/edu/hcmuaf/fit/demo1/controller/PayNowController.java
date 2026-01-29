package vn.edu.hcmuaf.fit.demo1.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.model.CartItem;
import vn.edu.hcmuaf.fit.demo1.service.BookingService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/pay-now")
public class PayNowController extends HttpServlet {
    private final BookingService bookingService = new BookingService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession(true); // ĐẢM BẢO CÓ SESSION

        System.out.println("\n=== PAY NOW FROM MODAL START ===");
        System.out.println("📥 Session ID: " + session.getId());
        System.out.println("📥 Session isNew: " + session.isNew());

        try {
            // Kiểm tra đăng nhập
            User user = (User) session.getAttribute("user");
            if (user == null) {
                user = (User) session.getAttribute("loggedUser");
            }

            if (user == null) {
                System.out.println("❌ User not logged in");
                result.put("success", false);
                result.put("message", "Vui lòng đăng nhập để thanh toán");
                result.put("redirect", request.getContextPath() + "/login.jsp?redirect=thanh-toan.jsp&payNow=true");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            System.out.println("👤 User ID: " + user.getId());

            // Lấy thông tin từ request
            String movieIdStr = request.getParameter("movieId");
            String showtimeIdStr = request.getParameter("showtimeId");
            String seatIdStr = request.getParameter("seatId");
            String ticketTypeIdStr = request.getParameter("ticketTypeId");

            System.out.println("📥 Parameters from modal:");
            System.out.println("  movieId: " + movieIdStr);
            System.out.println("  showtimeId: " + showtimeIdStr);
            System.out.println("  seatId: " + seatIdStr);
            System.out.println("  ticketTypeId: " + ticketTypeIdStr);

            // Validate parameters
            if (movieIdStr == null || showtimeIdStr == null ||
                    seatIdStr == null || ticketTypeIdStr == null) {
                result.put("success", false);
                result.put("message", "Thiếu thông tin đặt vé");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            int movieId = Integer.parseInt(movieIdStr);
            int showtimeId = Integer.parseInt(showtimeIdStr);
            int seatId = Integer.parseInt(seatIdStr);
            int ticketTypeId = Integer.parseInt(ticketTypeIdStr);

            // Tạo CartItem để lưu vào session
            CartItem cartItem = bookingService.createCartItem(movieId, showtimeId, seatId, ticketTypeId);
            if (cartItem == null) {
                result.put("success", false);
                result.put("message", "Không thể tạo thông tin vé");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Lưu thông tin thanh toán vào session
            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("movieId", movieId);
            paymentData.put("showtimeId", showtimeId);
            paymentData.put("seatId", seatId);
            paymentData.put("ticketTypeId", ticketTypeId);
            paymentData.put("movieTitle", cartItem.getMovieTitle());
            paymentData.put("seatCode", cartItem.getSeatCode());
            paymentData.put("showDate", cartItem.getShowDate().toString());
            paymentData.put("showTime", cartItem.getShowTime().toString());
            paymentData.put("roomName", cartItem.getRoomName());
            paymentData.put("ticketTypeName", cartItem.getTicketTypeName());
            paymentData.put("price", cartItem.getPrice());
            paymentData.put("roomId", cartItem.getRoomId());

            // QUAN TRỌNG: Set và invalidate session
            session.setAttribute("paymentData", paymentData);

            // Đảm bảo session được lưu
            session.setMaxInactiveInterval(30 * 60); // 30 phút

            // Log để debug
            System.out.println("✅ Payment data saved to session:");
            System.out.println("  Session ID: " + session.getId());
            System.out.println("  Movie: " + cartItem.getMovieTitle());
            System.out.println("  Seat: " + cartItem.getSeatCode());
            System.out.println("  Price: " + cartItem.getPrice());
            System.out.println("  All session attributes: " + getSessionAttributes(session));

            // Trả về JSON với thông tin thành công
            result.put("success", true);
            result.put("message", "Đã lưu thông tin thanh toán");
            result.put("redirectUrl", request.getContextPath() + "/thanh-toan.jsp?payNow=true");
            result.put("sessionId", session.getId());

            response.getWriter().write(gson.toJson(result));

        } catch (NumberFormatException e) {
            System.err.println("❌ NumberFormatException: " + e.getMessage());
            result.put("success", false);
            result.put("message", "Dữ liệu không hợp lệ");
            response.getWriter().write(gson.toJson(result));
        } catch (Exception e) {
            System.err.println("❌ Exception in PayNowController: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi server: " + e.getMessage());
            response.getWriter().write(gson.toJson(result));
        }

        System.out.println("=== PAY NOW FROM MODAL END ===\n");
    }

    private String getSessionAttributes(HttpSession session) {
        StringBuilder sb = new StringBuilder();
        java.util.Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            Object value = session.getAttribute(name);
            sb.append(name).append("=").append(value).append(", ");
        }
        return sb.toString();
    }
}