package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.service.UserService;
import java.io.IOException;

@WebServlet(name = "AuthServlet", value = {"/login", "/logout", "/register"})
public class AuthServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();

        if ("/logout".equals(action)) {
            logout(request, response);
        } else if ("/register".equals(action)) {
            showRegisterPage(request, response);
        } else {
            showLoginPage(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();

        if ("/register".equals(action)) {
            processRegister(request, response);
        } else {
            processLogin(request, response);
        }
    }

    private void showLoginPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra cookie remember me
        Cookie[] cookies = request.getCookies();
        String rememberedEmail = "";

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remember_email".equals(cookie.getName())) {
                    rememberedEmail = cookie.getValue();
                    break;
                }
            }
        }

        request.setAttribute("rememberedEmail", rememberedEmail);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Login.jsp");
        dispatcher.forward(request, response);
    }

    private void processLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");

        // Gọi service để xử lý đăng nhập
        User user = userService.login(email, password);

        if (user != null) {
            // Lưu thông tin user vào session
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("isLoggedIn", true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getFullName());

            // Lưu remember me trong cookie
            if ("on".equals(remember)) {
                Cookie emailCookie = new Cookie("remember_email", email);
                emailCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
                response.addCookie(emailCookie);
            }

            // Chuyển hướng về trang chủ
            response.sendRedirect(request.getContextPath() + "/home");
        } else {
            request.setAttribute("error", "Email hoặc mật khẩu không đúng!");
            request.setAttribute("email", email);
            showLoginPage(request, response);
        }
    }

    private void processRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");

        // Kiểm tra dữ liệu đầu vào
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin!");
            showRegisterPage(request, response);
            return;
        }

        // Kiểm tra password xác nhận
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
            request.setAttribute("email", email);
            request.setAttribute("fullName", fullName);
            request.setAttribute("phone", phone);
            showRegisterPage(request, response);
            return;
        }

        // Kiểm tra email hợp lệ
        if (!userService.isValidEmail(email)) {
            request.setAttribute("error", "Email không hợp lệ!");
            request.setAttribute("email", email);
            request.setAttribute("fullName", fullName);
            request.setAttribute("phone", phone);
            showRegisterPage(request, response);
            return;
        }

        // Kiểm tra password mạnh
        if (!userService.isStrongPassword(password)) {
            request.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
            request.setAttribute("email", email);
            request.setAttribute("fullName", fullName);
            request.setAttribute("phone", phone);
            showRegisterPage(request, response);
            return;
        }

        // Tạo đối tượng User
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setFullName(fullName);
        newUser.setPhone(phone);
        newUser.setRole("USER");

        // Gọi service đăng ký
        boolean isRegistered = userService.register(newUser);

        if (isRegistered) {
            request.setAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            showLoginPage(request, response);
        } else {
            request.setAttribute("error", "Email đã tồn tại!");
            request.setAttribute("email", email);
            request.setAttribute("fullName", fullName);
            request.setAttribute("phone", phone);
            showRegisterPage(request, response);
        }
    }

    private void showRegisterPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Register.jsp");
        dispatcher.forward(request, response);
    }

    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        session.invalidate();

        // Xóa cookie remember me
        Cookie emailCookie = new Cookie("remember_email", "");
        emailCookie.setMaxAge(0);
        response.addCookie(emailCookie);

        response.sendRedirect(request.getContextPath() + "/home");
    }
}