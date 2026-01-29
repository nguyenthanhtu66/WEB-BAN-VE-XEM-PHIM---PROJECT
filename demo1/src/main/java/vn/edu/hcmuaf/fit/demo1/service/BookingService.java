package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.*;
import vn.edu.hcmuaf.fit.demo1.model.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookingService {

    private final RoomDao roomDao = new RoomDao();
    private final SeatDao seatDao = new SeatDao();
    private final ShowtimeDao showtimeDao = new ShowtimeDao();
    private final TicketTypeDao ticketTypeDao = new TicketTypeDao();
    private final BookedSeatDao bookedSeatDao = new BookedSeatDao();
    private final MovieDao movieDao = new MovieDao();

    // Giá vé cơ bản (100,000 VNĐ)
    private static final double BASE_TICKET_PRICE = 100000.0;

    // ==================== VALIDATION METHODS ====================

    /**
     * Validate booking data
     */
    public boolean validateBooking(int movieId, int roomId, int showtimeId, int ticketTypeId, int seatId) {
        System.out.println("=== VALIDATE BOOKING ===");
        System.out.println("movieId=" + movieId + ", showtimeId=" + showtimeId +
                ", ticketTypeId=" + ticketTypeId + ", seatId=" + seatId);

        try {
            // Kiểm tra showtime có hợp lệ không
            Showtime showtime = showtimeDao.getShowtimeById(showtimeId);
            if (showtime == null || !showtime.isActive()) {
                System.out.println("❌ Showtime not found or inactive");
                return false;
            }

            if (roomId > 0 && showtime.getRoomId() != roomId) {
                System.out.println("❌ Room ID mismatch");
                return false;
            }

            if (showtime.getMovieId() != movieId) {
                System.out.println("❌ Movie ID mismatch");
                return false;
            }

            // Kiểm tra showtime date
            if (showtime.getShowDate().isBefore(LocalDate.now())) {
                System.out.println("❌ Showtime date is in the past");
                return false;
            }

            // Kiểm tra ticket type có hợp lệ không
            if (!ticketTypeDao.isTicketTypeValid(ticketTypeId)) {
                System.out.println("❌ Ticket type invalid");
                return false;
            }

            // Kiểm tra seat có tồn tại và active không
            Seat seat = seatDao.getSeatById(seatId);
            if (seat == null || !seat.isActive()) {
                System.out.println("❌ Seat not found or inactive");
                return false;
            }

            // Kiểm tra seat có thuộc đúng phòng không
            if (seat.getRoomId() != showtime.getRoomId()) {
                System.out.println("❌ Seat does not belong to showtime room");
                return false;
            }

            System.out.println("✅ All basic validations passed");
            System.out.println("=== END VALIDATE BOOKING ===");

            return true;

        } catch (Exception e) {
            System.err.println("Validation error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Validate booking data with session ID
     */
    public boolean validateBookingWithSession(int movieId, int roomId, int showtimeId, int ticketTypeId,
                                              int seatId, String sessionId) {
        System.out.println("=== VALIDATE BOOKING WITH SESSION ===");
        System.out.println("movieId=" + movieId + ", showtimeId=" + showtimeId +
                ", ticketTypeId=" + ticketTypeId + ", seatId=" + seatId + ", sessionId=" + sessionId);

        // Basic validation first
        if (!validateBooking(movieId, roomId, showtimeId, ticketTypeId, seatId)) {
            return false;
        }

        // Check seat availability for this session
        boolean isAvailable = isSeatAvailable(showtimeId, seatId, sessionId);
        System.out.println("Seat available for session: " + isAvailable);

        if (!isAvailable) {
            System.out.println("❌ Seat not available for this session");
            return false;
        }

        System.out.println("✅ All validations with session passed");
        System.out.println("=== END VALIDATE BOOKING WITH SESSION ===");
        return true;
    }

    // ==================== ROOM ====================

    public List<Room> getAllActiveRooms() {
        return roomDao.getAllActiveRooms();
    }

    public List<Room> getRoomsByMovieId(int movieId) {
        System.out.println("BookingService.getRoomsByMovieId - movieId: " + movieId);
        List<Room> rooms = roomDao.getRoomsByMovieId(movieId);
        System.out.println("BookingService.getRoomsByMovieId - returned " +
                (rooms != null ? rooms.size() : 0) + " rooms");
        return rooms;
    }

    public Room getRoomById(int roomId) {
        return roomDao.getRoomById(roomId);
    }

    // ==================== SEAT ====================

    public List<Seat> getSeatsByRoomId(int roomId) {
        return seatDao.getSeatsByRoomId(roomId);
    }

    public Seat getSeatById(int seatId) {
        return seatDao.getSeatById(seatId);
    }

    public List<String> getRowNumbersByRoom(int roomId) {
        return seatDao.getRowNumbersByRoom(roomId);
    }

    // ==================== SHOWTIME ====================

    public List<Showtime> getShowtimesByMovieAndRoom(int movieId, int roomId) {
        return showtimeDao.getShowtimesByMovieAndRoom(movieId, roomId);
    }

    public List<LocalDate> getAvailableDatesByMovieAndRoom(int movieId, int roomId) {
        return showtimeDao.getAvailableDatesByMovieAndRoom(movieId, roomId);
    }

    public List<LocalTime> getAvailableTimesByMovieRoomAndDate(int movieId, int roomId, LocalDate showDate) {
        return showtimeDao.getAvailableTimesByMovieRoomAndDate(movieId, roomId, showDate);
    }

    public Showtime getShowtimeById(int showtimeId) {
        return showtimeDao.getShowtimeById(showtimeId);
    }

    // ==================== TICKET TYPE ====================

    public List<TicketType> getAllActiveTicketTypes() {
        return ticketTypeDao.getAllActiveTicketTypes();
    }

    public TicketType getTicketTypeById(int ticketTypeId) {
        return ticketTypeDao.getTicketTypeById(ticketTypeId);
    }

    // ==================== BOOKED SEATS ====================

    public List<Integer> getBookedSeatIdsByShowtime(int showtimeId) {
        return bookedSeatDao.getBookedSeatIdsByShowtime(showtimeId);
    }

    public List<Integer> getReservedSeatIdsByShowtime(int showtimeId, String sessionId) {
        return bookedSeatDao.getReservedSeatIdsByShowtime(showtimeId, sessionId);
    }

    public boolean isSeatAvailable(int showtimeId, int seatId) {
        return bookedSeatDao.isSeatAvailable(showtimeId, seatId, "");
    }

    public boolean isSeatAvailable(int showtimeId, int seatId, String sessionId) {
        return bookedSeatDao.isSeatAvailable(showtimeId, seatId, sessionId);
    }

    public boolean reserveSeat(int showtimeId, int seatId, Integer userId, String sessionId) {
        // Kiểm tra ghế có khả dụng cho session này không
        BookedSeatDao bookedSeatDao = new BookedSeatDao();
        boolean isAvailable;

        if (userId != null) {
            isAvailable = bookedSeatDao.isSeatAvailableForUser(showtimeId, seatId, userId);
        } else if (sessionId != null && !sessionId.isEmpty()) {
            isAvailable = bookedSeatDao.isSeatAvailable(showtimeId, seatId, sessionId);
        } else {
            isAvailable = bookedSeatDao.isSeatAvailable(showtimeId, seatId);
        }

        if (!isAvailable) {
            System.out.println("❌ Seat not available for reservation");
            return false;
        }
        return bookedSeatDao.reserveSeat(showtimeId, seatId, userId, sessionId);
    }

    public boolean releaseSeat(int showtimeId, int seatId) {
        return bookedSeatDao.releaseSeat(showtimeId, seatId);
    }

    public boolean releaseUserSeats(int showtimeId, Integer userId) {
        return bookedSeatDao.releaseUserSeats(showtimeId, userId);
    }

    public int getAvailableSeatsCount(int showtimeId, int roomId) {
        return bookedSeatDao.getAvailableSeatsCount(showtimeId, roomId);
    }

    public void releaseExpiredReservations() {
        bookedSeatDao.releaseAllExpiredReservations();
    }

    // ==================== BOOKING LOGIC ====================

    /**
     * Tính giá vé dựa trên loại vé
     */
    public double calculateTicketPrice(int ticketTypeId) {
        TicketType ticketType = ticketTypeDao.getTicketTypeById(ticketTypeId);
        if (ticketType == null) {
            return BASE_TICKET_PRICE;
        }
        return ticketType.getActualPrice(BASE_TICKET_PRICE);
    }

    /**
     * Lấy thông tin đầy đủ cho CartItem
     */
    public CartItem createCartItem(int movieId, int showtimeId, int seatId, int ticketTypeId) {
        System.out.println("Creating cart item: movieId=" + movieId + ", showtimeId=" + showtimeId +
                ", seatId=" + seatId + ", ticketTypeId=" + ticketTypeId);

        try {
            // Lấy thông tin movie
            Movie movie = movieDao.getMovieById(movieId);
            if (movie == null) {
                System.out.println("Movie not found: " + movieId);
                return null;
            }

            // Lấy thông tin showtime
            Showtime showtime = showtimeDao.getShowtimeById(showtimeId);
            if (showtime == null) {
                System.out.println("Showtime not found: " + showtimeId);
                return null;
            }

            // Lấy thông tin room
            Room room = roomDao.getRoomById(showtime.getRoomId());
            if (room == null) {
                System.out.println("Room not found: " + showtime.getRoomId());
                return null;
            }

            // Lấy thông tin seat
            Seat seat = seatDao.getSeatById(seatId);
            if (seat == null) {
                System.out.println("Seat not found: " + seatId);
                return null;
            }

            // Lấy thông tin ticket type
            TicketType ticketType = ticketTypeDao.getTicketTypeById(ticketTypeId);
            if (ticketType == null) {
                System.out.println("Ticket type not found: " + ticketTypeId);
                return null;
            }

            // Tính giá
            double price = calculateTicketPrice(ticketTypeId);

            // Tạo CartItem
            CartItem cartItem = new CartItem(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getPosterUrl(),
                    showtime.getId(),
                    showtime.getShowDate(),
                    showtime.getShowTime(),
                    room.getId(),
                    room.getRoomName(),
                    seat.getId(),
                    seat.getSeatCode(),
                    ticketType.getId(),
                    ticketType.getTypeName(),
                    price
            );

            System.out.println("CartItem created successfully");
            return cartItem;

        } catch (Exception e) {
            System.err.println("Error creating cart item: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Kiểm tra xem seat có trong cart của session không
     */
    public boolean isSeatInCart(int showtimeId, int seatId, HttpSession session) {
        if (session == null) return false;

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) return false;

        return cart.containsSeat(showtimeId, seatId);
    }

    /**
     * Lấy danh sách seat IDs trong cart cho một showtime
     */
    public List<Integer> getCartSeatIdsForShowtime(int showtimeId, HttpSession session) {
        if (session == null) return List.of();

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) return List.of();

        return cart.getSeatIdsForShowtime(showtimeId);
    }

    /**
     * Xóa tất cả seat reservations của session khi clear cart
     */
    public boolean clearSessionCartSeats(HttpSession session) {
        if (session == null) return false;

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return false;

        String sessionId = session.getId();
        boolean allReleased = true;

        for (CartItem item : cart.getItems()) {
            boolean released = bookedSeatDao.releaseSeat(item.getShowtimeId(), item.getSeatId());
            if (!released) {
                allReleased = false;
                System.err.println("Failed to release seat: showtimeId=" +
                        item.getShowtimeId() + ", seatId=" + item.getSeatId());
            }
        }

        return allReleased;
    }

    /**
     * Lấy trạng thái chi tiết của seat
     */
    public String getSeatStatus(int showtimeId, int seatId, String sessionId, HttpSession session) {
        // Kiểm tra nếu đã booked
        if (bookedSeatDao.isSeatBooked(showtimeId, seatId)) {
            return "booked";
        }

        // Kiểm tra nếu đang reserved bởi session khác
        if (bookedSeatDao.isSeatReservedByOtherSession(showtimeId, seatId, sessionId)) {
            return "reserved";
        }

        // Kiểm tra nếu trong cart của session này
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null && cart.containsSeat(showtimeId, seatId)) {
            return "in_cart";
        }

        // Kiểm tra nếu đang reserved bởi session này
        if (bookedSeatDao.isSeatReservedBySession(showtimeId, seatId, sessionId)) {
            return "reserved_by_me";
        }

        return "available";
    }

    /**
     * Get complete seat map with all statuses
     */
    public List<Map<String, Object>> getSeatMapWithStatuses(int roomId, int showtimeId, String sessionId, HttpSession httpSession) {
        List<Seat> seats = seatDao.getSeatsByRoomId(roomId);
        List<Integer> bookedSeatIds = bookedSeatDao.getBookedSeatIdsByShowtime(showtimeId);
        List<Integer> reservedSeatIds = bookedSeatDao.getReservedSeatIdsByShowtime(showtimeId, sessionId);

        return seats.stream().map(seat -> {
            Map<String, Object> seatInfo = new HashMap<>();
            seatInfo.put("id", seat.getId());
            seatInfo.put("seatCode", seat.getSeatCode());
            seatInfo.put("rowNumber", seat.getRowNumber());
            seatInfo.put("seatNumber", seat.getSeatNumber());
            seatInfo.put("seatType", seat.getSeatType());

            boolean isBooked = bookedSeatIds.contains(seat.getId());
            boolean isReserved = reservedSeatIds.contains(seat.getId());
            boolean isInCart = httpSession != null &&
                    ((Cart) httpSession.getAttribute("cart") != null &&
                            ((Cart) httpSession.getAttribute("cart")).containsSeat(showtimeId, seat.getId()));

            // Determine status
            String status;
            if (isBooked) {
                status = "booked";
            } else if (isReserved) {
                status = "reserved";
            } else if (isInCart) {
                status = "in_cart";
            } else {
                status = "available";
            }

            seatInfo.put("status", status);
            seatInfo.put("isBooked", isBooked);
            seatInfo.put("isReserved", isReserved);
            seatInfo.put("isInCart", isInCart);
            seatInfo.put("isAvailable", !isBooked && !isReserved && !isInCart);

            return seatInfo;
        }).collect(Collectors.toList());
    }

    /**
     * Get movie by ID
     */
    public Movie getMovieById(int movieId) {
        return movieDao.getMovieById(movieId);
    }
}