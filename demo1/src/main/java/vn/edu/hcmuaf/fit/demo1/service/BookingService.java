package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.BookingDao;
import vn.edu.hcmuaf.fit.demo1.model.BookedSeat;
import vn.edu.hcmuaf.fit.demo1.model.SeatStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BookingService {

    private final BookingDao bookingDao = new BookingDao();
    private final int RESERVATION_DURATION = 300; // 5 phút = 300 giây

    // ==================== SEAT RESERVATION ====================

    /**
     * Giữ ghế tạm thời
     */
    public Map<String, Object> reserveSeats(int showtimeId, int roomId, List<String> seatCodes, Integer userId) {
        Map<String, Object> result = new HashMap<>();

        // Tạo reservation ID mới
        String reservationId = UUID.randomUUID().toString();

        // Thời gian hết hạn
        LocalDateTime reservedUntil = LocalDateTime.now().plus(RESERVATION_DURATION, ChronoUnit.SECONDS);

        try {
            // Kiểm tra từng ghế có còn trống không
            SeatService seatService = new SeatService();

            for (String seatCode : seatCodes) {
                // Lấy thông tin ghế
                var seat = seatService.getSeatByCode(roomId, seatCode);
                if (seat == null) {
                    result.put("success", false);
                    result.put("message", "Ghế " + seatCode + " không tồn tại");
                    return result;
                }

                // Kiểm tra trạng thái ghế
                SeatStatus status = bookingDao.checkSeatStatus(showtimeId, seat.getId());
                if (status != SeatStatus.AVAILABLE) {
                    result.put("success", false);
                    result.put("message", "Ghế " + seatCode + " đã có người đặt");
                    return result;
                }
            }

            // Tạo booked seat objects
            for (String seatCode : seatCodes) {
                var seat = seatService.getSeatByCode(roomId, seatCode);

                BookedSeat bookedSeat = new BookedSeat();
                bookedSeat.setShowtimeId(showtimeId);
                bookedSeat.setSeatId(seat.getId());
                bookedSeat.setUserId(userId);
                bookedSeat.setStatus(SeatStatus.RESERVED);
                bookedSeat.setReservationId(reservationId);
                bookedSeat.setReservedUntil(reservedUntil);
                bookedSeat.setCreatedAt(LocalDateTime.now());

                // Giữ ghế
                boolean success = bookingDao.reserveSeat(bookedSeat);
                if (!success) {
                    result.put("success", false);
                    result.put("message", "Không thể giữ ghế " + seatCode);
                    return result;
                }
            }

            result.put("success", true);
            result.put("reservationId", reservationId);
            result.put("seatCount", seatCodes.size());
            result.put("reservedUntil", reservedUntil.toString());
            result.put("reservationTime", RESERVATION_DURATION);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }

        return result;
    }

    /**
     * Giữ một ghế duy nhất
     */
    public boolean reserveSeat(int showtimeId, int seatId, Integer userId, String reservationId) {
        BookedSeat bookedSeat = new BookedSeat();
        bookedSeat.setShowtimeId(showtimeId);
        bookedSeat.setSeatId(seatId);
        bookedSeat.setUserId(userId);
        bookedSeat.setStatus(SeatStatus.RESERVED);
        bookedSeat.setReservationId(reservationId);
        bookedSeat.setReservedUntil(LocalDateTime.now().plus(RESERVATION_DURATION, ChronoUnit.SECONDS));
        bookedSeat.setCreatedAt(LocalDateTime.now());

        return bookingDao.reserveSeat(bookedSeat);
    }

    /**
     * Hủy giữ ghế theo reservationId
     */
    public boolean releaseSeats(String reservationId) {
        return bookingDao.releaseSeats(reservationId);
    }

    /**
     * Hủy giữ ghế theo showtime và seat
     */
    public boolean releaseSeat(int showtimeId, int seatId) {
        return bookingDao.releaseSeat(showtimeId, seatId);
    }

    // ==================== SEAT STATUS ====================

    /**
     * Kiểm tra trạng thái ghế
     */
    public SeatStatus checkSeatStatus(int showtimeId, int seatId) {
        return bookingDao.checkSeatStatus(showtimeId, seatId);
    }

    /**
     * Lấy trạng thái của tất cả ghế trong suất chiếu
     */
    public Map<String, String> getAllSeatStatus(int showtimeId, int roomId) {
        return bookingDao.getAllSeatStatus(showtimeId, roomId);
    }

    /**
     * Kiểm tra xem user có đang giữ ghế này không
     */
    public boolean isSeatReservedByUser(int showtimeId, int seatId, int userId) {
        return bookingDao.isSeatReservedByUser(showtimeId, seatId, userId);
    }

    // ==================== RESERVATION MANAGEMENT ====================

    /**
     * Lấy danh sách ghế theo reservation
     */
    public List<BookedSeat> getSeatsByReservation(String reservationId) {
        return bookingDao.getSeatsByReservation(reservationId);
    }

    /**
     * Kiểm tra reservation có hợp lệ không
     */
    public boolean isValidReservation(String reservationId) {
        return bookingDao.isValidReservation(reservationId);
    }

    /**
     * Lấy thời gian còn lại của reservation
     */
    public Integer getRemainingTime(String reservationId) {
        return bookingDao.getRemainingTime(reservationId);
    }

    /**
     * Đánh dấu ghế đã đặt (khi thanh toán thành công)
     */
    public boolean markSeatsAsBooked(String reservationId, int orderId) {
        return bookingDao.markSeatsAsBooked(reservationId, orderId);
    }

    // ==================== MAINTENANCE ====================

    /**
     * Dọn dẹp ghế giữ quá hạn
     */
    public int cleanupExpiredReservations() {
        return bookingDao.cleanupExpiredReservations();
    }

    /**
     * Dọn dẹp với thời gian cụ thể
     */
    public int cleanupExpiredReservations(LocalDateTime cutoffTime) {
        return bookingDao.cleanupExpiredReservations(cutoffTime);
    }

    // ==================== STATISTICS ====================

    /**
     * Đếm số ghế đang được giữ
     */
    public int countActiveReservations() {
        return bookingDao.countActiveReservations();
    }

    /**
     * Đếm số ghế user đang giữ
     */
    public int countUserReservations(int userId) {
        return bookingDao.countUserReservations(userId);
    }

    /**
     * Lấy thông tin chi tiết reservation
     */
    public BookedSeat getReservationDetails(String reservationId) {
        return bookingDao.getReservationDetails(reservationId);
    }

    // ==================== VALIDATION ====================

    /**
     * Kiểm tra xem các ghế có còn trống không
     */
    public boolean validateSeatsAvailable(int showtimeId, int roomId, List<String> seatCodes) {
        SeatService seatService = new SeatService();

        for (String seatCode : seatCodes) {
            var seat = seatService.getSeatByCode(roomId, seatCode);
            if (seat == null) {
                return false;
            }

            SeatStatus status = checkSeatStatus(showtimeId, seat.getId());
            if (status != SeatStatus.AVAILABLE) {
                return false;
            }
        }

        return true;
    }

    /**
     * Kiểm tra số ghế tối đa cho phép
     */
    public boolean validateMaxSeats(int showtimeId, int userId, int requestedSeats) {
        int currentReservations = countUserReservations(userId);
        int MAX_SEATS_PER_USER = 10; // Giới hạn 10 ghế/user

        return (currentReservations + requestedSeats) <= MAX_SEATS_PER_USER;
    }
}