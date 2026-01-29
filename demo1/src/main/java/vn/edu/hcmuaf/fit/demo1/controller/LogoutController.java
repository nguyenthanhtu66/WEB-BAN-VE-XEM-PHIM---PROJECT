package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            // Xóa CẢ HAI attributes
            session.removeAttribute("loggedUser");
            session.removeAttribute("user");

            // Có thể xóa cart
            session.removeAttribute("cart");

            // Hủy session
            session.invalidate();

            System.out.println("✅ User logged out successfully");
        }

        // Chuyển hướng về trang chủ
        response.sendRedirect(request.getContextPath() + "/home");
    }
}