package vn.edu.hcmuaf.fit.demo1.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.model.Cart;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/cart-info")
public class CartInfoController extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            HttpSession session = request.getSession(false);
            Cart cart = null;

            if (session != null) {
                cart = (Cart) session.getAttribute("cart");
            }

            result.put("success", true);
            result.put("cartSize", cart != null ? cart.getTotalItems() : 0);
            result.put("totalAmount", cart != null ? cart.getTotalAmount() : 0);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error getting cart info");
        }

        response.getWriter().write(gson.toJson(result));
    }
}