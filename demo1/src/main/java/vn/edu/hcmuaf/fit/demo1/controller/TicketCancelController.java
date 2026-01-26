package vn.edu.hcmuaf.fit.demo1.controller;

import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.service.TicketService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/ticket-cancel")
public class TicketCancelController extends HttpServlet {

    private final TicketService ticketService = new TicketService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        int ticketId = Integer.parseInt(req.getParameter("ticketId"));

        ticketService.cancelTicket(ticketId, ticketId);

        // Quay lại trang lịch sử vé
        resp.sendRedirect(req.getContextPath() + "/ticket-warehouse");
    }
}
