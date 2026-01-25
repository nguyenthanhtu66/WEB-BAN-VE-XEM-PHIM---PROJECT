package vn.edu.hcmuaf.fit.demo1.dao;

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
            seat.setIsActive(rs.getBoolean("is_active"));
            return seat;
        }
    }

    // Lấy tất cả ghế của một phòng
    public List<Seat> getSeatsByRoom(int roomId) {
        String sql = """
            SELECT * FROM seats 
            WHERE room_id = :roomId 
            AND is_active = TRUE
            ORDER BY row_number, seat_number
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomId", roomId)
                        .map(new SeatMapper())
                        .list()
        );
    }

    // Lấy ghế theo ID
    public Seat getSeatById(int seatId) {
        String sql = "SELECT * FROM seats WHERE id = :seatId";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("seatId", seatId)
                        .map(new SeatMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Lấy ghế theo mã ghế và phòng
    public Seat getSeatByCode(int roomId, String seatCode) {
        String sql = "SELECT * FROM seats WHERE room_id = :roomId AND seat_code = :seatCode";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomId", roomId)
                        .bind("seatCode", seatCode)
                        .map(new SeatMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Kiểm tra ghế có bị đặt/giữ chưa cho một suất chiếu
    public boolean isSeatAvailable(int showtimeId, int seatId) {
        String sql = """
            SELECT COUNT(*) FROM booked_seats 
            WHERE showtime_id = :showtimeId 
            AND seat_id = :seatId 
            AND status IN ('reserved', 'booked')
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

    // Kiểm tra trạng thái nhiều ghế cùng lúc
    public List<String> getUnavailableSeatCodes(int showtimeId, List<Integer> seatIds) {
        String sql = """
            SELECT DISTINCT s.seat_code 
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            WHERE bs.showtime_id = :showtimeId 
            AND bs.seat_id IN (<seatIds>)
            AND bs.status IN ('reserved', 'booked')
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .bindList("seatIds", seatIds)
                        .mapTo(String.class)
                        .list()
        );
    }
}