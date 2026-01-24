package vn.edu.hcmuaf.fit.demo1.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import vn.edu.hcmuaf.fit.demo1.model.BookedSeat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class BookingDao extends BaseDao {

    private static class BookedSeatMapper implements RowMapper<BookedSeat> {
        @Override
        public BookedSeat map(ResultSet rs, StatementContext ctx) throws SQLException {
            BookedSeat bookedSeat = new BookedSeat();
            bookedSeat.setId(rs.getInt("id"));
            bookedSeat.setShowtimeId(rs.getInt("showtime_id"));
            bookedSeat.setSeatId(rs.getInt("seat_id"));
            bookedSeat.setOrderId(rs.getInt("order_id"));
            bookedSeat.setUserId(rs.getInt("user_id"));
            bookedSeat.setStatus(rs.getString("status"));

            Timestamp reservedUntil = rs.getTimestamp("reserved_until");
            if (reservedUntil != null) {
                bookedSeat.setReservedUntil(reservedUntil.toLocalDateTime());
            }

            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                bookedSeat.setCreatedAt(createdAt.toLocalDateTime());
            }

            try {
                bookedSeat.setSeatCode(rs.getString("seat_code"));
                bookedSeat.setRowNumber(rs.getString("row_number"));
                bookedSeat.setSeatNumber(rs.getInt("seat_number"));
            } catch (SQLException e) {
                // Không bắt buộc
            }

            return bookedSeat;
        }
    }

    // Giữ ghế tạm thời
    public boolean reserveSeat(int showtimeId, int seatId, int userId) {
        cleanupExpiredReservations();

        if (!isSeatAvailable(showtimeId, seatId)) {
            return false;
        }

        LocalDateTime reservedUntil = LocalDateTime.now().plusMinutes(5);

        String sql = """
            INSERT INTO booked_seats (showtime_id, seat_id, user_id, status, reserved_until)
            VALUES (:showtimeId, :seatId, :userId, 'reserved', :reservedUntil)
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .bind("userId", userId)
                            .bind("reservedUntil", reservedUntil)
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Giữ nhiều ghế cùng lúc
    public boolean reserveMultipleSeats(int showtimeId, List<Integer> seatIds, int userId) {
        cleanupExpiredReservations();

        for (int seatId : seatIds) {
            if (!isSeatAvailable(showtimeId, seatId)) {
                return false;
            }
        }

        LocalDateTime reservedUntil = LocalDateTime.now().plusMinutes(5);

        try {
            get().useTransaction(handle -> {
                for (int seatId : seatIds) {
                    String sql = """
                        INSERT INTO booked_seats (showtime_id, seat_id, user_id, status, reserved_until)
                        VALUES (:showtimeId, :seatId, :userId, 'reserved', :reservedUntil)
                        """;

                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .bind("userId", userId)
                            .bind("reservedUntil", reservedUntil)
                            .execute();
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra ghế có sẵn không
    private boolean isSeatAvailable(int showtimeId, int seatId) {
        String sql = """
            SELECT COUNT(*) FROM booked_seats 
            WHERE showtime_id = :showtimeId 
            AND seat_id = :seatId 
            AND status IN ('reserved', 'booked')
            AND (reserved_until IS NULL OR reserved_until > NOW())
            """;

        int count = get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .bind("seatId", seatId)
                        .mapTo(Integer.class)
                        .one()
        );

        return count == 0;
    }

    // Xác nhận đặt ghế
    public boolean confirmBooking(int showtimeId, List<Integer> seatIds, int orderId, int userId) {
        String sql = """
            UPDATE booked_seats 
            SET status = 'booked', order_id = :orderId
            WHERE showtime_id = :showtimeId 
            AND seat_id IN (<seatIds>)
            AND user_id = :userId
            AND status = 'reserved'
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .bindList("seatIds", seatIds)
                            .bind("orderId", orderId)
                            .bind("userId", userId)
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hủy giữ ghế
    public boolean releaseSeat(int showtimeId, int seatId, int userId) {
        String sql = """
            UPDATE booked_seats 
            SET status = 'released'
            WHERE showtime_id = :showtimeId 
            AND seat_id = :seatId
            AND user_id = :userId
            AND status = 'reserved'
            """;

        int rows = get().withHandle(handle ->
                handle.createUpdate(sql)
                        .bind("showtimeId", showtimeId)
                        .bind("seatId", seatId)
                        .bind("userId", userId)
                        .execute()
        );
        return rows > 0;
    }

    // Hủy giữ nhiều ghế
    public boolean releaseMultipleSeats(int showtimeId, List<Integer> seatIds, int userId) {
        if (seatIds == null || seatIds.isEmpty()) return true;

        String sql = """
            UPDATE booked_seats 
            SET status = 'released'
            WHERE showtime_id = :showtimeId 
            AND seat_id IN (<seatIds>)
            AND user_id = :userId
            AND status = 'reserved'
            """;

        int rows = get().withHandle(handle ->
                handle.createUpdate(sql)
                        .bind("showtimeId", showtimeId)
                        .bindList("seatIds", seatIds)
                        .bind("userId", userId)
                        .execute()
        );
        return rows > 0;
    }

    // PHƯƠNG THỨC MỚI: Hủy giữ ghế theo reservationId
    public boolean releaseSeats(String reservationId) {
        // Trong trường hợp bạn lưu reservationId riêng
        // Nếu không có, bạn có thể xóa tất cả reservation hết hạn
        return cleanupExpiredReservations();
    }

    // Xóa các reservation hết hạn
    public boolean cleanupExpiredReservations() {
        String sql = """
            UPDATE booked_seats 
            SET status = 'released'
            WHERE status = 'reserved'
            AND reserved_until IS NOT NULL
            AND reserved_until <= NOW()
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql).execute()
            );
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách ghế đã được giữ/đặt
    public List<BookedSeat> getBookedSeatsForShowtime(int showtimeId) {
        String sql = """
            SELECT bs.*, s.seat_code, s.row_number, s.seat_number
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            WHERE bs.showtime_id = :showtimeId
            AND bs.status IN ('reserved', 'booked')
            AND (bs.reserved_until IS NULL OR bs.reserved_until > NOW())
            ORDER BY s.row_number, s.seat_number
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .map(new BookedSeatMapper())
                        .list()
        );
    }
}