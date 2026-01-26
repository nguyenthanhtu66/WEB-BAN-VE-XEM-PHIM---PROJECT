package vn.edu.hcmuaf.fit.demo1.controller;

import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.service.TicketService;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/ticket-warehouse")
public class TicketWarehouseController extends HttpServlet {

    private final TicketService ticketService = new TicketService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // User user = (User) req.getSession().getAttribute("user");
        // if (user == null) {
        //     resp.sendRedirect("login.jsp");
        //     return;
        // }

        // req.setAttribute(
        //         "tickets",
        //         ticketService.getTicketsByUser(user.getId()));

        // req.getRequestDispatcher("/WEB-INF/views/ticket-warehouse.jsp")
        //         .forward(req, resp);

         HttpSession session = req.getSession();

    User user = (User) session.getAttribute("user");

    // ===== TẠM THỜI BỎ QUA LOGIN =====
    if (user == null) {
        user = new User();
        user.setId(1); // PHẢI khớp user_id trong bảng tickets
        user.setFullName("Nguyễn Văn Demo");
        user.setEmail("demo@gmail.com");
System.out.println(">>> USER ID = " + user.getId());

        session.setAttribute("user", user);
    }
    // ===== HẾT PHẦN TẠM =====

    req.setAttribute(
        "tickets",
        ticketService.getTicketsByUser(user.getId())
    );

    req.getRequestDispatcher("/WEB-INF/views/ticket-warehouse.jsp")
       .forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
        resp.sendRedirect("login.jsp");
        return;
        }

        String action = req.getParameter("action");

        if ("cancel".equals(action)) {
        int ticketId = Integer.parseInt(req.getParameter("ticketId"));
        ticketService.cancelTicket(ticketId, user.getId());
        }

        // Sau khi hủy vé → reload lại trang lịch sử
        resp.sendRedirect("ticket-warehouse");
        

        req.getRequestDispatcher("/WEB-INF/views/ticket-warehouse.jsp")
                .forward(req, resp);
    }
}
