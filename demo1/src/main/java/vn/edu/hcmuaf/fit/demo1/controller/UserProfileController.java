package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.demo1.model.User;

import java.io.IOException;

@WebServlet("/user/profile")
public class UserProfileController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");
        req.setAttribute("user", user);

        req.getRequestDispatcher("/WEB-INF/views/user/user.jsp")
           .forward(req, resp);
    }
}
