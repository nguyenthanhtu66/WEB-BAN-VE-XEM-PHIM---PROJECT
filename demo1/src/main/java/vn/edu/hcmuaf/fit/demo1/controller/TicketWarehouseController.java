package vn.edu.hcmuaf.fit.demo1.controller;

import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.service.TicketService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/ticket-warehouse")
public class TicketWarehouseController extends HttpServlet {

    private final TicketService ticketService = new TicketService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        // TẠM THỜI: fake user để test (xóa trước khi nộp)
        if (user == null) {
            user = new User();
            user.setId(1); // phải tồn tại trong DB
            user.setFullName("Nguyễn Văn Demo");
            user.setEmail("demo@gmail.com");
            session.setAttribute("user", user);
        }

        req.setAttribute("tickets",
                ticketService.getTicketsByUser(user.getId()));

        req.getRequestDispatcher("/WEB-INF/views/ticket-warehouse.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect("login");
            return;
        }

        String action = req.getParameter("action");
        if ("cancel".equals(action)) {
            int ticketId = Integer.parseInt(req.getParameter("ticketId"));
            ticketService.cancelTicket(ticketId, user.getId());
        }

        // PRG
        resp.sendRedirect("ticket-warehouse");
    }
}
