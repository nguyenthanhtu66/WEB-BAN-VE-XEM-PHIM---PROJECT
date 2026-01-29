package vn.edu.hcmuaf.fit.demo1.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import vn.edu.hcmuaf.fit.demo1.model.BookedSeat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        SET status = 'released', 
            order_id = NULL,
            user_id = NULL,
            reserved_until = NULL
        WHERE showtime_id = :showtimeId 
          AND seat_id = :seatId
          AND status IN ('booked', 'reserved', 'reserved_in_cart', 'reserved_for_cart')
        """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .execute()
            );
            System.out.println("✅ Released seat: showtimeId=" + showtimeId +
                    ", seatId=" + seatId + ", rows affected=" + rows);
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

    public boolean releaseSessionSeats(String sessionId) {
        String sql = """
        UPDATE booked_seats 
        SET status = 'released'
        WHERE session_id = :sessionId
          AND status IN ('reserved', 'reserved_for_cart')
        """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("sessionId", sessionId)
                            .execute()
            );
            System.out.println("✅ Released " + rows + " seats for session: " + sessionId);
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
        UPDATE booked_seats 
        SET status = 'released'
        WHERE status IN ('reserved', 'reserved_for_cart')
          AND reserved_until <= NOW()
        """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql).execute()
            );

            if (rows > 0) {
                System.out.println("🧹 Cleaned up " + rows + " expired reservations");
            }
        } catch (Exception e) {
            System.err.println("Error cleaning up expired reservations: " + e.getMessage());
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
    public boolean isSeatAvailableForCart(int showtimeId, int seatId, String sessionId, int userId) {
        try {
            String query = "SELECT bs.status, bs.user_id, bs.order_id, bs.reserved_until " +
                    "FROM booked_seats bs " +
                    "WHERE bs.showtime_id = ? AND bs.seat_id = ?";

            return get().withHandle(handle -> {
                return handle.createQuery(query)
                        .bind(0, showtimeId)
                        .bind(1, seatId)
                        .mapToMap()
                        .findFirst()
                        .map(row -> {
                            String status = (String) row.get("status");
                            Integer reservedUserId = (Integer) row.get("user_id");
                            Integer orderId = (Integer) row.get("order_id");
                            Timestamp reservedUntil = (Timestamp) row.get("reserved_until");

                            System.out.println("🔍 Checking seat availability:");
                            System.out.println("  - Seat ID: " + seatId);
                            System.out.println("  - Status: " + status);
                            System.out.println("  - Reserved User ID: " + reservedUserId);
                            System.out.println("  - Current User ID: " + userId);
                            System.out.println("  - Order ID: " + orderId);
                            System.out.println("  - Reserved Until: " + reservedUntil);

                            // TRƯỜNG HỢP 1: Không có record -> ghế trống
                            if (status == null) {
                                System.out.println("✅ Seat " + seatId + ": No record - AVAILABLE");
                                return true;
                            }

                            // TRƯỜNG HỢP 2: Ghế đã booked (đã thanh toán) -> không available
                            if ("booked".equals(status) && orderId != null) {
                                System.out.println("❌ Seat " + seatId + ": Already BOOKED with order " + orderId);
                                return false;
                            }

                            // TRƯỜNG HỢP 3: Ghế đang reserved bởi CHÍNH user này -> vẫn available để thanh toán
                            if ("reserved".equals(status) && reservedUserId != null && reservedUserId == userId) {
                                System.out.println("✅ Seat " + seatId + ": Reserved by current user - STILL AVAILABLE for payment");

                                // Kiểm tra xem reservation có hết hạn không
                                if (reservedUntil != null && reservedUntil.before(new java.util.Date())) {
                                    System.out.println("⚠️ Reservation expired - releasing");
                                    releaseSeat(showtimeId, seatId);
                                    return true;
                                }
                                return true;
                            }

                            // TRƯỜNG HỢP 4: Ghế đang reserved bởi user khác -> không available
                            if ("reserved".equals(status) && reservedUserId != null && reservedUserId != userId) {
                                System.out.println("❌ Seat " + seatId + ": Reserved by another user " + reservedUserId);

                                // Kiểm tra xem reservation có hết hạn không
                                if (reservedUntil != null && reservedUntil.before(new java.util.Date())) {
                                    System.out.println("ℹ️ Reservation expired - releasing");
                                    releaseSeat(showtimeId, seatId);
                                    return true;
                                }
                                return false;
                            }

                            // TRƯỜNG HỢP 5: Ghế đã released -> available
                            if ("released".equals(status)) {
                                System.out.println("✅ Seat " + seatId + ": RELEASED - AVAILABLE");
                                return true;
                            }

                            // TRƯỜNG HỢP 6: Ghế booked nhưng không có order_id (lỗi data)
                            if ("booked".equals(status) && orderId == null) {
                                System.out.println("⚠️ Seat " + seatId + ": BOOKED but no order_id - treating as available");
                                return true;
                            }

                            // TRƯỜNG HỢP mặc định: không available
                            System.out.println("❓ Seat " + seatId + ": Unknown status - " + status);
                            return false;
                        })
                        .orElse(true); // Mặc định là available nếu không có record
            });
        } catch (Exception e) {
            System.err.println("❌ Error in isSeatAvailableForCart: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
    public boolean bookSeatWithSession(int showtimeId, int seatId, int orderId, int userId, String sessionId) {
        try {
            // Kiểm tra trạng thái hiện tại
            String checkQuery = "SELECT status, user_id FROM booked_seats " +
                    "WHERE showtime_id = ? AND seat_id = ?";

            return get().withHandle(handle -> {
                // Kiểm tra trạng thái hiện tại
                Map<String, Object> currentStatus = handle.createQuery(checkQuery)
                        .bind(0, showtimeId)
                        .bind(1, seatId)
                        .mapToMap()
                        .findFirst()
                        .orElse(null);

                System.out.println("🔍 Current seat status before booking:");
                System.out.println("  - Showtime ID: " + showtimeId);
                System.out.println("  - Seat ID: " + seatId);
                System.out.println("  - Status: " + (currentStatus != null ? currentStatus.get("status") : "null"));
                System.out.println("  - User ID: " + (currentStatus != null ? currentStatus.get("user_id") : "null"));
                System.out.println("  - Current User ID: " + userId);
                System.out.println("  - Order ID to book: " + orderId);

                // Nếu ghế đã được booked (đã thanh toán) -> thất bại
                if (currentStatus != null && "booked".equals(currentStatus.get("status"))) {
                    System.out.println("❌ Seat already booked");
                    return false;
                }

                // Nếu ghế đang reserved bởi user KHÁC -> thất bại
                if (currentStatus != null && "reserved".equals(currentStatus.get("status"))) {
                    Integer reservedUserId = (Integer) currentStatus.get("user_id");
                    if (reservedUserId != null && reservedUserId != userId) {
                        System.out.println("❌ Seat reserved by another user: " + reservedUserId);
                        return false;
                    }
                    // Nếu là chính user này đang giữ -> tiếp tục book
                }

                // INSERT hoặc UPDATE record
                String upsertQuery = "INSERT INTO booked_seats " +
                        "(showtime_id, seat_id, order_id, user_id, status, reserved_until, created_at) " +
                        "VALUES (?, ?, ?, ?, 'booked', NULL, NOW()) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "order_id = VALUES(order_id), " +
                        "status = VALUES(status), " +
                        "reserved_until = NULL";

                int rows = handle.createUpdate(upsertQuery)
                        .bind(0, showtimeId)
                        .bind(1, seatId)
                        .bind(2, orderId)
                        .bind(3, userId)
                        .execute();

                System.out.println("✅ Seat booked successfully. Rows affected: " + rows);
                return rows > 0;
            });
        } catch (Exception e) {
            System.err.println("❌ Error in bookSeatWithSession: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean checkAndReserveSeatForPayment(int showtimeId, int seatId, int userId) {
        try {
            String query = "SELECT status, user_id FROM booked_seats " +
                    "WHERE showtime_id = ? AND seat_id = ?";

            return get().withHandle(handle -> {
                Map<String, Object> seatStatus = handle.createQuery(query)
                        .bind(0, showtimeId)
                        .bind(1, seatId)
                        .mapToMap()
                        .findFirst()
                        .orElse(null);

                // Trường hợp không có record
                if (seatStatus == null) {
                    System.out.println("✅ Seat is completely free");
                    return true;
                }

                String status = (String) seatStatus.get("status");
                Integer reservedUserId = (Integer) seatStatus.get("user_id");

                System.out.println("🔍 CheckAndReserve:");
                System.out.println("  - Status: " + status);
                System.out.println("  - Reserved User ID: " + reservedUserId);
                System.out.println("  - Current User ID: " + userId);

                // Cho phép nếu:
                // 1. Không có record
                // 2. Status = 'released'
                // 3. Status = 'reserved' VÀ user_id = current_user_id
                if (status == null || "released".equals(status)) {
                    return true;
                }

                if ("reserved".equals(status) && reservedUserId != null && reservedUserId == userId) {
                    // Gia hạn reservation
                    String extendQuery = "UPDATE booked_seats SET reserved_until = DATE_ADD(NOW(), INTERVAL 5 MINUTE) " +
                            "WHERE showtime_id = ? AND seat_id = ?";
                    handle.createUpdate(extendQuery)
                            .bind(0, showtimeId)
                            .bind(1, seatId)
                            .execute();
                    return true;
                }

                return false;
            });
        } catch (Exception e) {
            System.err.println("❌ Error in checkAndReserveSeatForPayment: " + e.getMessage());
            return false;
        }
    }
    // Thêm phương thức này vào BookedSeatDao
    public boolean bookSeatForPayment(int showtimeId, int seatId, int orderId, int userId) {
        try {
            String query = "INSERT INTO booked_seats " +
                    "(showtime_id, seat_id, order_id, user_id, status, created_at) " +
                    "VALUES (?, ?, ?, ?, 'booked', NOW()) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "order_id = VALUES(order_id), " +
                    "user_id = VALUES(user_id), " +
                    "status = VALUES(status)";

            int rows = get().withHandle(handle ->
                    handle.createUpdate(query)
                            .bind(0, showtimeId)
                            .bind(1, seatId)
                            .bind(2, orderId)
                            .bind(3, userId)
                            .execute()
            );

            System.out.println("✅ Seat " + seatId + " booked for order " + orderId + ", rows: " + rows);
            return rows > 0;

        } catch (Exception e) {
            System.err.println("❌ Error booking seat for payment: " + e.getMessage());
            return false;
        }
    }

    public boolean updateReservationToUser(int showtimeId, int seatId, String sessionId, int userId) {
        String sql = """
        UPDATE booked_seats 
        SET user_id = :userId, 
            session_id = NULL,
            reserved_until = DATE_ADD(NOW(), INTERVAL 5 MINUTE)
        WHERE showtime_id = :showtimeId 
          AND seat_id = :seatId
          AND session_id = :sessionId
          AND status IN ('reserved', 'reserved_for_cart')
        """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .bind("sessionId", sessionId)
                            .bind("userId", userId)
                            .execute()
            );

            System.out.println("✅ Updated reservation to user ID " + userId +
                    ", rows affected: " + rows);
            return rows > 0;

        } catch (Exception e) {
            System.err.println("Error updating reservation to user: " + e.getMessage());
            return false;
        }
    }
    public boolean transferSessionToUser(String sessionId, int userId) {
        String sql = """
        UPDATE booked_seats 
        SET user_id = :userId, 
            session_id = NULL,
            reserved_until = DATE_ADD(NOW(), INTERVAL 5 MINUTE)
        WHERE session_id = :sessionId
          AND status IN ('reserved', 'reserved_for_cart')
          AND reserved_until > NOW()
        """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("userId", userId)
                            .bind("sessionId", sessionId)
                            .execute()
            );

            System.out.println("✅ Transferred " + rows + " reservations to user ID " + userId);
            return rows > 0;

        } catch (Exception e) {
            System.err.println("Error transferring session to user: " + e.getMessage());
            return false;
        }
    }
    public boolean forceBookSeat(int showtimeId, int seatId, int orderId, int userId) {
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

            System.out.println("✅ Force booked seat: showtimeId=" + showtimeId +
                    ", seatId=" + seatId + ", orderId=" + orderId +
                    ", rows affected=" + rows);
            return rows > 0;

        } catch (Exception e) {
            System.err.println("Error force booking seat: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean isSeatAvailableForPayment(int showtimeId, int seatId, int userId) {
        try {
            System.out.println("\n🎯 SEAT AVAILABILITY FOR PAYMENT 🎯");
            System.out.println("  Showtime: " + showtimeId);
            System.out.println("  Seat: " + seatId);
            System.out.println("  User: " + userId);

            String query = "SELECT status, user_id, order_id FROM booked_seats " +
                    "WHERE showtime_id = ? AND seat_id = ?";

            Map<String, Object> row = get().withHandle(handle ->
                    handle.createQuery(query)
                            .bind(0, showtimeId)
                            .bind(1, seatId)
                            .mapToMap()
                            .findFirst()
                            .orElse(null)
            );

            if (row == null) {
                System.out.println("✅ No record - DEFINITELY AVAILABLE");
                return true;
            }

            String status = (String) row.get("status");
            Object reservedUserIdObj = row.get("user_id");
            Integer orderId = (Integer) row.get("order_id");

            System.out.println("  DB Record: " + row);
            System.out.println("  Status: " + status);
            System.out.println("  Reserved User ID (raw): " + reservedUserIdObj + " (type: " +
                    (reservedUserIdObj != null ? reservedUserIdObj.getClass() : "null") + ")");
            System.out.println("  Order ID: " + orderId);

            // Convert user_id từ Object sang Integer đúng cách
            Integer reservedUserId = null;
            if (reservedUserIdObj != null) {
                if (reservedUserIdObj instanceof Integer) {
                    reservedUserId = (Integer) reservedUserIdObj;
                } else if (reservedUserIdObj instanceof Long) {
                    reservedUserId = ((Long) reservedUserIdObj).intValue();
                } else if (reservedUserIdObj instanceof Number) {
                    reservedUserId = ((Number) reservedUserIdObj).intValue();
                } else {
                    System.out.println("⚠️ Unexpected user_id type: " + reservedUserIdObj.getClass());
                    // Thử parse
                    try {
                        reservedUserId = Integer.parseInt(reservedUserIdObj.toString());
                    } catch (NumberFormatException e) {
                        System.err.println("❌ Cannot parse user_id: " + reservedUserIdObj);
                    }
                }
            }

            System.out.println("  Reserved User ID (parsed): " + reservedUserId);

            // LOGIC ĐƠN GIẢN HƠN CHO THANH TOÁN
            if (status == null || "released".equals(status)) {
                System.out.println("✅ Status is null/released - AVAILABLE");
                return true;
            }

            if ("booked".equals(status) && orderId != null) {
                System.out.println("❌ Already booked with order - NOT AVAILABLE");
                return false;
            }

            if ("reserved".equals(status)) {
                if (reservedUserId == null) {
                    System.out.println("✅ Reserved but no user_id - AVAILABLE");
                    return true;
                }

                System.out.println("🔍 Comparing user IDs:");
                System.out.println("  - Reserved user: " + reservedUserId + " (int: " + reservedUserId.intValue() + ")");
                System.out.println("  - Current user: " + userId + " (int: " + userId + ")");
                System.out.println("  - Equal? " + (reservedUserId.intValue() == userId));

                if (reservedUserId.intValue() == userId) {
                    System.out.println("✅ Reserved by SAME user - AVAILABLE for payment");
                    return true;
                } else {
                    System.out.println("❌ Reserved by DIFFERENT user - NOT AVAILABLE");
                    return false;
                }
            }

            System.out.println("❓ Unknown status: " + status + " - NOT AVAILABLE (default)");
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error in isSeatAvailableForPayment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean extendReservation(int showtimeId, int seatId, int userId) {
        try {
            String query = "UPDATE booked_seats SET " +
                    "reserved_until = DATE_ADD(NOW(), INTERVAL 15 MINUTE) " +
                    "WHERE showtime_id = ? AND seat_id = ? " +
                    "AND user_id = ? AND status = 'reserved'";

            int rows = get().withHandle(handle ->
                    handle.createUpdate(query)
                            .bind(0, showtimeId)
                            .bind(1, seatId)
                            .bind(2, userId)
                            .execute()
            );

            System.out.println("⏰ Extended reservation for seat: " + seatId + ", rows: " + rows);
            return rows > 0;
        } catch (Exception e) {
            System.err.println("❌ Error extending reservation: " + e.getMessage());
            return false;
        }
    }
    public Map<String, Object> getSeatStatus(int showtimeId, int seatId) {
        try {
            String query = "SELECT status, user_id, order_id, reserved_until, created_at " +
                    "FROM booked_seats " +
                    "WHERE showtime_id = ? AND seat_id = ?";

            return get().withHandle(handle ->
                    handle.createQuery(query)
                            .bind(0, showtimeId)
                            .bind(1, seatId)
                            .mapToMap()
                            .findFirst()
                            .orElse(new HashMap<String, Object>() {{
                                put("status", "available");
                                put("user_id", null);
                                put("order_id", null);
                                put("reserved_until", null);
                                put("created_at", null);
                            }})
            );
        } catch (Exception e) {
            System.err.println("❌ Error getting seat status: " + e.getMessage());
            return new HashMap<>();
        }
    }
}