package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.BookedSeat;
import vn.edu.hcmuaf.fit.demo1.model.SeatStatus;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class BookingDao extends BaseDao {

    // ==================== ROW MAPPERS ====================

    private static class BookedSeatMapper implements RowMapper<BookedSeat> {
        @Override
        public BookedSeat map(ResultSet rs, StatementContext ctx) throws SQLException {
            BookedSeat bookedSeat = new BookedSeat();
            bookedSeat.setId(rs.getInt("id"));
            bookedSeat.setShowtimeId(rs.getInt("showtime_id"));
            bookedSeat.setSeatId(rs.getInt("seat_id"));

            // Xử lý các trường có thể null
            Integer orderId = rs.getObject("order_id") != null ? rs.getInt("order_id") : null;
            bookedSeat.setOrderId(orderId);

            Integer userId = rs.getObject("user_id") != null ? rs.getInt("user_id") : null;
            bookedSeat.setUserId(userId);

            // Chuyển đổi status string sang enum
            String statusStr = rs.getString("status");
            try {
                bookedSeat.setStatus(SeatStatus.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                bookedSeat.setStatus(SeatStatus.AVAILABLE); // Giá trị mặc định nếu lỗi
            }

            bookedSeat.setReservationId(rs.getString("reservation_id"));

            // Xử lý thời gian
            if (rs.getTimestamp("reserved_until") != null) {
                bookedSeat.setReservedUntil(rs.getTimestamp("reserved_until").toLocalDateTime());
            }

            if (rs.getTimestamp("created_at") != null) {
                bookedSeat.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }

            return bookedSeat;
        }
    }

    // ==================== SEAT RESERVATION METHODS ====================

    /**
     * Giữ ghế tạm thời
     */
    public boolean reserveSeat(BookedSeat bookedSeat) {
        String sql = """
            INSERT INTO booked_seats (
                showtime_id, seat_id, order_id, user_id, 
                status, reservation_id, reserved_until, created_at
            ) VALUES (
                :showtimeId, :seatId, :orderId, :userId,
                :status, :reservationId, :reservedUntil, NOW()
            )
            ON DUPLICATE KEY UPDATE
                status = VALUES(status),
                order_id = VALUES(order_id),
                user_id = VALUES(user_id),
                reservation_id = VALUES(reservation_id),
                reserved_until = VALUES(reserved_until),
                created_at = NOW()
            """;

        return get().withHandle(handle -> {
            int rows = handle.createUpdate(sql)
                    .bind("showtimeId", bookedSeat.getShowtimeId())
                    .bind("seatId", bookedSeat.getSeatId())
                    .bind("orderId", bookedSeat.getOrderId())
                    .bind("userId", bookedSeat.getUserId())
                    .bind("status", bookedSeat.getStatus().name()) // Lấy string từ enum
                    .bind("reservationId", bookedSeat.getReservationId())
                    .bind("reservedUntil", bookedSeat.getReservedUntil())
                    .execute();
            return rows > 0;
        });
    }

    /**
     * Giữ nhiều ghế cùng lúc
     */
    public boolean reserveMultipleSeats(List<BookedSeat> bookedSeats) {
        return get().withHandle(handle -> {
            int successCount = 0;

            for (BookedSeat bookedSeat : bookedSeats) {
                String sql = """
                    INSERT INTO booked_seats (
                        showtime_id, seat_id, order_id, user_id, 
                        status, reservation_id, reserved_until, created_at
                    ) VALUES (
                        :showtimeId, :seatId, :orderId, :userId,
                        :status, :reservationId, :reservedUntil, NOW()
                    )
                    ON DUPLICATE KEY UPDATE
                        status = VALUES(status),
                        order_id = VALUES(order_id),
                        user_id = VALUES(user_id),
                        reservation_id = VALUES(reservation_id),
                        reserved_until = VALUES(reserved_until),
                        created_at = NOW()
                    """;

                int rows = handle.createUpdate(sql)
                        .bind("showtimeId", bookedSeat.getShowtimeId())
                        .bind("seatId", bookedSeat.getSeatId())
                        .bind("orderId", bookedSeat.getOrderId())
                        .bind("userId", bookedSeat.getUserId())
                        .bind("status", bookedSeat.getStatus().name())
                        .bind("reservationId", bookedSeat.getReservationId())
                        .bind("reservedUntil", bookedSeat.getReservedUntil())
                        .execute();

                if (rows > 0) {
                    successCount++;
                }
            }

            return successCount == bookedSeats.size();
        });
    }

    /**
     * Hủy giữ ghế theo reservationId
     */
    public boolean releaseSeats(String reservationId) {
        String sql = """
            UPDATE booked_seats 
            SET status = 'RELEASED', 
                reserved_until = NULL,
                updated_at = NOW()
            WHERE reservation_id = :reservationId 
            AND status IN ('RESERVED')
            """;

        return get().withHandle(handle -> {
            int rows = handle.createUpdate(sql)
                    .bind("reservationId", reservationId)
                    .execute();
            return rows > 0;
        });
    }

    /**
     * Hủy giữ ghế theo showtime và seat
     */
    public boolean releaseSeat(int showtimeId, int seatId) {
        String sql = """
            UPDATE booked_seats 
            SET status = 'RELEASED', 
                reserved_until = NULL,
                updated_at = NOW()
            WHERE showtime_id = :showtimeId 
            AND seat_id = :seatId 
            AND status IN ('RESERVED')
            """;

        return get().withHandle(handle -> {
            int rows = handle.createUpdate(sql)
                    .bind("showtimeId", showtimeId)
                    .bind("seatId", seatId)
                    .execute();
            return rows > 0;
        });
    }

    /**
     * Hủy giữ tất cả ghế của một user
     */
    public boolean releaseUserSeats(int userId) {
        String sql = """
            UPDATE booked_seats 
            SET status = 'RELEASED', 
                reserved_until = NULL,
                updated_at = NOW()
            WHERE user_id = :userId 
            AND status IN ('RESERVED')
            AND (reserved_until IS NULL OR reserved_until > NOW())
            """;

        return get().withHandle(handle -> {
            int rows = handle.createUpdate(sql)
                    .bind("userId", userId)
                    .execute();
            return rows > 0;
        });
    }

    // ==================== SEAT STATUS CHECKING ====================

    /**
     * Kiểm tra trạng thái của một ghế cụ thể
     */
    public SeatStatus checkSeatStatus(int showtimeId, int seatId) {
        String sql = """
            SELECT status 
            FROM booked_seats 
            WHERE showtime_id = :showtimeId 
            AND seat_id = :seatId 
            AND status IN ('RESERVED', 'BOOKED')
            AND (reserved_until IS NULL OR reserved_until > NOW())
            LIMIT 1
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .bind("seatId", seatId)
                        .mapTo(String.class)
                        .findOne()
                        .map(statusStr -> {
                            try {
                                return SeatStatus.valueOf(statusStr.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                return SeatStatus.AVAILABLE;
                            }
                        })
                        .orElse(SeatStatus.AVAILABLE)
        );
    }

    /**
     * Lấy trạng thái của tất cả ghế trong một suất chiếu
     */
    public Map<String, String> getAllSeatStatus(int showtimeId, int roomId) {
        String sql = """
            SELECT 
                s.seat_code,
                COALESCE(
                    CASE 
                        WHEN bs.status = 'BOOKED' THEN 'BOOKED'
                        WHEN bs.status = 'RESERVED' AND bs.reserved_until > NOW() THEN 'RESERVED'
                        ELSE 'AVAILABLE'
                    END, 
                    'AVAILABLE'
                ) as status
            FROM seats s
            LEFT JOIN booked_seats bs ON s.id = bs.seat_id 
                AND bs.showtime_id = :showtimeId
                AND bs.status IN ('RESERVED', 'BOOKED')
                AND (bs.reserved_until IS NULL OR bs.reserved_until > NOW())
            WHERE s.room_id = :roomId
            AND s.is_active = TRUE
            ORDER BY s.row_number, s.seat_number
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .bind("roomId", roomId)
                        .reduceRows(
                                new HashMap<>(),
                                (map, rowView) -> {
                                    String seatCode = rowView.getColumn("seat_code", String.class);
                                    String status = rowView.getColumn("status", String.class);
                                    map.put(seatCode, status);
                                    return map;
                                }
                        )
        );
    }

    /**
     * Kiểm tra xem ghế có đang được giữ bởi user không
     */
    public boolean isSeatReservedByUser(int showtimeId, int seatId, int userId) {
        String sql = """
            SELECT COUNT(*) 
            FROM booked_seats 
            WHERE showtime_id = :showtimeId 
            AND seat_id = :seatId 
            AND user_id = :userId 
            AND status = 'RESERVED'
            AND (reserved_until IS NULL OR reserved_until > NOW())
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .bind("seatId", seatId)
                        .bind("userId", userId)
                        .mapTo(Integer.class)
                        .one() > 0
        );
    }

    // ==================== SEAT QUERY METHODS ====================

    /**
     * Lấy danh sách ghế đã đặt/giữ cho suất chiếu
     */
    public List<BookedSeat> getBookedSeatsForShowtime(int showtimeId) {
        String sql = """
            SELECT bs.* 
            FROM booked_seats bs
            WHERE bs.showtime_id = :showtimeId 
            AND bs.status IN ('RESERVED', 'BOOKED')
            AND (bs.reserved_until IS NULL OR bs.reserved_until > NOW())
            ORDER BY bs.created_at DESC
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .map(new BookedSeatMapper())
                        .list()
        );
    }

    /**
     * Lấy danh sách ghế đang giữ theo reservationId
     */
    public List<BookedSeat> getSeatsByReservation(String reservationId) {
        String sql = """
            SELECT bs.* 
            FROM booked_seats bs
            WHERE bs.reservation_id = :reservationId 
            AND bs.status IN ('RESERVED', 'BOOKED')
            AND (bs.reserved_until IS NULL OR bs.reserved_until > NOW())
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("reservationId", reservationId)
                        .map(new BookedSeatMapper())
                        .list()
        );
    }

    /**
     * Lấy reservationId theo showtime và seat
     */
    public String getReservationId(int showtimeId, int seatId) {
        String sql = """
            SELECT reservation_id 
            FROM booked_seats 
            WHERE showtime_id = :showtimeId 
            AND seat_id = :seatId 
            AND status = 'RESERVED'
            AND (reserved_until IS NULL OR reserved_until > NOW())
            LIMIT 1
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .bind("seatId", seatId)
                        .mapTo(String.class)
                        .findOne()
                        .orElse(null)
        );
    }

    /**
     * Đếm số ghế đang giữ theo reservationId
     */
    public int countSeatsByReservation(String reservationId) {
        String sql = """
            SELECT COUNT(*) 
            FROM booked_seats 
            WHERE reservation_id = :reservationId 
            AND status = 'RESERVED'
            AND (reserved_until IS NULL OR reserved_until > NOW())
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("reservationId", reservationId)
                        .mapTo(Integer.class)
                        .one()
        );
    }

    // ==================== MAINTENANCE METHODS ====================

    /**
     * Dọn dẹp ghế giữ quá hạn
     */
    public int cleanupExpiredReservations() {
        String sql = """
            UPDATE booked_seats 
            SET status = 'RELEASED',
                updated_at = NOW()
            WHERE status = 'RESERVED' 
            AND reserved_until IS NOT NULL 
            AND reserved_until < NOW()
            """;

        return get().withHandle(handle ->
                handle.createUpdate(sql)
                        .execute()
        );
    }

    /**
     * Dọn dẹp ghế giữ quá hạn với thời gian cụ thể
     */
    public int cleanupExpiredReservations(LocalDateTime cutoffTime) {
        String sql = """
            UPDATE booked_seats 
            SET status = 'RELEASED',
                updated_at = NOW()
            WHERE status = 'RESERVED' 
            AND reserved_until IS NOT NULL 
            AND reserved_until < :cutoffTime
            """;

        return get().withHandle(handle ->
                handle.createUpdate(sql)
                        .bind("cutoffTime", cutoffTime)
                        .execute()
        );
    }

    /**
     * Đánh dấu ghế đã đặt (chuyển từ RESERVED sang BOOKED)
     */
    public boolean markSeatsAsBooked(String reservationId, int orderId) {
        String sql = """
            UPDATE booked_seats 
            SET status = 'BOOKED',
                order_id = :orderId,
                reserved_until = NULL,
                updated_at = NOW()
            WHERE reservation_id = :reservationId 
            AND status = 'RESERVED'
            """;

        return get().withHandle(handle -> {
            int rows = handle.createUpdate(sql)
                    .bind("reservationId", reservationId)
                    .bind("orderId", orderId)
                    .execute();
            return rows > 0;
        });
    }

    /**
     * Kiểm tra xem reservationId có hợp lệ không
     */
    public boolean isValidReservation(String reservationId) {
        String sql = """
            SELECT COUNT(*) 
            FROM booked_seats 
            WHERE reservation_id = :reservationId 
            AND status = 'RESERVED'
            AND (reserved_until IS NULL OR reserved_until > NOW())
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("reservationId", reservationId)
                        .mapTo(Integer.class)
                        .one() > 0
        );
    }

    /**
     * Lấy thời gian còn lại của reservation
     */
    public Integer getRemainingTime(String reservationId) {
        String sql = """
            SELECT TIMESTAMPDIFF(SECOND, NOW(), reserved_until) as remaining_seconds
            FROM booked_seats 
            WHERE reservation_id = :reservationId 
            AND status = 'RESERVED'
            AND reserved_until > NOW()
            LIMIT 1
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("reservationId", reservationId)
                        .mapTo(Integer.class)
                        .findOne()
                        .orElse(0)
        );
    }

    // ==================== STATISTICS METHODS ====================

    /**
     * Đếm tổng số ghế đang được giữ
     */
    public int countActiveReservations() {
        String sql = """
            SELECT COUNT(*) 
            FROM booked_seats 
            WHERE status = 'RESERVED'
            AND (reserved_until IS NULL OR reserved_until > NOW())
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .mapTo(Integer.class)
                        .one()
        );
    }

    /**
     * Đếm số ghế đang được giữ theo user
     */
    public int countUserReservations(int userId) {
        String sql = """
            SELECT COUNT(*) 
            FROM booked_seats 
            WHERE user_id = :userId
            AND status = 'RESERVED'
            AND (reserved_until IS NULL OR reserved_until > NOW())
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("userId", userId)
                        .mapTo(Integer.class)
                        .one()
        );
    }

    /**
     * Lấy thông tin chi tiết về một reservation
     */
    public BookedSeat getReservationDetails(String reservationId) {
        String sql = """
            SELECT bs.* 
            FROM booked_seats bs
            WHERE bs.reservation_id = :reservationId 
            LIMIT 1
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("reservationId", reservationId)
                        .map(new BookedSeatMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // ==================== BATCH OPERATIONS ====================

    /**
     * Xóa tất cả reservations cũ (dọn dẹp database)
     */
    public int cleanupOldRecords(int daysToKeep) {
        String sql = """
            DELETE FROM booked_seats 
            WHERE created_at < DATE_SUB(NOW(), INTERVAL :days DAY)
            AND status = 'RELEASED'
            """;

        return get().withHandle(handle ->
                handle.createUpdate(sql)
                        .bind("days", daysToKeep)
                        .execute()
        );
    }

    /**
     * Backup reservations vào bảng lịch sử
     */
    public int backupReservations() {
        String sql = """
            INSERT INTO booked_seats_history 
            SELECT *, NOW() as backed_up_at 
            FROM booked_seats 
            WHERE status IN ('BOOKED', 'RELEASED')
            AND created_at < DATE_SUB(NOW(), INTERVAL 30 DAY)
            """;

        return get().withHandle(handle ->
                handle.createUpdate(sql)
                        .execute()
        );
    }
}