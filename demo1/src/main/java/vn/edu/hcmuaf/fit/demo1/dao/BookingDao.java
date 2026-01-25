package vn.edu.hcmuaf.fit.demo1.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import vn.edu.hcmuaf.fit.demo1.model.BookedSeat;
import vn.edu.hcmuaf.fit.demo1.model.Seat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Giữ ghế tạm thời (5 phút)
    public boolean reserveSeat(int showtimeId, int seatId, int userId) {
        cleanupExpiredReservations();

        if (!isSeatAvailable(showtimeId, seatId)) {
            return false;
        }

        LocalDateTime reservedUntil = LocalDateTime.now().plusMinutes(5);

        String sql = """
            INSERT INTO booked_seats (showtime_id, seat_id, user_id, status, reserved_until)
            VALUES (:showtimeId, :seatId, :userId, 'reserved', :reservedUntil)
            ON DUPLICATE KEY UPDATE 
                user_id = VALUES(user_id),
                status = 'reserved',
                reserved_until = VALUES(reserved_until),
                created_at = CURRENT_TIMESTAMP
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

        // Kiểm tra tất cả ghế có sẵn không
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
                        ON DUPLICATE KEY UPDATE 
                            user_id = VALUES(user_id),
                            status = 'reserved',
                            reserved_until = VALUES(reserved_until),
                            created_at = CURRENT_TIMESTAMP
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

    // Xác nhận đặt ghế (chuyển từ reserved sang booked)
    public boolean confirmBooking(int showtimeId, List<Integer> seatIds, int orderId, int userId) {
        String sql = """
            UPDATE booked_seats 
            SET status = 'booked', 
                order_id = :orderId,
                reserved_until = NULL
            WHERE showtime_id = :showtimeId 
            AND seat_id IN (<seatIds>)
            AND user_id = :userId
            AND status = 'reserved'
            AND reserved_until > NOW()
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
            SET status = 'released',
                reserved_until = NULL
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
            SET status = 'released',
                reserved_until = NULL
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

    // Xóa các reservation hết hạn
    public boolean cleanupExpiredReservations() {
        String sql = """
            UPDATE booked_seats 
            SET status = 'released',
                reserved_until = NULL
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

    // Lấy danh sách ghế đã được giữ/đặt cho showtime
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

    // Kiểm tra ghế có bị giữ bởi user khác không
    public boolean isSeatReservedByOtherUser(int showtimeId, int seatId, int userId) {
        String sql = """
            SELECT COUNT(*) FROM booked_seats 
            WHERE showtime_id = :showtimeId 
            AND seat_id = :seatId 
            AND status = 'reserved'
            AND user_id != :userId
            AND reserved_until > NOW()
            """;

        int count = get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .bind("seatId", seatId)
                        .bind("userId", userId)
                        .mapTo(Integer.class)
                        .one()
        );

        return count > 0;
    }

    // Lấy thông tin reservation của user
    public List<BookedSeat> getUserReservations(int userId) {
        String sql = """
            SELECT bs.*, s.seat_code, s.row_number, s.seat_number,
                   st.show_date, st.show_time, m.title as movie_title,
                   r.room_name
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            JOIN showtimes st ON bs.showtime_id = st.id
            JOIN movies m ON st.movie_id = m.id
            JOIN rooms r ON st.room_id = r.id
            WHERE bs.user_id = :userId
            AND bs.status = 'reserved'
            AND bs.reserved_until > NOW()
            ORDER BY bs.reserved_until ASC
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("userId", userId)
                        .map(new BookedSeatMapper())
                        .list()
        );
    }

    // Lấy trạng thái ghế theo mã ghế
    public Map<String, String> getSeatStatusByCodes(int showtimeId, List<String> seatCodes) {
        String sql = """
            SELECT 
                s.seat_code,
                CASE 
                    WHEN bs.id IS NOT NULL AND bs.status = 'booked' THEN 'booked'
                    WHEN bs.id IS NOT NULL AND bs.status = 'reserved' 
                         AND bs.reserved_until > NOW() THEN 'reserved'
                    ELSE 'available'
                END as seat_status
            FROM seats s
            JOIN showtimes st ON st.room_id = s.room_id
            LEFT JOIN booked_seats bs ON bs.showtime_id = st.id AND bs.seat_id = s.id
            WHERE st.id = :showtimeId
            AND s.seat_code IN (<seatCodes>)
            """;

        try {
            List<Map<String, Object>> result = get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .bindList("seatCodes", seatCodes)
                            .mapToMap()
                            .list()
            );

            Map<String, String> seatStatusMap = new HashMap<>();
            for (Map<String, Object> row : result) {
                String seatCode = (String) row.get("seat_code");
                String status = (String) row.get("seat_status");
                seatStatusMap.put(seatCode, status);
            }

            return seatStatusMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    // Lấy thông tin ghế đã được giữ (cho timer)
    public List<Map<String, Object>> getExpiringReservations(int minutesBefore) {
        String sql = """
            SELECT 
                bs.*,
                s.seat_code,
                u.email,
                TIMESTAMPDIFF(SECOND, NOW(), bs.reserved_until) as seconds_remaining,
                m.title as movie_title,
                st.show_date,
                st.show_time
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            JOIN users u ON bs.user_id = u.id
            JOIN showtimes st ON bs.showtime_id = st.id
            JOIN movies m ON st.movie_id = m.id
            WHERE bs.status = 'reserved'
            AND bs.reserved_until > NOW()
            AND bs.reserved_until <= DATE_ADD(NOW(), INTERVAL :minutes MINUTE)
            ORDER BY bs.reserved_until ASC
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("minutes", minutesBefore)
                            .mapToMap()
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Tạo reservation ID mới
    public String createReservationId(int showtimeId, int userId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "RES-" + timestamp + "-" + userId + "-" + showtimeId;
    }

    // Lấy thông tin booking theo orderId
    public List<BookedSeat> getBookingByOrderId(int orderId) {
        String sql = """
            SELECT bs.*, s.seat_code, s.row_number, s.seat_number,
                   m.title as movie_title, r.room_name,
                   st.show_date, st.show_time
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            JOIN showtimes st ON bs.showtime_id = st.id
            JOIN movies m ON st.movie_id = m.id
            JOIN rooms r ON st.room_id = r.id
            WHERE bs.order_id = :orderId
            AND bs.status = 'booked'
            ORDER BY s.seat_code
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("orderId", orderId)
                        .map(new BookedSeatMapper())
                        .list()
        );
    }

    // Kiểm tra xem user có reservation nào sắp hết hạn không
    public boolean hasExpiringReservation(int userId) {
        String sql = """
            SELECT COUNT(*) FROM booked_seats 
            WHERE user_id = :userId
            AND status = 'reserved'
            AND reserved_until > NOW()
            AND reserved_until <= DATE_ADD(NOW(), INTERVAL 1 MINUTE)
            """;

        int count = get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("userId", userId)
                        .mapTo(Integer.class)
                        .one()
        );

        return count > 0;
    }

    // Lấy tất cả reservation đang active
    public List<BookedSeat> getAllActiveReservations() {
        String sql = """
            SELECT bs.*, s.seat_code, u.email,
                   m.title as movie_title, r.room_name
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            JOIN users u ON bs.user_id = u.id
            JOIN showtimes st ON bs.showtime_id = st.id
            JOIN movies m ON st.movie_id = m.id
            JOIN rooms r ON st.room_id = r.id
            WHERE bs.status = 'reserved'
            AND bs.reserved_until > NOW()
            ORDER BY bs.reserved_until ASC
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new BookedSeatMapper())
                        .list()
        );
    }
}