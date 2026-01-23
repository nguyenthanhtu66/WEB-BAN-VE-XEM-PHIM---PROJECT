package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.Room;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RoomDao extends BaseDao {

    private static class RoomMapper implements RowMapper<Room> {
        @Override
        public Room map(ResultSet rs, StatementContext ctx) throws SQLException {
            Room room = new Room();
            room.setId(rs.getInt("id"));
            room.setRoomName(rs.getString("room_name"));
            room.setTotalSeats(rs.getInt("total_seats"));
            room.setRoomType(rs.getString("room_type"));
            room.setActive(rs.getBoolean("is_active"));
            return room;
        }
    }

    public List<Room> getAllRooms() {
        String sql = "SELECT * FROM rooms ORDER BY room_name";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new RoomMapper())
                        .list()
        );
    }

    public Room getRoomById(int id) {
        String sql = "SELECT * FROM rooms WHERE id = :id";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("id", id)
                        .map(new RoomMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    public Room getRoomByName(String name) {
        String sql = "SELECT * FROM rooms WHERE room_name = :name";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("name", name)
                        .map(new RoomMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    public List<Room> getActiveRooms() {
        String sql = "SELECT * FROM rooms WHERE is_active = TRUE ORDER BY room_name";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new RoomMapper())
                        .list()
        );
    }
}