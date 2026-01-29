package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import vn.edu.hcmuaf.fit.demo1.dao.UserDao;
import vn.edu.hcmuaf.fit.demo1.dto.LoginForm;
import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.util.PasswordUtils;
import vn.edu.hcmuaf.fit.demo1.validation.LoginValidate;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "Login", value = "/login")
public class LoginController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LoginForm logForm = new LoginForm();
        logForm.setEmail(request.getParameter("email"));
        logForm.setPassword(request.getParameter("password"));

        LoginValidate validation = new LoginValidate();
        Map<String, String> errors = validation.loginValidate(logForm);

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("email", logForm.getEmail());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        UserDao userDao = new UserDao();
        User user = userDao.findByEmail(logForm.getEmail());

        if (user == null || !PasswordUtils.check(logForm.getPassword(), user.getPassword())) {
            request.setAttribute("loginError", "Email hoặc mật khẩu không đúng");
            request.setAttribute("email", logForm.getEmail());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setAttribute("role", user.getRole());
//        response.sendRedirect("home");
        if (user.getRole().equals("admin")) {
            response.sendRedirect(request.getContextPath() + "/admin-users");
        } else {
            response.sendRedirect("home");
        }
    }
}