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
            seat.setActive(rs.getBoolean("is_active"));
            return seat;
        }
    }

    // Lấy tất cả ghế trong một phòng
    public List<Seat> getSeatsByRoomId(int roomId) {
        String sql = """
            SELECT * FROM seats 
            WHERE room_id = :roomId AND is_active = true
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
    public Seat getSeatById(int id) {
        String sql = "SELECT * FROM seats WHERE id = :id";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("id", id)
                        .map(new SeatMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Lấy ghế theo seat code và room
    public Seat getSeatByCodeAndRoom(String seatCode, int roomId) {
        String sql = """
            SELECT * FROM seats 
            WHERE seat_code = :seatCode AND room_id = :roomId
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("seatCode", seatCode)
                        .bind("roomId", roomId)
                        .map(new SeatMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Đếm số ghế trong phòng
    public int countSeatsByRoom(int roomId) {
        String sql = "SELECT COUNT(*) FROM seats WHERE room_id = :roomId AND is_active = true";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomId", roomId)
                        .mapTo(Integer.class)
                        .one()
        );
    }

    // Lấy danh sách hàng ghế (row numbers) trong phòng
    public List<String> getRowNumbersByRoom(int roomId) {
        String sql = """
            SELECT DISTINCT row_number 
            FROM seats 
            WHERE room_id = :roomId AND is_active = true
            ORDER BY row_number
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomId", roomId)
                        .mapTo(String.class)
                        .list()
        );
    }
}