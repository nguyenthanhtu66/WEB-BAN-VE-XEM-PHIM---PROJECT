package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.BookedSeat;
import vn.edu.hcmuaf.fit.demo1.model.SeatStatus;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class BookedSeatDao extends BaseDao {

    private static class BookedSeatMapper implements RowMapper<BookedSeat> {
        @Override
        public BookedSeat map(ResultSet rs, StatementContext ctx) throws SQLException {
            BookedSeat bookedSeat = new BookedSeat();
            bookedSeat.setId(rs.getInt("id"));
            bookedSeat.setShowtimeId(rs.getInt("showtime_id"));
            bookedSeat.setSeatId(rs.getInt("seat_id"));
            bookedSeat.setOrderId(rs.getObject("order_id") != null ? rs.getInt("order_id") : null);
            bookedSeat.setUserId(rs.getObject("user_id") != null ? rs.getInt("user_id") : null);

            // SỬA LỖI: Chuyển đổi String sang SeatStatus enum
            String statusStr = rs.getString("status");
            try {
                bookedSeat.setStatus(SeatStatus.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                bookedSeat.setStatus(SeatStatus.AVAILABLE); // Mặc định nếu không hợp lệ
            }

            bookedSeat.setReservationId(rs.getString("reservation_id"));

            if (rs.getTimestamp("reserved_until") != null) {
                bookedSeat.setReservedUntil(rs.getTimestamp("reserved_until").toLocalDateTime());
            }

            if (rs.getTimestamp("created_at") != null) {
                bookedSeat.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }

            return bookedSeat;
        }
    }

    // Giữ ghế tạm thời
    public boolean reserveSeat(BookedSeat bookedSeat) {
        String sql = """
            INSERT INTO booked_seats (
                showtime_id, seat_id, order_id, user_id, 
                status, reservation_id, reserved_until
            ) VALUES (
                :showtimeId, :seatId, :orderId, :userId,
                :status, :reservationId, :reservedUntil
            )
            ON DUPLICATE KEY UPDATE
                status = VALUES(status),
                reservation_id = VALUES(reservation_id),
                reserved_until = VALUES(reserved_until),
                user_id = VALUES(user_id)
            """;

        return get().withHandle(handle -> {
            int rows = handle.createUpdate(sql)
                    .bind("showtimeId", bookedSeat.getShowtimeId())
                    .bind("seatId", bookedSeat.getSeatId())
                    .bind("orderId", bookedSeat.getOrderId())
                    .bind("userId", bookedSeat.getUserId())
                    .bind("status", bookedSeat.getStatus().name()) // SỬA: .name() để lấy String từ enum
                    .bind("reservationId", bookedSeat.getReservationId())
                    .bind("reservedUntil", bookedSeat.getReservedUntil())
                    .execute();
            return rows > 0;
        });
    }

    // Hủy giữ ghế theo reservationId
    public boolean releaseSeats(String reservationId) {
        String sql = """
            UPDATE booked_seats 
            SET status = 'RELEASED', reserved_until = NULL 
            WHERE reservation_id = :reservationId 
            AND status IN ('RESERVED', 'BOOKED')
            """;

        return get().withHandle(handle -> {
            int rows = handle.createUpdate(sql)
                    .bind("reservationId", reservationId)
                    .execute();
            return rows > 0;
        });
    }

    // Hủy giữ ghế theo showtime và seat
    public boolean releaseSeat(int showtimeId, int seatId) {
        String sql = """
            UPDATE booked_seats 
            SET status = 'RELEASED', reserved_until = NULL 
            WHERE showtime_id = :showtimeId 
            AND seat_id = :seatId 
            AND status IN ('RESERVED', 'BOOKED')
            """;

        return get().withHandle(handle -> {
            int rows = handle.createUpdate(sql)
                    .bind("showtimeId", showtimeId)
                    .bind("seatId", seatId)
                    .execute();
            return rows > 0;
        });
    }

    // Kiểm tra trạng thái ghế
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

    // Lấy danh sách ghế đã đặt/giữ cho suất chiếu
    public List<BookedSeat> getBookedSeatsForShowtime(int showtimeId) {
        String sql = """
            SELECT * FROM booked_seats 
            WHERE showtime_id = :showtimeId 
            AND status IN ('RESERVED', 'BOOKED')
            AND (reserved_until IS NULL OR reserved_until > NOW())
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .map(new BookedSeatMapper())
                        .list()
        );
    }

    // Dọn dẹp ghế giữ quá hạn
    public int cleanupExpiredReservations(LocalDateTime now) {
        String sql = """
            UPDATE booked_seats 
            SET status = 'RELEASED' 
            WHERE status = 'RESERVED' 
            AND reserved_until IS NOT NULL 
            AND reserved_until < :now
            """;

        return get().withHandle(handle ->
                handle.createUpdate(sql)
                        .bind("now", now)
                        .execute()
        );
    }

    // Kiểm tra xem ghế có đang được giữ bởi user không
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

    // Lấy reservationId theo showtime và seat
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

    // Đếm số ghế đang giữ theo reservationId
    public int countSeatsByReservation(String reservationId) {
        String sql = """
            SELECT COUNT(*) 
            FROM booked_seats 
            WHERE reservation_id = :reservationId 
            AND status = 'RESERVED'
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("reservationId", reservationId)
                        .mapTo(Integer.class)
                        .one()
        );
    }
}