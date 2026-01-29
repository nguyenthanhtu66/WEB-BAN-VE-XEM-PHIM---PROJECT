package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.model.Cart;
import vn.edu.hcmuaf.fit.demo1.model.CartItem;
import vn.edu.hcmuaf.fit.demo1.dao.BookedSeatDao;

import java.io.IOException;

@WebServlet("/cart")
public class CartController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy cart từ session
        HttpSession session = request.getSession(false);
        Cart cart = null;
        if (session != null) {
            cart = (Cart) session.getAttribute("cart");
        }

        if (cart == null) {
            cart = new Cart();
        }

        // Set attribute
        request.setAttribute("cart", cart);

        // Forward to JSP
        request.getRequestDispatcher("/Gio-hang.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);

        if (session != null) {
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart != null) {
                switch (action) {
                    case "remove":
                        int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
                        int seatId = Integer.parseInt(request.getParameter("seatId"));

                        // Release seat trong database
                        BookedSeatDao bookedSeatDao = new BookedSeatDao();
                        bookedSeatDao.releaseSeat(showtimeId, seatId);
                        System.out.println("✅ Released seat from database: showtimeId=" + showtimeId + ", seatId=" + seatId);

                        // Xóa khỏi cart
                        cart.removeItem(showtimeId, seatId);
                        break;

                    case "clear":
                        // Release tất cả seats trong cart
                        BookedSeatDao bookedSeatDao2 = new BookedSeatDao();
                        for (CartItem item : cart.getItems()) {
                            bookedSeatDao2.releaseSeat(item.getShowtimeId(), item.getSeatId());
                            System.out.println("✅ Released seat: showtimeId=" + item.getShowtimeId() + ", seatId=" + item.getSeatId());
                        }
                        cart.clear();
                        break;
                }
                session.setAttribute("cart", cart);
            }
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }
}