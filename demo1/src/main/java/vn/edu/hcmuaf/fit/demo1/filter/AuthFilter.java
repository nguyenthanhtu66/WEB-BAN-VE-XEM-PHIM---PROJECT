package vn.edu.hcmuaf.fit.demo1.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.model.User;

import java.io.IOException;

@WebFilter("/profile")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        System.out.println("🔐 AuthFilter checking for /profile");

        // Kiểm tra xem user đã đăng nhập chưa
        User user = null;
        if (session != null) {
            user = (User) session.getAttribute("loggedUser");
            if (user == null) {
                user = (User) session.getAttribute("user");
                if (user != null) {
                    // Migrate sang loggedUser
                    session.setAttribute("loggedUser", user);
                    session.removeAttribute("user");
                }
            }
        }

        if (user == null) {
            System.out.println("❌ AuthFilter: No user, redirecting to login");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            return;
        }

        System.out.println("✅ AuthFilter: User authenticated: " + user.getEmail());
        chain.doFilter(request, response);
    }
}