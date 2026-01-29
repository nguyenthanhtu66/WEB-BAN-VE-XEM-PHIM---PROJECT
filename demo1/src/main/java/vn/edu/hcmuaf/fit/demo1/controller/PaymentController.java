package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.model.Cart;

import java.io.IOException;
import java.util.Map;

@WebServlet("/thanh-toan")
public class PaymentController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Kiểm tra đăng nhập
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?redirect=thanh-toan.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = (User) session.getAttribute("loggedUser");
        }

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?redirect=thanh-toan.jsp");
            return;
        }

        // Kiểm tra có dữ liệu thanh toán không
        String fromCart = request.getParameter("fromCart");
        String payNow = request.getParameter("payNow");

        boolean hasPaymentData = false;

        if ("true".equals(fromCart)) {
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null || cart.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }
            hasPaymentData = true;
        }
        else if ("true".equals(payNow)) {
            Map<String, Object> paymentData = (Map<String, Object>) session.getAttribute("paymentData");
            if (paymentData == null) {
                // Không có paymentData, có thể user reload trang
                // Có thể redirect về home hoặc show thông báo
                request.setAttribute("errorMessage", "Không tìm thấy thông tin vé. Vui lòng đặt vé lại.");
            } else {
                hasPaymentData = true;
            }
        }

        // Forward to payment page
        request.setAttribute("hasPaymentData", hasPaymentData);
        request.getRequestDispatcher("/thanh-toan.jsp").forward(request, response);
    }
}