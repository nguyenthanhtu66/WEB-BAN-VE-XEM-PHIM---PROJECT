package vn.edu.hcmuaf.fit.demo1.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import vn.edu.hcmuaf.fit.demo1.model.Room;

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
            room.setIsActive(rs.getBoolean("is_active"));
            return room;
        }
    }

    // Lấy phòng theo ID
    public Room getRoomById(int roomId) {
        String sql = "SELECT * FROM rooms WHERE id = :roomId";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomId", roomId)
                        .map(new RoomMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Lấy phòng theo tên
    public Room getRoomByName(String roomName) {
        String sql = "SELECT * FROM rooms WHERE room_name = :roomName AND is_active = TRUE";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomName", roomName)
                        .map(new RoomMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Kiểm tra phòng có sẵn sàng cho suất chiếu
    public boolean isRoomAvailable(int roomId, String date, String time) {
        String sql = """
            SELECT COUNT(*) FROM showtimes 
            WHERE room_id = :roomId 
            AND show_date = :date 
            AND show_time = :time
            AND is_active = TRUE
            """;

        int count = get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomId", roomId)
                        .bind("date", date)
                        .bind("time", time)
                        .mapTo(Integer.class)
                        .one()
        );

        return count == 0;
    }
    // Lấy phòng có suất chiếu cho phim cụ thể
    public List<Room> getRoomsForMovie(int movieId) {
        String sql = """
        SELECT DISTINCT r.* 
        FROM rooms r
        JOIN showtimes st ON r.id = st.room_id
        WHERE st.movie_id = :movieId
        AND st.is_active = TRUE
        AND st.show_date >= CURDATE()
        AND r.is_active = TRUE
        ORDER BY r.room_name
        """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("movieId", movieId)
                        .map(new RoomMapper())
                        .list()
        );
    }

    // Lấy tất cả phòng đang hoạt động
    public List<Room> getAllActiveRooms() {
        String sql = """
        SELECT * FROM rooms 
        WHERE is_active = TRUE 
        ORDER BY room_name
        """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new RoomMapper())
                        .list()
        );
    }
}