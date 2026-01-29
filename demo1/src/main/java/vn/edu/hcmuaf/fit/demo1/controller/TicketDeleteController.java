package vn.edu.hcmuaf.fit.demo1.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.service.TicketService;

@WebServlet("/ticket-delete")
public class TicketDeleteController extends HttpServlet {

    private TicketService ticketService = new TicketService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect("login");
            return;
        }

        int ticketId = Integer.parseInt(req.getParameter("ticketId"));
        ticketService.deleteTicket(ticketId, user.getId());

        resp.sendRedirect("ticket-warehouse");
    }
}

