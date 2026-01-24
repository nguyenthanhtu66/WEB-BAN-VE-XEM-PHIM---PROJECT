package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.*;
import vn.edu.hcmuaf.fit.demo1.model.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CartService {

    private final CartDao cartDao = new CartDao();
    private final MovieDao movieDao = new MovieDao();
    private final ShowtimeDao showtimeDao = new ShowtimeDao();
    private final RoomDao roomDao = new RoomDao();
    private final BookingDao bookingDao = new BookingDao();
    private final SeatDao seatDao = new SeatDao();
    private final TicketTypeDao ticketTypeDao = new TicketTypeDao();

    private static final double BASE_PRICE = 100000.0;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Lấy giỏ hàng từ session
    public Cart getCartFromSession(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    // Thêm item vào giỏ hàng
    public Map<String, Object> addToCart(HttpSession session, int movieId, int showtimeId,
                                         int roomId, String ticketType, int quantity,
                                         List<String> seatCodes, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Kiểm tra thông tin
            Movie movie = movieDao.getMovieById(movieId);
            if (movie == null) {
                result.put("success", false);
                result.put("message", "Phim không tồn tại");
                return result;
            }

            Showtime showtime = showtimeDao.getShowtimeById(showtimeId);
            if (showtime == null) {
                result.put("success", false);
                result.put("message", "Suất chiếu không tồn tại");
                return result;
            }

            Room room = roomDao.getRoomById(roomId);
            if (room == null) {
                result.put("success", false);
                result.put("message", "Phòng chiếu không tồn tại");
                return result;
            }

            // 2. Kiểm tra ghế có sẵn không
            if (seatCodes != null && !seatCodes.isEmpty()) {
                List<String> unavailableSeats = new ArrayList<>();

                // Lấy seatIds từ seatCodes
                List<Integer> seatIds = new ArrayList<>();
                for (String seatCode : seatCodes) {
                    Seat seat = seatDao.getSeatByCode(roomId, seatCode);
                    if (seat != null) {
                        seatIds.add(seat.getId());
                    }
                }

                List<String> bookedSeats = seatDao.getUnavailableSeatCodes(showtimeId, seatIds);
                if (!bookedSeats.isEmpty()) {
                    result.put("success", false);
                    result.put("message", "Một số ghế đã được đặt: " + String.join(", ", bookedSeats));
                    return result;
                }
            }

            // 3. Tính giá vé
            double unitPrice = getTicketPrice(ticketType);

            // 4. Tạo CartItem
            String itemId = UUID.randomUUID().toString();

            CartItem item = new CartItem();
            item.setId(itemId);
            item.setMovieId(movieId);
            item.setMovieTitle(movie.getTitle());
            item.setPosterUrl(movie.getPosterUrl());
            item.setShowtimeId(showtimeId);
            item.setShowtime(formatShowtime(showtime));
            item.setRoomId(roomId);
            item.setRoom(room.getRoomName());
            item.setTicketType(ticketType);
            item.setQuantity(quantity);
            item.setSeats(seatCodes != null ? String.join(", ", seatCodes) : "");
            item.setUnitPrice(unitPrice);

            // 5. Giữ ghế tạm thời nếu có chọn ghế
            if (seatCodes != null && !seatCodes.isEmpty() && userId > 0) {
                // Chuyển seatCodes thành seatIds
                List<Integer> seatIds = new ArrayList<>();
                for (String seatCode : seatCodes) {
                    Seat seat = seatDao.getSeatByCode(roomId, seatCode);
                    if (seat != null) {
                        seatIds.add(seat.getId());
                    }
                }

                // Giữ ghế trong 5 phút
                boolean reserved = bookingDao.reserveMultipleSeats(showtimeId, seatIds, userId);
                if (!reserved) {
                    result.put("success", false);
                    result.put("message", "Không thể giữ ghế. Vui lòng thử lại.");
                    return result;
                }
            }

            // 6. Thêm vào giỏ hàng
            Cart cart = getCartFromSession(session);
            cart.addItem(item);

            // 7. Cập nhật session
            session.setAttribute("cart", cart);

            result.put("success", true);
            result.put("message", "Đã thêm vào giỏ hàng");
            result.put("cartItemCount", cart.getTotalItems());
            result.put("itemId", itemId);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống: " + e.getMessage());
        }

        return result;
    }

    // Cập nhật số lượng
    public Map<String, Object> updateQuantity(HttpSession session, String itemId, int newQuantity) {
        Map<String, Object> result = new HashMap<>();

        try {
            Cart cart = getCartFromSession(session);
            CartItem item = cart.getItemById(itemId);

            if (item == null) {
                result.put("success", false);
                result.put("message", "Item không tồn tại trong giỏ hàng");
                return result;
            }

            if (newQuantity < 1) {
                result.put("success", false);
                result.put("message", "Số lượng phải lớn hơn 0");
                return result;
            }

            item.setQuantity(newQuantity);
            cart.calculateTotals();
            session.setAttribute("cart", cart);

            result.put("success", true);
            result.put("message", "Đã cập nhật số lượng");
            result.put("cartItemCount", cart.getTotalItems());
            result.put("subtotal", cart.getSubtotal());
            result.put("grandTotal", cart.getGrandTotal());

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống");
        }

        return result;
    }

    // Xóa item khỏi giỏ hàng và release ghế
    public Map<String, Object> removeFromCart(HttpSession session, String itemId, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            Cart cart = getCartFromSession(session);
            CartItem item = cart.getItemById(itemId);

            if (item != null) {
                // Release ghế nếu có
                if (item.getSeats() != null && !item.getSeats().isEmpty() &&
                        item.getShowtimeId() > 0 && userId > 0) {

                    String[] seatCodes = item.getSeats().split(", ");
                    List<Integer> seatIds = new ArrayList<>();

                    for (String seatCode : seatCodes) {
                        Seat seat = seatDao.getSeatByCode(item.getRoomId(), seatCode.trim());
                        if (seat != null) {
                            seatIds.add(seat.getId());
                        }
                    }

                    bookingDao.releaseMultipleSeats(item.getShowtimeId(), seatIds, userId);
                }

                cart.removeItem(itemId);
                session.setAttribute("cart", cart);

                result.put("success", true);
                result.put("message", "Đã xóa khỏi giỏ hàng");
                result.put("cartItemCount", cart.getTotalItems());
            } else {
                result.put("success", false);
                result.put("message", "Item không tồn tại");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống");
        }

        return result;
    }

    // Xóa toàn bộ giỏ hàng
    public Map<String, Object> clearCart(HttpSession session, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            Cart cart = getCartFromSession(session);

            // Release tất cả ghế đang giữ
            for (CartItem item : cart.getItems()) {
                if (item.getSeats() != null && !item.getSeats().isEmpty() &&
                        item.getShowtimeId() > 0 && userId > 0) {

                    String[] seatCodes = item.getSeats().split(", ");
                    List<Integer> seatIds = new ArrayList<>();

                    for (String seatCode : seatCodes) {
                        Seat seat = seatDao.getSeatByCode(item.getRoomId(), seatCode.trim());
                        if (seat != null) {
                            seatIds.add(seat.getId());
                        }
                    }

                    bookingDao.releaseMultipleSeats(item.getShowtimeId(), seatIds, userId);
                }
            }

            cart.clear();
            session.setAttribute("cart", cart);

            result.put("success", true);
            result.put("message", "Đã xóa toàn bộ giỏ hàng");

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống");
        }

        return result;
    }

    // Cập nhật ghế
    public Map<String, Object> updateSeats(HttpSession session, String itemId,
                                           List<String> newSeats, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            Cart cart = getCartFromSession(session);
            CartItem item = cart.getItemById(itemId);

            if (item == null) {
                result.put("success", false);
                result.put("message", "Item không tồn tại");
                return result;
            }

            // Release ghế cũ
            if (item.getSeats() != null && !item.getSeats().isEmpty() &&
                    item.getShowtimeId() > 0 && userId > 0) {

                String[] oldSeatCodes = item.getSeats().split(", ");
                List<Integer> oldSeatIds = new ArrayList<>();

                for (String seatCode : oldSeatCodes) {
                    Seat seat = seatDao.getSeatByCode(item.getRoomId(), seatCode.trim());
                    if (seat != null) {
                        oldSeatIds.add(seat.getId());
                    }
                }

                bookingDao.releaseMultipleSeats(item.getShowtimeId(), oldSeatIds, userId);
            }

            // Giữ ghế mới
            if (newSeats != null && !newSeats.isEmpty() && userId > 0) {
                List<Integer> newSeatIds = new ArrayList<>();
                for (String seatCode : newSeats) {
                    Seat seat = seatDao.getSeatByCode(item.getRoomId(), seatCode.trim());
                    if (seat != null) {
                        newSeatIds.add(seat.getId());
                    }
                }

                boolean reserved = bookingDao.reserveMultipleSeats(
                        item.getShowtimeId(), newSeatIds, userId);

                if (!reserved) {
                    result.put("success", false);
                    result.put("message", "Không thể giữ ghế mới");
                    return result;
                }
            }

            // Cập nhật item
            item.setSeats(newSeats != null ? String.join(", ", newSeats) : "");
            item.setQuantity(newSeats != null ? newSeats.size() : 1);

            cart.calculateTotals();
            session.setAttribute("cart", cart);

            result.put("success", true);
            result.put("message", "Đã cập nhật ghế");
            result.put("cartItemCount", cart.getTotalItems());

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống");
        }

        return result;
    }

    // Áp dụng mã khuyến mãi
    public Map<String, Object> applyPromoCode(HttpSession session, String promoCode) {
        Map<String, Object> result = new HashMap<>();

        try {
            Cart cart = getCartFromSession(session);
            boolean applied = cart.applyPromoCode(promoCode);

            if (applied) {
                session.setAttribute("cart", cart);
                result.put("success", true);
                result.put("message", "Áp dụng mã khuyến mãi thành công");
                result.put("discount", cart.getDiscount());
                result.put("grandTotal", cart.getGrandTotal());
            } else {
                result.put("success", false);
                result.put("message", "Mã khuyến mãi không hợp lệ");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống");
        }

        return result;
    }

    // Helper methods
    private double getTicketPrice(String ticketType) {
        switch (ticketType) {
            case "adult":
                return BASE_PRICE;
            case "student":
                return BASE_PRICE * 0.8; // 80%
            case "child":
                return BASE_PRICE * 0.6; // 60%
            case "u22":
                return BASE_PRICE * 0.55; // 55%
            default:
                return BASE_PRICE;
        }
    }

    private String formatShowtime(Showtime showtime) {
        if (showtime == null) return "";

        String date = showtime.getShowDate() != null ?
                showtime.getShowDate().format(DATE_FORMATTER) : "";
        String time = showtime.getShowTime() != null ?
                showtime.getShowTime().format(TIME_FORMATTER) : "";

        return date + " " + time;
    }

    // Xử lý quick booking
    public Map<String, Object> processQuickBooking(int movieId, int roomId,
                                                   String showtimeStr, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Parse showtime
            String[] parts = showtimeStr.split("T");
            if (parts.length != 2) {
                result.put("success", false);
                result.put("message", "Định dạng thời gian không hợp lệ");
                return result;
            }

            LocalDate showDate = LocalDate.parse(parts[0]);
            LocalTime showTime = LocalTime.parse(parts[1]);

            // 2. Kiểm tra phòng có sẵn không
            boolean roomAvailable = showtimeDao.isRoomAvailable(roomId, showDate, showTime);
            if (!roomAvailable) {
                result.put("success", false);
                result.put("message", "Phòng đã có suất chiếu vào thời gian này");
                return result;
            }

            // 3. Tạo hoặc lấy showtime
            Integer showtimeId = showtimeDao.createShowtime(movieId, roomId, showDate, showTime);
            if (showtimeId == null || showtimeId <= 0) {
                result.put("success", false);
                result.put("message", "Không thể tạo suất chiếu");
                return result;
            }

            // 4. Lấy thông tin bổ sung
            Movie movie = movieDao.getMovieById(movieId);
            Room room = roomDao.getRoomById(roomId);
            Showtime showtime = showtimeDao.getShowtimeById(showtimeId);

            result.put("success", true);
            result.put("message", "Sẵn sàng đặt vé");
            result.put("movieId", movieId);
            result.put("movieTitle", movie != null ? movie.getTitle() : "");
            result.put("roomId", roomId);
            result.put("roomName", room != null ? room.getRoomName() : "");
            result.put("showtimeId", showtimeId);
            result.put("showtime", formatShowtime(showtime));

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống: " + e.getMessage());
        }

        return result;
    }
}