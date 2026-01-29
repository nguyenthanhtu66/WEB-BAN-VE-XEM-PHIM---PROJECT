package vn.edu.hcmuaf.fit.demo1.controller.admin;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.hcmuaf.fit.demo1.service.ContactService;

@WebServlet("/admin/contacts")
public class AdminContactController extends HttpServlet {

    private final ContactService service = new ContactService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("contacts", service.getAll());
        req.getRequestDispatcher("admin/contacts.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String action = req.getParameter("action");

        if ("done".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            service.markDone(id);
        }

        resp.sendRedirect(req.getContextPath() + "/admin/contacts");
    }
}