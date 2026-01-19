package vn.edu.hcmuaf.fit.demo1.dao;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import vn.edu.hcmuaf.fit.demo1.model.Seat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SeatDao extends BaseDao {

    private static class SeatMapper implements RowMapper<Seat> {
        @Override
        public Seat map(ResultSet rs, StatementContext ctx) throws SQLException {
            Seat seat = new Seat();
            seat.setId(rs.getInt("id"));
            seat.setRoomId(rs.getInt("room_id"));
            seat.setSeatCode(rs.getString("seat_code"));
            seat.setRowNumber(rs.getString("row_number"));
            seat.setSeatNumber(rs.getInt("seat_number"));
            seat.setSeatType(rs.getString("seat_type"));
            seat.setActive(rs.getBoolean("is_active"));
            return seat;
        }
    }

    // Lấy ghế theo ID
    public Seat getSeatById(int seatId) {
        String sql = "SELECT * FROM seats WHERE id = :seatId AND is_active = true";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("seatId", seatId)
                        .map(new SeatMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Lấy ghế theo mã và phòng
    public Seat getSeatByCode(int roomId, String seatCode) {
        String sql = """
            SELECT * FROM seats 
            WHERE room_id = :roomId 
              AND seat_code = :seatCode 
              AND is_active = true
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomId", roomId)
                        .bind("seatCode", seatCode)
                        .map(new SeatMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Lấy tất cả ghế trong phòng
    public List<Seat> getSeatsByRoom(int roomId) {
        String sql = """
            SELECT * FROM seats 
            WHERE room_id = :roomId 
              AND is_active = true
            ORDER BY row_number, seat_number
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomId", roomId)
                        .map(new SeatMapper())
                        .list()
        );
    }

    // Kiểm tra ghế có sẵn cho suất chiếu không
    public boolean isSeatAvailable(int seatId, int showtimeId) {
        String sql = """
            SELECT COUNT(*) = 0
            FROM booked_seats bs
            WHERE bs.seat_id = :seatId
              AND bs.showtime_id = :showtimeId
              AND (
                  bs.status = 'booked' 
                  OR (bs.status = 'reserved' AND bs.reserved_until > NOW())
              )
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("seatId", seatId)
                        .bind("showtimeId", showtimeId)
                        .mapTo(Boolean.class)
                        .one()
        );
    }

    // Lấy ghế theo loại (normal, vip)
    public List<Seat> getSeatsByType(int roomId, String seatType) {
        String sql = """
            SELECT * FROM seats 
            WHERE room_id = :roomId 
              AND seat_type = :seatType
              AND is_active = true
            ORDER BY row_number, seat_number
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomId", roomId)
                        .bind("seatType", seatType)
                        .map(new SeatMapper())
                        .list()
        );
    }

    // Đếm số ghế còn trống trong phòng cho suất chiếu
    public int countAvailableSeats(int roomId, int showtimeId) {
        String sql = """
            SELECT COUNT(*)
            FROM seats s
            WHERE s.room_id = :roomId
              AND s.is_active = true
              AND NOT EXISTS (
                  SELECT 1 FROM booked_seats bs
                  WHERE bs.seat_id = s.id
                    AND bs.showtime_id = :showtimeId
                    AND (
                        bs.status = 'booked' 
                        OR (bs.status = 'reserved' AND bs.reserved_until > NOW())
                    )
              )
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomId", roomId)
                        .bind("showtimeId", showtimeId)
                        .mapTo(Integer.class)
                        .one()
        );
    }

    // Lấy thông tin ghế với trạng thái cho suất chiếu
    public List<Seat> getSeatsWithStatus(int roomId, int showtimeId) {
        String sql = """
            SELECT 
                s.*,
                CASE 
                    WHEN bs.status = 'booked' THEN 'booked'
                    WHEN bs.status = 'reserved' AND bs.reserved_until > NOW() THEN 'reserved'
                    ELSE 'available'
                END as seat_status
            FROM seats s
            LEFT JOIN booked_seats bs ON s.id = bs.seat_id 
                AND bs.showtime_id = :showtimeId
                AND (bs.status = 'booked' OR (bs.status = 'reserved' AND bs.reserved_until > NOW()))
            WHERE s.room_id = :roomId 
              AND s.is_active = true
            ORDER BY s.row_number, s.seat_number
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomId", roomId)
                        .bind("showtimeId", showtimeId)
                        .map((rs, ctx) -> {
                            Seat seat = new SeatMapper().map(rs, ctx);
                            seat.setSeatStatus(rs.getString("seat_status"));
                            return seat;
                        })
                        .list()
        );
    }
}