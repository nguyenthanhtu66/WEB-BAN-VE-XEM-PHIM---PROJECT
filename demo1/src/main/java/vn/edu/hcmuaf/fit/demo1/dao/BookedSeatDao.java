package vn.edu.hcmuaf.fit.demo1.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class BookedSeatDao extends BaseDao {

    // Giữ ghế tạm thời
    public String reserveSeats(int showtimeId, int userId, List<String> seatCodes, int reservationMinutes) {
        try {
            // 1. Xóa các reservation cũ đã hết hạn
            clearExpiredReservations();

            // 2. Tạo reservation ID
            String reservationId = "RES-" + System.currentTimeMillis() + "-" + userId;

            // 3. Tính thời gian hết hạn
            LocalDateTime reservedUntil = LocalDateTime.now().plusMinutes(reservationMinutes);
            Timestamp reservedUntilTimestamp = Timestamp.valueOf(reservedUntil);

            // 4. Lấy danh sách seat_id từ seat_codes
            String getSeatIdsSql = """
                SELECT s.id, s.seat_code 
                FROM seats s
                JOIN showtimes st ON st.room_id = s.room_id
                WHERE st.id = :showtimeId 
                    AND s.seat_code IN (<seatCodes>)
                    AND s.is_active = true
                """;

            List<Map<String, Object>> seatInfoList = get().withHandle(handle -> {
                var query = handle.createQuery(getSeatIdsSql);

                // Build dynamic IN clause
                var seatCodesArray = seatCodes.toArray(new String[0]);
                query.bindList("seatCodes", seatCodesArray);
                query.bind("showtimeId", showtimeId);

                return query.mapToMap().list();
            });

            if (seatInfoList.isEmpty()) {
                System.out.println("Không tìm thấy ghế nào hợp lệ");
                return null;
            }

            // 5. Giữ từng ghế
            for (Map<String, Object> seatInfo : seatInfoList) {
                Integer seatId = (Integer) seatInfo.get("id");
                String seatCode = (String) seatInfo.get("seat_code");

                // Kiểm tra ghế đã được đặt/chưa
                if (!isSeatAvailable(showtimeId, seatCode)) {
                    System.out.println("Ghế " + seatCode + " không khả dụng");
                    continue;
                }

                String insertSql = """
                    INSERT INTO booked_seats (showtime_id, seat_id, user_id, status, reserved_until)
                    VALUES (:showtimeId, :seatId, :userId, 'reserved', :reservedUntil)
                    ON DUPLICATE KEY UPDATE 
                        user_id = VALUES(user_id),
                        status = 'reserved',
                        reserved_until = VALUES(reserved_until),
                        created_at = NOW()
                    """;

                try {
                    int affectedRows = get().withHandle(handle ->
                            handle.createUpdate(insertSql)
                                    .bind("showtimeId", showtimeId)
                                    .bind("seatId", seatId)
                                    .bind("userId", userId)
                                    .bind("reservedUntil", reservedUntilTimestamp)
                                    .execute()
                    );

                    if (affectedRows > 0) {
                        System.out.println("Đã giữ ghế: " + seatCode);
                    }

                } catch (Exception e) {
                    System.err.println("Lỗi khi giữ ghế " + seatCode + ": " + e.getMessage());
                    continue;
                }
            }

            return reservationId;

        } catch (Exception e) {
            System.err.println("Lỗi khi reserve seats: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Hủy giữ ghế theo reservationId
    public boolean releaseSeats(String reservationId) {
        // Note: Cần thêm cột reservation_id vào bảng booked_seats để tracking
        // Tạm thời dùng cách khác
        System.out.println("Release seats với reservationId: " + reservationId);
        return true;
    }

    // Hủy giữ ghế theo seat code
    public boolean releaseSeat(int showtimeId, String seatCode) {
        String sql = """
            UPDATE booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            SET bs.status = 'released'
            WHERE bs.showtime_id = :showtimeId
                AND s.seat_code = :seatCode
                AND bs.status = 'reserved'
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatCode", seatCode)
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa các reservation đã hết hạn
    public void clearExpiredReservations() {
        String sql = """
            UPDATE booked_seats 
            SET status = 'released' 
            WHERE status = 'reserved' 
                AND reserved_until < NOW()
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql).execute()
            );
            if (rows > 0) {
                System.out.println("Đã xóa " + rows + " reservation hết hạn");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi clear expired reservations: " + e.getMessage());
        }
    }

    // Kiểm tra ghế có sẵn để đặt không
    public boolean isSeatAvailable(int showtimeId, String seatCode) {
        String sql = """
            SELECT COUNT(*) 
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            WHERE bs.showtime_id = :showtimeId
                AND s.seat_code = :seatCode
                AND bs.status IN ('reserved', 'booked')
                AND (bs.status != 'reserved' OR bs.reserved_until > NOW())
            """;

        try {
            int count = get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("seatCode", seatCode)
                            .mapTo(Integer.class)
                            .one()
            );
            return count == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy trạng thái ghế cho một showtime
    public Map<String, String> getSeatStatusForShowtime(int showtimeId) {
        String sql = """
            SELECT 
                s.seat_code,
                CASE 
                    WHEN bs.id IS NOT NULL AND bs.status = 'booked' THEN 'BOOKED'
                    WHEN bs.id IS NOT NULL AND bs.status = 'reserved' 
                         AND bs.reserved_until > NOW() THEN 'RESERVED'
                    ELSE 'AVAILABLE'
                END as seat_status
            FROM seats s
            JOIN showtimes st ON st.room_id = s.room_id
            LEFT JOIN booked_seats bs ON bs.showtime_id = st.id AND bs.seat_id = s.id
            WHERE st.id = :showtimeId
                AND s.is_active = true
            ORDER BY s.row_number, s.seat_number
            """;

        try {
            List<Map<String, Object>> resultList = get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .mapToMap()  // Sửa ở đây - không có tham số
                            .list()
            );

            // Chuyển List<Map> thành Map<String, String>
            Map<String, String> seatStatusMap = new HashMap<>();
            for (Map<String, Object> row : resultList) {
                String seatCode = (String) row.get("seat_code");
                String status = (String) row.get("seat_status");
                seatStatusMap.put(seatCode, status);
            }

            return seatStatusMap;

        } catch (Exception e) {
            System.err.println("Lỗi khi lấy seat status: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    // Lấy thông tin reservation
    public Map<String, Object> getReservationInfo(int reservationId) {
        String sql = """
            SELECT bs.*, s.seat_code, m.title as movie_title, st.show_date, st.show_time
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            JOIN showtimes st ON bs.showtime_id = st.id
            JOIN movies m ON st.movie_id = m.id
            WHERE bs.id = :reservationId
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("reservationId", reservationId)
                            .mapToMap()
                            .findOne()
                            .orElse(null)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lấy danh sách ghế đã đặt cho một showtime
    public List<Map<String, Object>> getBookedSeatsForShowtime(int showtimeId) {
        String sql = """
            SELECT 
                s.seat_code,
                s.seat_type,
                bs.status,
                bs.reserved_until,
                u.email as user_email
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            LEFT JOIN users u ON bs.user_id = u.id
            WHERE bs.showtime_id = :showtimeId
                AND bs.status IN ('reserved', 'booked')
                AND (bs.status != 'reserved' OR bs.reserved_until > NOW())
            ORDER BY s.row_number, s.seat_number
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .mapToMap()
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Lấy số ghế còn trống cho một showtime
    public int getAvailableSeatsCount(int showtimeId) {
        String sql = """
            SELECT COUNT(*) 
            FROM seats s
            JOIN showtimes st ON st.room_id = s.room_id
            LEFT JOIN booked_seats bs ON bs.showtime_id = st.id AND bs.seat_id = s.id
            WHERE st.id = :showtimeId
                AND s.is_active = true
                AND (bs.id IS NULL OR bs.status = 'released' 
                     OR (bs.status = 'reserved' AND bs.reserved_until <= NOW()))
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

    // Xác nhận đặt ghế (chuyển từ reserved sang booked)
    public boolean confirmBooking(int showtimeId, List<String> seatCodes, int userId) {
        try {
            String sql = """
                UPDATE booked_seats bs
                JOIN seats s ON bs.seat_id = s.id
                SET bs.status = 'booked',
                    bs.reserved_until = NULL
                WHERE bs.showtime_id = :showtimeId
                    AND s.seat_code IN (<seatCodes>)
                    AND bs.user_id = :userId
                    AND bs.status = 'reserved'
                """;

            int rows = get().withHandle(handle -> {
                var update = handle.createUpdate(sql);
                update.bindList("seatCodes", seatCodes);
                update.bind("showtimeId", showtimeId);
                update.bind("userId", userId);
                return update.execute();
            });

            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra xem user đã giữ ghế nào chưa
    public List<String> getUserReservedSeats(int showtimeId, int userId) {
        String sql = """
            SELECT s.seat_code
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            WHERE bs.showtime_id = :showtimeId
                AND bs.user_id = :userId
                AND bs.status = 'reserved'
                AND bs.reserved_until > NOW()
            ORDER BY s.seat_code
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("userId", userId)
                            .mapTo(String.class)
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Xóa tất cả reservation của một user
    public boolean clearUserReservations(int userId) {
        String sql = """
            UPDATE booked_seats 
            SET status = 'released' 
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

    // Lấy thông tin ghế đã đặt của một user
    public List<Map<String, Object>> getUserBookings(int userId) {
        String sql = """
            SELECT 
                bs.*,
                s.seat_code,
                m.title as movie_title,
                st.show_date,
                st.show_time,
                r.room_name
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            JOIN showtimes st ON bs.showtime_id = st.id
            JOIN movies m ON st.movie_id = m.id
            JOIN rooms r ON st.room_id = r.id
            WHERE bs.user_id = :userId
                AND bs.status IN ('reserved', 'booked')
            ORDER BY bs.reserved_until DESC, bs.created_at DESC
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("userId", userId)
                            .mapToMap()
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Kiểm tra xem có reservation nào sắp hết hạn không
    public List<Map<String, Object>> getExpiringReservations(int minutesBeforeExpire) {
        String sql = """
            SELECT 
                bs.*,
                s.seat_code,
                u.email,
                TIMESTAMPDIFF(SECOND, NOW(), bs.reserved_until) as seconds_remaining
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            JOIN users u ON bs.user_id = u.id
            WHERE bs.status = 'reserved'
                AND bs.reserved_until > NOW()
                AND bs.reserved_until <= DATE_ADD(NOW(), INTERVAL :minutes MINUTE)
            ORDER BY bs.reserved_until ASC
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("minutes", minutesBeforeExpire)
                            .mapToMap()
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}