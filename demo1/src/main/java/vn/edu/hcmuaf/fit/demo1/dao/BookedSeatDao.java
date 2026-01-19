package vn.edu.hcmuaf.fit.demo1.dao;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import vn.edu.hcmuaf.fit.demo1.model.BookedSeat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class BookedSeatDao extends BaseDao {

    private static class BookedSeatMapper implements RowMapper<BookedSeat> {
        @Override
        public BookedSeat map(ResultSet rs, StatementContext ctx) throws SQLException {
            BookedSeat bookedSeat = new BookedSeat();
            bookedSeat.setId(rs.getInt("id"));
            bookedSeat.setShowtimeId(rs.getInt("showtime_id"));
            bookedSeat.setSeatId(rs.getInt("seat_id"));

            Integer orderId = rs.getObject("order_id", Integer.class);
            bookedSeat.setOrderId(orderId);

            Integer userId = rs.getObject("user_id", Integer.class);
            bookedSeat.setUserId(userId);

            bookedSeat.setStatus(rs.getString("status"));

            Timestamp reservedUntil = rs.getTimestamp("reserved_until");
            if (reservedUntil != null) {
                bookedSeat.setReservedUntil(reservedUntil.toLocalDateTime());
            }

            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                bookedSeat.setCreatedAt(createdAt.toLocalDateTime());
            }

            return bookedSeat;
        }
    }

    // Giữ ghế tạm thời (tính bằng phút)
    public boolean reserveSeatsTemporarily(int showtimeId, int roomId,
                                           String[] seatCodes, int userId,
                                           int minutes) {
        String sql = """
            INSERT INTO booked_seats (showtime_id, seat_id, user_id, status, reserved_until) 
            SELECT :showtimeId, s.id, :userId, 'reserved', 
                   DATE_ADD(NOW(), INTERVAL :minutes MINUTE)
            FROM seats s
            WHERE s.room_id = :roomId 
              AND s.seat_code IN (<seatCodes>)
              AND s.is_active = true
              AND s.id NOT IN (
                  SELECT bs.seat_id 
                  FROM booked_seats bs
                  WHERE bs.showtime_id = :showtimeId 
                    AND bs.status IN ('reserved', 'booked')
                    AND (bs.reserved_until IS NULL OR bs.reserved_until > NOW())
              )
            """;

        try {
            return get().withHandle(handle -> {
                // Tạo query với danh sách seat codes
                var query = handle.createUpdate(sql)
                        .bind("showtimeId", showtimeId)
                        .bind("roomId", roomId)
                        .bind("userId", userId)
                        .bind("minutes", minutes)
                        .bindList("seatCodes", Arrays.asList(seatCodes));

                int rows = query.execute();
                return rows > 0;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra ghế có được giữ không
    public boolean isSeatReserved(int showtimeId, String seatCode) {
        String sql = """
            SELECT COUNT(*) > 0
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            WHERE bs.showtime_id = :showtimeId
              AND s.seat_code = :seatCode
              AND bs.status = 'reserved'
              AND bs.reserved_until > NOW()
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatCode", seatCode)
                            .mapTo(Boolean.class)
                            .one()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra ghế có được giữ bởi user cụ thể không
    public boolean isSeatReservedByUser(int showtimeId, String seatCode, int userId) {
        String sql = """
            SELECT COUNT(*) > 0
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            WHERE bs.showtime_id = :showtimeId
              AND s.seat_code = :seatCode
              AND bs.user_id = :userId
              AND bs.status = 'reserved'
              AND bs.reserved_until > NOW()
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatCode", seatCode)
                            .bind("userId", userId)
                            .mapTo(Boolean.class)
                            .one()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hủy giữ ghế
    public boolean releaseSeats(int showtimeId, String[] seatCodes) {
        String sql = """
            DELETE bs FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            WHERE bs.showtime_id = :showtimeId
              AND s.seat_code IN (<seatCodes>)
              AND bs.status = 'reserved'
            """;

        try {
            return get().withHandle(handle -> {
                var query = handle.createUpdate(sql)
                        .bind("showtimeId", showtimeId)
                        .bindList("seatCodes", Arrays.asList(seatCodes));

                int rows = query.execute();
                return rows > 0;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật trạng thái từ "reserved" sang "booked" khi thanh toán thành công
    public boolean updateToBooked(int showtimeId, String[] seatCodes, int orderId, int userId) {
        String sql = """
            UPDATE booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            SET bs.status = 'booked',
                bs.order_id = :orderId,
                bs.reserved_until = NULL
            WHERE bs.showtime_id = :showtimeId
              AND s.seat_code IN (<seatCodes>)
              AND bs.user_id = :userId
              AND bs.status = 'reserved'
            """;

        try {
            return get().withHandle(handle -> {
                var query = handle.createUpdate(sql)
                        .bind("showtimeId", showtimeId)
                        .bind("orderId", orderId)
                        .bind("userId", userId)
                        .bindList("seatCodes", Arrays.asList(seatCodes));

                int rows = query.execute();
                return rows > 0;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Gia hạn thời gian giữ ghế
    public boolean extendReservation(int showtimeId, String[] seatCodes, int additionalMinutes) {
        String sql = """
            UPDATE booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            SET bs.reserved_until = DATE_ADD(bs.reserved_until, INTERVAL :minutes MINUTE)
            WHERE bs.showtime_id = :showtimeId
              AND s.seat_code IN (<seatCodes>)
              AND bs.status = 'reserved'
              AND bs.reserved_until > NOW()
            """;

        try {
            return get().withHandle(handle -> {
                var query = handle.createUpdate(sql)
                        .bind("showtimeId", showtimeId)
                        .bind("minutes", additionalMinutes)
                        .bindList("seatCodes", Arrays.asList(seatCodes));

                int rows = query.execute();
                return rows > 0;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy trạng thái ghế
    public String getSeatStatus(int showtimeId, String seatCode) {
        String sql = """
            SELECT 
                CASE 
                    WHEN bs.status = 'booked' THEN 'booked'
                    WHEN bs.status = 'reserved' AND bs.reserved_until > NOW() THEN 'reserved'
                    ELSE 'available'
                END as status
            FROM seats s
            LEFT JOIN booked_seats bs ON s.id = bs.seat_id 
                AND bs.showtime_id = :showtimeId
                AND (bs.status = 'booked' OR (bs.status = 'reserved' AND bs.reserved_until > NOW()))
            WHERE s.seat_code = :seatCode
              AND s.is_active = true
            ORDER BY bs.reserved_until DESC
            LIMIT 1
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatCode", seatCode)
                            .mapTo(String.class)
                            .findOne()
                            .orElse("available")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return "available";
        }
    }

    // Dọn dẹp ghế hết hạn giữ
    public int cleanupExpiredReservations() {
        String sql = """
            DELETE FROM booked_seats 
            WHERE status = 'reserved' 
              AND reserved_until <= NOW()
            """;

        try {
            return get().withHandle(handle ->
                    handle.createUpdate(sql).execute()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Lấy thông tin ghế đã đặt/giữ
    public List<Map<String, Object>> getBookedSeatsInfo(int showtimeId) {
        String sql = """
            SELECT 
                s.seat_code,
                bs.status,
                bs.user_id,
                bs.reserved_until,
                bs.created_at,
                u.full_name as reserved_by
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            LEFT JOIN users u ON bs.user_id = u.id
            WHERE bs.showtime_id = :showtimeId
              AND (bs.status = 'booked' OR (bs.status = 'reserved' AND bs.reserved_until > NOW()))
            ORDER BY s.seat_code
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .map((rs, ctx) -> {
                                Map<String, Object> map = new HashMap<>();
                                map.put("seatCode", rs.getString("seat_code"));
                                map.put("status", rs.getString("status"));
                                map.put("userId", rs.getInt("user_id"));
                                map.put("reservedUntil", rs.getTimestamp("reserved_until"));
                                map.put("createdAt", rs.getTimestamp("created_at"));
                                map.put("reservedBy", rs.getString("reserved_by"));
                                return map;
                            })
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Kiểm tra xem tất cả ghế có được giữ bởi cùng một user không
    public boolean areAllSeatsReservedBySameUser(int showtimeId, String[] seatCodes, int userId) {
        if (seatCodes == null || seatCodes.length == 0) {
            return false;
        }

        String sql = """
            SELECT COUNT(DISTINCT bs.user_id) = 1 AND MIN(bs.user_id) = :userId
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            WHERE bs.showtime_id = :showtimeId
              AND s.seat_code IN (<seatCodes>)
              AND bs.status = 'reserved'
              AND bs.reserved_until > NOW()
            """;

        try {
            return get().withHandle(handle -> {
                var query = handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .bind("userId", userId)
                        .bindList("seatCodes", Arrays.asList(seatCodes));

                return query.mapTo(Boolean.class).one();
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy tất cả ghế đã đặt/giữ cho suất chiếu
    public List<String> getAllBookedSeatsForShowtime(int showtimeId) {
        String sql = """
            SELECT DISTINCT s.seat_code
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            WHERE bs.showtime_id = :showtimeId
              AND (bs.status = 'booked' OR (bs.status = 'reserved' AND bs.reserved_until > NOW()))
            ORDER BY s.seat_code
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .mapTo(String.class)
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Đếm số ghế đã đặt/giữ
    public int countBookedSeatsForShowtime(int showtimeId) {
        String sql = """
            SELECT COUNT(*)
            FROM booked_seats bs
            WHERE bs.showtime_id = :showtimeId
              AND (bs.status = 'booked' OR (bs.status = 'reserved' AND bs.reserved_until > NOW()))
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .mapTo(Integer.class)
                            .one()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Kiểm tra xem ghế có đang được giữ bởi bất kỳ ai không
    public boolean isSeatCurrentlyReserved(int showtimeId, String seatCode) {
        String sql = """
            SELECT COUNT(*) > 0
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            WHERE bs.showtime_id = :showtimeId
              AND s.seat_code = :seatCode
              AND bs.status = 'reserved'
              AND bs.reserved_until > NOW()
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatCode", seatCode)
                            .mapTo(Boolean.class)
                            .one()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa tất cả ghế giữ của user
    public boolean releaseAllUserReservations(int userId) {
        String sql = """
            DELETE FROM booked_seats 
            WHERE user_id = :userId 
              AND status = 'reserved'
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("userId", userId)
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy thời gian còn lại giữ ghế
    public int getRemainingReservationTime(int showtimeId, String seatCode) {
        String sql = """
            SELECT TIMESTAMPDIFF(SECOND, NOW(), bs.reserved_until) as seconds_left
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            WHERE bs.showtime_id = :showtimeId
              AND s.seat_code = :seatCode
              AND bs.status = 'reserved'
              AND bs.reserved_until > NOW()
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatCode", seatCode)
                            .mapTo(Integer.class)
                            .findOne()
                            .orElse(0)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}