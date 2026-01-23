package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.Seat;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

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

    public Seat getSeatById(int seatId) {
        String sql = "SELECT * FROM seats WHERE id = :seatId AND is_active = TRUE";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("seatId", seatId)
                        .map(new SeatMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    public Seat getSeatByCode(int roomId, String seatCode) {
        String sql = """
            SELECT * FROM seats 
            WHERE room_id = :roomId 
            AND seat_code = :seatCode
            AND is_active = TRUE
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

    public List<Seat> getAllSeats() {
        String sql = "SELECT * FROM seats WHERE is_active = TRUE ORDER BY room_id, row_number, seat_number";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new SeatMapper())
                        .list()
        );
    }
}