package vn.edu.hcmuaf.fit.demo1.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import vn.edu.hcmuaf.fit.demo1.model.User;

import java.io.IOException;

@WebFilter("/admin/*")
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Lấy session (không tạo mới)
        HttpSession session = request.getSession(false);

        // ===== CHƯA LOGIN =====
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Lấy user từ session
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // ===== KIỂM TRA QUYỀN ADMIN =====
        if ("admin".equalsIgnoreCase(user.getRole())) {
            chain.doFilter(req, res); // Cho phép truy cập
            return;
        }

        // ===== KHÔNG PHẢI ADMIN =====
        response.sendRedirect(request.getContextPath() + "/403.jsp");

    }
}
