package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.service.UserService;

import java.io.IOException;

@WebServlet("/user/profile/edit")
public class UserProfileEditController extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");
        req.setAttribute("user", user);

        req.getRequestDispatcher("/WEB-INF/views/user/profile-edit.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        User user = (User) req.getSession().getAttribute("user");

        String fullName  = req.getParameter("fullName");
        String phone     = req.getParameter("phone");
        String email     = req.getParameter("email");
        String gender    = req.getParameter("gender");
        String birthDate = req.getParameter("birthDate");

        userService.updateProfile(
                user.getId(),
                fullName,
                phone,
                email,
                gender,
                birthDate
        );

        // refresh lại session
        User updatedUser = userService.getById(user.getId());
        req.getSession().setAttribute("user", updatedUser);

        resp.sendRedirect(req.getContextPath() + "/user/profile");
    }
}
