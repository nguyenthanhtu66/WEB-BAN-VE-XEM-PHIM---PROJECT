package vn.edu.hcmuaf.fit.demo1.service;

import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.dao.*;
import vn.edu.hcmuaf.fit.demo1.model.*;

import java.util.*;

public class CartService {

    private final SeatDao seatDao = new SeatDao();
    private final BookedSeatDao bookedSeatDao = new BookedSeatDao();
    private final ShowtimeDao showtimeDao = new ShowtimeDao();
    private final MovieDao movieDao = new MovieDao();
    private final RoomDao roomDao = new RoomDao();

    // Lấy hoặc tạo giỏ hàng từ session
    public ShoppingCart getOrCreateCart(HttpSession session) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");

        if (cart == null) {
            cart = new ShoppingCart();
            session.setAttribute("cart", cart);
        }

        // Kiểm tra và làm mới trạng thái giữ ghế
        refreshSeatReservations(cart, session);

        return cart;
    }

    // Lưu giỏ hàng vào session
    public void saveCart(HttpSession session, ShoppingCart cart) {
        session.setAttribute("cart", cart);
    }

    // Xóa giỏ hàng
    public void clearCart(HttpSession session) {
        ShoppingCart cart = getOrCreateCart(session);
        releaseAllSeats(cart, session);
        session.removeAttribute("cart");
        session.removeAttribute("seatReservations");
    }

    // Thêm item vào giỏ hàng
    public boolean addToCart(HttpSession session, CartItem cartItem) {
        ShoppingCart cart = getOrCreateCart(session);

        // Kiểm tra ghế có sẵn không
        if (!verifySeatsAvailable(cartItem)) {
            return false;
        }

        // Giữ ghế tạm thời
        if (!reserveSeatsForItem(cartItem, session)) {
            return false;
        }

        cart.addItem(cartItem);
        saveCart(session, cart);
        return true;
    }

    // Cập nhật số lượng
    public boolean updateQuantity(HttpSession session, String itemId, int newQuantity) {
        ShoppingCart cart = getOrCreateCart(session);
        CartItem item = findCartItem(cart, itemId);

        if (item == null) {
            return false;
        }

        if (newQuantity <= 0) {
            return removeItem(session, itemId);
        }

        // Kiểm tra xem có đủ ghế không
        int oldQuantity = item.getQuantity();
        int quantityDiff = newQuantity - oldQuantity;

        if (quantityDiff > 0) {
            // Cần thêm ghế
            if (!canAddMoreSeats(item, quantityDiff)) {
                return false;
            }

            // Giữ thêm ghế
            if (!reserveAdditionalSeats(item, quantityDiff, session)) {
                return false;
            }
        } else if (quantityDiff < 0) {
            // Giảm số ghế, hủy bớt
            releaseExcessSeats(item, Math.abs(quantityDiff));
        }

        cart.updateItemQuantity(itemId, newQuantity);
        saveCart(session, cart);
        return true;
    }

    // Xóa item khỏi giỏ hàng
    public boolean removeItem(HttpSession session, String itemId) {
        ShoppingCart cart = getOrCreateCart(session);
        CartItem item = findCartItem(cart, itemId);

        if (item == null) {
            return false;
        }

        // Hủy giữ ghế
        releaseSeatsForItem(item);

        cart.removeItem(itemId);
        saveCart(session, cart);
        return true;
    }

    // Kiểm tra và làm mới trạng thái giữ ghế
    private void refreshSeatReservations(ShoppingCart cart, HttpSession session) {
        Iterator<CartItem> iterator = cart.getItems().iterator();
        boolean cartModified = false;

        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (!areSeatsStillReserved(item)) {
                // Ghế không còn được giữ, xóa item
                iterator.remove();
                cartModified = true;
            }
        }

        if (cartModified) {
            cart.calculateTotals();
            saveCart(session, cart);
        }
    }

    // Kiểm tra ghế có sẵn không
    private boolean verifySeatsAvailable(CartItem item) {
        String[] seatCodes = item.getSeats().split(", ");

        for (String seatCode : seatCodes) {
            Seat seat = seatDao.getSeatByCode(item.getRoomId(), seatCode.trim());
            if (seat == null || !seatDao.isSeatAvailable(seat.getId(), item.getShowtimeId())) {
                return false;
            }
        }
        return true;
    }

    // Giữ ghế cho item
    private boolean reserveSeatsForItem(CartItem item, HttpSession session) {
        String[] seatCodes = item.getSeats().split(", ");

        // Lấy userId từ session
        User user = (User) session.getAttribute("user");
        int userId = user != null ? user.getId() : 0;

        // Giữ ghế trong 5 phút
        boolean reserved = bookedSeatDao.reserveSeatsTemporarily(
                item.getShowtimeId(),
                item.getRoomId(),
                seatCodes,
                userId,
                5
        );

        if (reserved) {
            // Lưu thông tin giữ ghế vào session
            saveSeatReservation(session, item.getShowtimeId(), item.getRoomId(), seatCodes);
        }

        return reserved;
    }

    // Lưu thông tin giữ ghế vào session
    private void saveSeatReservation(HttpSession session, int showtimeId, int roomId, String[] seatCodes) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> reservations =
                (List<Map<String, Object>>) session.getAttribute("seatReservations");

        if (reservations == null) {
            reservations = new ArrayList<>();
        }

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("showtimeId", showtimeId);
        reservation.put("roomId", roomId);
        reservation.put("seatCodes", seatCodes);
        reservation.put("reservedAt", System.currentTimeMillis());

        reservations.add(reservation);
        session.setAttribute("seatReservations", reservations);
    }

    // Kiểm tra ghế vẫn được giữ
    private boolean areSeatsStillReserved(CartItem item) {
        String[] seatCodes = item.getSeats().split(", ");

        for (String seatCode : seatCodes) {
            if (!bookedSeatDao.isSeatReserved(item.getShowtimeId(), seatCode.trim())) {
                return false;
            }
        }
        return true;
    }

    // Hủy giữ ghế cho item
    private void releaseSeatsForItem(CartItem item) {
        String[] seatCodes = item.getSeats().split(", ");
        bookedSeatDao.releaseSeats(item.getShowtimeId(), seatCodes);
    }

    // Hủy tất cả ghế trong giỏ
    private void releaseAllSeats(ShoppingCart cart, HttpSession session) {
        for (CartItem item : cart.getItems()) {
            releaseSeatsForItem(item);
        }
        session.removeAttribute("seatReservations");
    }

    // Tìm item trong giỏ
    private CartItem findCartItem(ShoppingCart cart, String itemId) {
        for (CartItem item : cart.getItems()) {
            if (item.getId().equals(itemId)) {
                return item;
            }
        }
        return null;
    }

    // Kiểm tra có thể thêm ghế không
    private boolean canAddMoreSeats(CartItem item, int additionalSeats) {
        // Lấy danh sách ghế còn trống trong phòng
        int availableSeats = seatDao.countAvailableSeats(item.getRoomId(), item.getShowtimeId());
        return availableSeats >= additionalSeats;
    }

    // Giữ thêm ghế
    private boolean reserveAdditionalSeats(CartItem item, int additionalSeats, HttpSession session) {
        // Logic chọn thêm ghế (cần implement chi tiết)
        // Hiện tại trả về true giả định
        return true;
    }

    // Hủy bớt ghế
    private void releaseExcessSeats(CartItem item, int excessSeats) {
        // Logic chọn ghế để hủy (cần implement chi tiết)
    }

    // Lấy thông tin suất chiếu
    public Showtime getShowtimeInfo(int showtimeId) {
        return showtimeDao.getShowtimeById(showtimeId);
    }

    // Lấy thông tin phim
    public Movie getMovieInfo(int movieId) {
        return movieDao.getMovieById(movieId);
    }

    // Lấy thông tin phòng
    public Room getRoomInfo(int roomId) {
        return roomDao.getRoomById(roomId);
    }

    // Tính giá vé
    public double calculateTicketPrice(String ticketType) {
        switch (ticketType.toLowerCase()) {
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
}