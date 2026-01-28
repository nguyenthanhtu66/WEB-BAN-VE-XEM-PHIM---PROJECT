package vn.edu.hcmuaf.fit.demo1.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import vn.edu.hcmuaf.fit.demo1.model.BookedSeat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

            // Handle nullable orderId
            int orderId = rs.getInt("order_id");
            if (!rs.wasNull()) {
                bookedSeat.setOrderId(orderId);
            }

            // Handle nullable userId
            int userId = rs.getInt("user_id");
            if (!rs.wasNull()) {
                bookedSeat.setUserId(userId);
            }

            bookedSeat.setStatus(rs.getString("status"));

            // Handle timestamp
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

    // Lấy danh sách ghế đã đặt cho một showtime
    public List<BookedSeat> getBookedSeatsByShowtime(int showtimeId) {
        String sql = """
                SELECT * FROM booked_seats 
                WHERE showtime_id = :showtimeId 
                  AND (status = 'booked' 
                       OR (status = 'reserved' AND reserved_until > NOW()))
                """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .map(new BookedSeatMapper())
                        .list()
        );
    }

    // Lấy danh sách seat IDs đã đặt cho showtime
    public List<Integer> getBookedSeatIdsByShowtime(int showtimeId) {
        String sql = """
                SELECT seat_id FROM booked_seats 
                WHERE showtime_id = :showtimeId 
                  AND status = 'booked'
                """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .mapTo(Integer.class)
                        .list()
        );
    }

    public List<Integer> getAllReservedSeatIdsByShowtime(int showtimeId) {
        String sql = """
                SELECT seat_id FROM booked_seats 
                WHERE showtime_id = :showtimeId 
                  AND (status = 'reserved' AND reserved_until > NOW())
                """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .mapTo(Integer.class)
                        .list()
        );
    }

    public boolean reserveSeat(int showtimeId, int seatId, Integer userId, String sessionId) {
        // Xóa reservation cũ nếu có (đã hết hạn)
        releaseExpiredReservation(showtimeId, seatId);

        String sql = """
                INSERT INTO booked_seats (showtime_id, seat_id, user_id, session_id, status, reserved_until)
                VALUES (:showtimeId, :seatId, :userId, :sessionId, 'reserved', DATE_ADD(NOW(), INTERVAL 5 MINUTE))
                ON DUPLICATE KEY UPDATE
                    user_id = :userId,
                    session_id = :sessionId,
                    status = 'reserved',
                    reserved_until = DATE_ADD(NOW(), INTERVAL 5 MINUTE)
                """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .bind("userId", userId)
                            .bind("sessionId", sessionId)
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            System.err.println("Error reserving seat: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Đặt ghế chính thức (khi thanh toán)
    public boolean bookSeat(int showtimeId, int seatId, int orderId, Integer userId) {
        String sql = """
                INSERT INTO booked_seats (showtime_id, seat_id, order_id, user_id, status, reserved_until)
                VALUES (:showtimeId, :seatId, :orderId, :userId, 'booked', NULL)
                ON DUPLICATE KEY UPDATE
                    order_id = :orderId,
                    user_id = :userId,
                    status = 'booked',
                    reserved_until = NULL
                """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .bind("orderId", orderId)
                            .bind("userId", userId)
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            System.err.println("Error booking seat: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Hủy giữ ghế
    public boolean releaseSeat(int showtimeId, int seatId) {
        String sql = """
                UPDATE booked_seats 
                SET status = 'released'
                WHERE showtime_id = :showtimeId 
                  AND seat_id = :seatId
                  AND (status = 'reserved' OR status = 'reserved_in_cart')
                """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .execute()
            );
            System.out.println("✅ Released seat: showtimeId=" + showtimeId + ", seatId=" + seatId + ", rows affected=" + rows);
            return rows > 0;
        } catch (Exception e) {
            System.err.println("Error releasing seat: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Hủy tất cả ghế đã giữ của user
    public boolean releaseUserSeats(int showtimeId, Integer userId) {
        if (userId == null) return false;

        String sql = """
                UPDATE booked_seats 
                SET status = 'released'
                WHERE showtime_id = :showtimeId 
                  AND user_id = :userId
                  AND (status = 'reserved' OR status = 'reserved_in_cart')
                """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("userId", userId)
                            .execute()
            );
            return rows >= 0;
        } catch (Exception e) {
            System.err.println("Error releasing user seats: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isSeatAvailable(int showtimeId, int seatId) {
        return isSeatAvailable(showtimeId, seatId, "");
    }


    // Kiểm tra ghế có khả dụng không
    public boolean isSeatAvailable(int showtimeId, int seatId, String sessionId) {
        System.out.println("=== isSeatAvailable ===");
        System.out.println("showtimeId: " + showtimeId + ", seatId: " + seatId + ", sessionId: " + sessionId);

        // Trước tiên, xóa các reservation đã hết hạn
        releaseExpiredReservation(showtimeId, seatId);

        String sql = """
                SELECT COUNT(*) FROM booked_seats 
                WHERE showtime_Id = :showtimeId 
                  AND seat_id = :seatId
                  AND (status = 'booked' 
                       OR (status = 'reserved' AND (user_id IS NOT NULL OR session_id != :sessionId)))
                """;

        try {
            int count = get().withHandle(handle -> {
                System.out.println("Executing availability check");
                return handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .bind("seatId", seatId)
                        .bind("sessionId", sessionId != null ? sessionId : "")
                        .mapTo(Integer.class)
                        .one();
            });

            System.out.println("Seat count (occupied by others): " + count);
            System.out.println("=== END isSeatAvailable ===");

            return count == 0; // Available nếu không có ai KHÁC giữ/đặt
        } catch (Exception e) {
            System.err.println("Error in isSeatAvailable: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Xóa reservation đã hết hạn
    public void releaseExpiredReservation(int showtimeId, int seatId) {
        String sql = """
                UPDATE booked_seats 
                SET status = 'released'
                WHERE showtime_id = :showtimeId 
                  AND seat_id = :seatId
                  AND status IN ('reserved', 'reserved_in_cart')
                  AND reserved_until <= NOW()
                """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .execute()
            );
            if (rows > 0) {
                System.out.println("✅ Released expired reservation: showtimeId=" + showtimeId + ", seatId=" + seatId);
            }
        } catch (Exception e) {
            System.err.println("Error releasing expired reservation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Xóa tất cả reservation đã hết hạn (chạy định kỳ)
    public int releaseAllExpiredReservations() {
        String sql = """
                UPDATE booked_seats 
                SET status = 'released'
                WHERE status IN ('reserved', 'reserved_in_cart')
                  AND reserved_until <= NOW()
                """;

        try {
            return get().withHandle(handle ->
                    handle.createUpdate(sql).execute()
            );
        } catch (Exception e) {
            System.err.println("Error releasing all expired reservations: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    // Lấy số ghế còn trống cho showtime
    public int getAvailableSeatsCount(int showtimeId, int roomId) {
        String sql = """
                SELECT COUNT(*) 
                FROM seats s
                WHERE s.room_id = :roomId 
                  AND s.is_active = true
                  AND s.id NOT IN (
                    SELECT seat_id FROM booked_seats 
                    WHERE showtime_id = :showtimeId 
                      AND (status = 'booked' 
                           OR (status = 'reserved' AND reserved_until > NOW()))
                  )
                """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .bind("roomId", roomId)
                        .mapTo(Integer.class)
                        .one()
        );
    }

    // Kiểm tra seat có available để chọn không
    public boolean isSeatAvailableForUser(int showtimeId, int seatId, int userId) {
        String sql = """
        SELECT COUNT(*) FROM booked_seats 
        WHERE showtime_id = :showtimeId 
          AND seat_id = :seatId
          AND (status = 'booked' 
               OR (status = 'reserved' AND (user_id != :userId OR user_id IS NULL)))
        """;

        try {
            int count = get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .bind("userId", userId)
                            .mapTo(Integer.class)
                            .one()
            );
            return count == 0; // Available nếu không có ai KHÁC giữ/đặt
        } catch (Exception e) {
            System.err.println("Error in isSeatAvailableForUser: " + e.getMessage());
            return false;
        }
    }
    public boolean isSeatAvailableForSession(int showtimeId, int seatId, String sessionId) {
        System.out.println("=== isSeatAvailableForSession ===");
        System.out.println("showtimeId: " + showtimeId + ", seatId: " + seatId + ", sessionId: " + sessionId);

        String sql = """
                SELECT COUNT(*) FROM booked_seats 
                WHERE showtime_id = :showtimeId 
                  AND seat_id = :seatId
                  AND (status = 'booked' 
                       OR (status = 'reserved' AND session_id != :sessionId))
                """;

        try {
            int count = get().withHandle(handle -> {
                System.out.println("Executing session availability check");
                return handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .bind("seatId", seatId)
                        .bind("sessionId", sessionId != null ? sessionId : "")
                        .mapTo(Integer.class)
                        .one();
            });

            System.out.println("Seat count for other sessions: " + count);
            System.out.println("=== END isSeatAvailableForSession ===");

            return count == 0;

        } catch (Exception e) {
            System.err.println("Error in isSeatAvailableForSession: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Integer> getReservedSeatIdsByShowtime(int showtimeId, String sessionId) {
        String sql = """
                SELECT seat_id FROM booked_seats 
                WHERE showtime_id = :showtimeId 
                  AND (status = 'booked' 
                       OR (status = 'reserved' AND reserved_until > NOW()))
                  AND (session_id = :sessionId OR user_id IS NOT NULL)
                """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .bind("sessionId", sessionId != null ? sessionId : "")
                        .mapTo(Integer.class)
                        .list()
        );
    }// Thêm vào BookedSeatDao.java

    public boolean isSeatBooked(int showtimeId, int seatId) {
        String sql = """
                SELECT COUNT(*) FROM booked_seats 
                WHERE showtime_id = :showtimeId 
                  AND seat_id = :seatId
                  AND status = 'booked'
                """;

        try {
            int count = get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .mapTo(Integer.class)
                            .one()
            );
            return count > 0;
        } catch (Exception e) {
            System.err.println("Error checking if seat is booked: " + e.getMessage());
            return false;
        }
    }

    public boolean isSeatReservedByOtherSession(int showtimeId, int seatId, String sessionId) {
        String sql = """
                SELECT COUNT(*) FROM booked_seats 
                WHERE showtime_id = :showtimeId 
                  AND seat_id = :seatId
                  AND status = 'reserved'
                  AND reserved_until > NOW()
                  AND (session_id IS NULL OR session_id != :sessionId)
                """;

        try {
            int count = get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .bind("sessionId", sessionId != null ? sessionId : "")
                            .mapTo(Integer.class)
                            .one()
            );
            return count > 0;
        } catch (Exception e) {
            System.err.println("Error checking if seat reserved by other session: " + e.getMessage());
            return false;
        }
    }

    public boolean isSeatReservedBySession(int showtimeId, int seatId, String sessionId) {
        String sql = """
        SELECT COUNT(*) FROM booked_seats 
        WHERE showtime_id = :showtimeId 
          AND seat_id = :seatId 
          AND session_id = :sessionId
          AND status = 'reserved'
          AND reserved_until > NOW()
        """;

        try {
            int count = get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .bind("sessionId", sessionId)
                            .mapTo(Integer.class)
                            .one()
            );
            return count > 0;
        } catch (Exception e) {
            System.err.println("Error checking if seat reserved by session: " + e.getMessage());
            return false;
        }
    }

    public boolean releaseSessionSeats(int showtimeId, String sessionId) {
        String sql = """
        UPDATE booked_seats 
        SET status = 'released'
        WHERE showtime_id = :showtimeId 
          AND session_id = :sessionId
          AND status = 'reserved'
        """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("sessionId", sessionId)
                            .execute()
            );
            return rows >= 0;
        } catch (Exception e) {
            System.err.println("Error releasing session seats: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSeatStatusInCart(int showtimeId, int seatId, String sessionId) {
        String sql = """
                UPDATE booked_seats 
                SET status = 'reserved_in_cart'
                WHERE showtime_id = :showtimeId 
                  AND seat_id = :seatId
                  AND session_id = :sessionId
                  AND status = 'reserved'
                """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .bind("sessionId", sessionId)
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            System.err.println("Error updating seat status to in-cart: " + e.getMessage());
            return false;
        }
    }
    // Kiểm tra seat có đang reserved bởi user/session này không
    public boolean isSeatReservedByUser(int showtimeId, int seatId, int userId) {
        String sql = """
        SELECT COUNT(*) FROM booked_seats 
        WHERE showtime_id = :showtimeId 
          AND seat_id = :seatId
          AND user_id = :userId
          AND status = 'reserved'
          AND reserved_until > NOW()
        """;

        try {
            int count = get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .bind("userId", userId)
                            .mapTo(Integer.class)
                            .one()
            );
            return count > 0;
        } catch (Exception e) {
            System.err.println("Error checking if seat reserved by user: " + e.getMessage());
            return false;
        }
    }

    public boolean reserveSeatForCart(int showtimeId, int seatId, Integer userId, String sessionId) {
        System.out.println("🔒 Reserving seat for cart - showtimeId: " + showtimeId +
                ", seatId: " + seatId +
                ", userId: " + userId +
                ", sessionId: " + sessionId);

        try {
            // 1. Xóa reservation cũ nếu có (nếu đang reserve)
            String deleteSql = """
            DELETE FROM booked_seats 
            WHERE showtime_id = :showtimeId 
              AND seat_id = :seatId 
              AND status = 'reserved'
              AND (user_id = :userId OR session_id = :sessionId)
            """;

            int deleted = get().withHandle(handle ->
                    handle.createUpdate(deleteSql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .bind("userId", userId != null ? userId : 0)
                            .bind("sessionId", sessionId != null ? sessionId : "")
                            .execute()
            );

            System.out.println("Deleted old reservations: " + deleted);

            // 2. Thêm reservation mới với 5 phút cho CART
            String insertSql = """
            INSERT INTO booked_seats 
            (showtime_id, seat_id, user_id, session_id, status, reserved_until, created_at)
            VALUES (:showtimeId, :seatId, :userId, :sessionId, 'reserved_for_cart', 
                    DATE_ADD(NOW(), INTERVAL 5 MINUTE), NOW())
            """;

            int rows = get().withHandle(handle ->
                    handle.createUpdate(insertSql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .bind("userId", userId != null ? userId : null)
                            .bind("sessionId", sessionId != null ? sessionId : null)
                            .execute()
            );

            System.out.println("✅ Seat reserved for cart (5 minutes), rows affected: " + rows);
            return rows > 0;

        } catch (Exception e) {
            System.err.println("❌ Error reserving seat for cart: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public void cleanupExpiredCartReservations() {
        String sql = """
        DELETE FROM booked_seats 
        WHERE status = 'reserved_for_cart' 
          AND reserved_until <= NOW()
        """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql).execute()
            );

            if (rows > 0) {
                System.out.println("🧹 Cleaned up " + rows + " expired cart reservations");
            }
        } catch (Exception e) {
            System.err.println("Error cleaning up cart reservations: " + e.getMessage());
        }
    }
    // Release seat khỏi cart (khi xóa khỏi cart)
    public boolean releaseSeatFromCart(int showtimeId, int seatId) {
        String sql = """
        DELETE FROM booked_seats 
        WHERE showtime_id = :showtimeId 
          AND seat_id = :seatId 
          AND status = 'reserved_for_cart'
        """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .execute()
            );

            System.out.println("✅ Released seat from cart: showtimeId=" + showtimeId + ", seatId=" + seatId + ", rows=" + rows);
            return rows > 0;

        } catch (Exception e) {
            System.err.println("❌ Error releasing seat from cart: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    // Kiểm tra xem ghế có available để thêm vào cart không
    public boolean isSeatAvailableForCart(int showtimeId, int seatId, String sessionId, Integer userId) {
        String sql = """
        SELECT COUNT(*) 
        FROM booked_seats 
        WHERE showtime_id = :showtimeId 
          AND seat_id = :seatId 
          AND (
            status = 'booked' OR  -- Đã đặt (đỏ)
            (status = 'reserved' AND reserved_until > NOW()) OR  -- Đang giữ (vàng)
            (status = 'reserved_for_cart' AND reserved_until > NOW() AND 
             NOT (session_id = :sessionId OR user_id = :userId))  -- Cart của người khác
          )
        """;

        Integer count = get().withHandle(handle -> {
            var query = handle.createQuery(sql)
                    .bind("showtimeId", showtimeId)
                    .bind("seatId", seatId);

            if (sessionId != null && !sessionId.isEmpty()) {
                query = query.bind("sessionId", sessionId);
            } else {
                query = query.bind("sessionId", "");
            }

            if (userId != null) {
                query = query.bind("userId", userId);
            } else {
                query = query.bind("userId", 0);
            }

            return query.mapTo(Integer.class).one();
        });

        // Nếu count = 0 thì seat available
        return count != null && count == 0;
    }
    public boolean reserveSeatForSession(int showtimeId, int seatId, String sessionId) {
        String sql = """
        INSERT INTO booked_seats (showtime_id, seat_id, session_id, status, reserved_until)
        VALUES (:showtimeId, :seatId, :sessionId, 'reserved', DATE_ADD(NOW(), INTERVAL 5 MINUTE))
        ON DUPLICATE KEY UPDATE
            session_id = :sessionId,
            status = 'reserved',
            reserved_until = DATE_ADD(NOW(), INTERVAL 5 MINUTE)
        """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .bind("sessionId", sessionId)
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            System.err.println("Error reserving seat for session: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Reserve seat cho user (đã login)
    public boolean reserveSeatForUser(int showtimeId, int seatId, int userId) {
        String sql = """
        INSERT INTO booked_seats (showtime_id, seat_id, user_id, status, reserved_until)
        VALUES (:showtimeId, :seatId, :userId, 'reserved', DATE_ADD(NOW(), INTERVAL 5 MINUTE))
        ON DUPLICATE KEY UPDATE
            user_id = :userId,
            status = 'reserved',
            reserved_until = DATE_ADD(NOW(), INTERVAL 5 MINUTE)
        """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .bind("userId", userId)
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            System.err.println("Error reserving seat for user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}