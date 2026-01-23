package vn.edu.hcmuaf.fit.demo1.service;

import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.dao.*;
import vn.edu.hcmuaf.fit.demo1.model.*;

import java.util.HashMap;
import java.util.Map;

public class CartService {

    private final MovieDao movieDao = new MovieDao();
    private final ShowtimeDao showtimeDao = new ShowtimeDao();
    private final RoomDao roomDao = new RoomDao();

    // Lấy thông tin phim
    public Movie getMovieInfo(int movieId) {
        return movieDao.getMovieById(movieId);
    }

    // Lấy thông tin showtime
    public Showtime getShowtimeInfo(int showtimeId) {
        return showtimeDao.getShowtimeById(showtimeId);
    }

    // Lấy thông tin phòng
    public Room getRoomInfo(int roomId) {
        return roomDao.getRoomById(roomId);
    }

    // Tính giá vé
    public double calculateTicketPrice(String ticketType) {
        switch(ticketType) {
            case "adult":
                return 100000;
            case "student":
                return 80000;
            case "child":
                return 60000;
            default:
                return 100000;
        }
    }

    // Lấy hoặc tạo giỏ hàng từ session
    public ShoppingCart getOrCreateCart(HttpSession session) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart == null) {
            cart = new ShoppingCart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    // Thêm vào giỏ hàng
    public boolean addToCart(HttpSession session, CartItem cartItem) {
        ShoppingCart cart = getOrCreateCart(session);
        return cart.addItem(cartItem);
    }

    // Cập nhật số lượng
    public boolean updateQuantity(HttpSession session, String itemId, int newQuantity) {
        if (newQuantity < 1) {
            return removeItem(session, itemId);
        }

        ShoppingCart cart = getOrCreateCart(session);
        return cart.updateQuantity(itemId, newQuantity);
    }

    // Xóa item
    public boolean removeItem(HttpSession session, String itemId) {
        ShoppingCart cart = getOrCreateCart(session);
        return cart.removeItem(itemId);
    }

    // Xóa toàn bộ giỏ hàng
    public void clearCart(HttpSession session) {
        ShoppingCart cart = getOrCreateCart(session);
        cart.clear();
    }

    // Áp dụng mã khuyến mãi
    public Map<String, Object> applyPromoCode(HttpSession session, String promoCode) {
        Map<String, Object> result = new HashMap<>();
        ShoppingCart cart = getOrCreateCart(session);

        // Kiểm tra mã khuyến mãi
        if (promoCode == null || promoCode.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "Vui lòng nhập mã khuyến mãi");
            return result;
        }

        // Demo: Giảm giá 10% cho mã "DISCOUNT10"
        if (promoCode.equalsIgnoreCase("DISCOUNT10")) {
            double discount = cart.getSubtotal() * 0.1;
            cart.setDiscount(discount);
            result.put("success", true);
            result.put("message", "Áp dụng mã giảm giá 10% thành công!");
            result.put("discount", discount);
            result.put("grandTotal", cart.getGrandTotal());
        } else {
            result.put("success", false);
            result.put("message", "Mã khuyến mãi không hợp lệ");
        }

        return result;
    }
}