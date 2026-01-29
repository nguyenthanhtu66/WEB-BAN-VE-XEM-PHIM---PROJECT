package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.model.TicketWarehouse;
import vn.edu.hcmuaf.fit.demo1.dao.TicketWarehouseDao;

import java.io.IOException;
import java.util.List;

@WebServlet("/ticket-warehouse")
public class TicketWarehouseController extends HttpServlet {
    private final TicketWarehouseDao ticketWarehouseDao = new TicketWarehouseDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Kiểm tra đăng nhập
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?redirect=" + request.getRequestURI());
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = (User) session.getAttribute("loggedUser");
        }

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?redirect=" + request.getRequestURI());
            return;
        }

        try {
            // Lấy danh sách vé của user
            List<TicketWarehouse> tickets = ticketWarehouseDao.getTicketsByUserId(user.getId());

            // Tính toán thống kê
            long totalTickets = tickets.size();
            long validTickets = tickets.stream().filter(t -> "valid".equals(t.getTicketStatus())).count();
            long usedTickets = tickets.stream().filter(t -> "used".equals(t.getTicketStatus())).count();
            long expiredTickets = tickets.stream().filter(t -> "expired".equals(t.getTicketStatus())).count();
            long cancelledTickets = tickets.stream().filter(t -> "cancelled".equals(t.getTicketStatus())).count();

            // Set attributes
            request.setAttribute("tickets", tickets);
            request.setAttribute("totalTickets", totalTickets);
            request.setAttribute("validTickets", validTickets);
            request.setAttribute("usedTickets", usedTickets);
            request.setAttribute("expiredTickets", expiredTickets);
            request.setAttribute("cancelledTickets", cancelledTickets);

            // Forward to JSP
            request.getRequestDispatcher("/Kho-Ve.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải kho vé: " + e.getMessage());
            request.getRequestDispatcher("/Kho-Ve.jsp").forward(request, response);
        }
    }
}
