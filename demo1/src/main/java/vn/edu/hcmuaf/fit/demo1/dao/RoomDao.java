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
            room.setActive(rs.getBoolean("is_active"));
            return room;
        }
    }

    // Lấy tất cả phòng đang hoạt động
    public List<Room> getAllActiveRooms() {
        String sql = "SELECT * FROM rooms WHERE is_active = true ORDER BY room_name";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new RoomMapper())
                        .list()
        );
    }

    // Lấy phòng theo ID
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

    // Lấy các phòng có suất chiếu cho phim cụ thể
    public List<Room> getRoomsByMovieId(int movieId) {
        System.out.println("RoomDao.getRoomsByMovieId - movieId: " + movieId);

        String sql = """
            SELECT DISTINCT r.*
            FROM rooms r
            INNER JOIN showtimes s ON r.id = s.room_id
            INNER JOIN movies m ON s.movie_id = m.id
            WHERE s.movie_id = :movieId 
              AND s.is_active = true
              AND s.show_date >= CURDATE()
              AND r.is_active = true
            ORDER BY r.room_name
            """;

        try {
            List<Room> rooms = get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("movieId", movieId)
                            .map(new RoomMapper())
                            .list()
            );

            System.out.println("RoomDao.getRoomsByMovieId - query executed, found " +
                    (rooms != null ? rooms.size() : 0) + " rooms");
            return rooms;

        } catch (Exception e) {
            System.err.println("Error in RoomDao.getRoomsByMovieId: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Kiểm tra phòng có tồn tại và active không
    public boolean isRoomActive(int roomId) {
        String sql = "SELECT is_active FROM rooms WHERE id = :id";

        Boolean isActive = get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("id", roomId)
                        .mapTo(Boolean.class)
                        .findOne()
                        .orElse(false)
        );

        return isActive;
    }
}