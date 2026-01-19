package vn.edu.hcmuaf.fit.demo1.dao;

import org.jdbi.v3.core.Jdbi;
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

    // Lấy phòng theo ID
    public Room getRoomById(int roomId) {
        String sql = "SELECT * FROM rooms WHERE id = :roomId AND is_active = true";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomId", roomId)
                        .map(new RoomMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Lấy tất cả phòng
    public List<Room> getAllRooms() {
        String sql = "SELECT * FROM rooms WHERE is_active = true ORDER BY room_name";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new RoomMapper())
                        .list()
        );
    }

    // Lấy phòng theo loại (2D, 3D, VIP)
    public List<Room> getRoomsByType(String roomType) {
        String sql = """
            SELECT * FROM rooms 
            WHERE room_type = :roomType 
              AND is_active = true
            ORDER BY room_name
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomType", roomType)
                        .map(new RoomMapper())
                        .list()
        );
    }

    // Lấy phòng theo rạp (nếu có thông tin rạp trong database)
    public List<Room> getRoomsByCinema(String cinemaCode) {
        // Giả sử bảng rooms có cột cinema_code
        String sql = """
            SELECT * FROM rooms 
            WHERE cinema_code = :cinemaCode 
              AND is_active = true
            ORDER BY room_name
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("cinemaCode", cinemaCode)
                        .map(new RoomMapper())
                        .list()
        );
    }

    // Đếm số ghế còn trống trong phòng cho suất chiếu
    public int countAvailableSeatsInRoom(int roomId, int showtimeId) {
        String sql = """
            SELECT 
                r.total_seats - COUNT(DISTINCT bs.seat_id) as available_seats
            FROM rooms r
            LEFT JOIN seats s ON r.id = s.room_id
            LEFT JOIN booked_seats bs ON s.id = bs.seat_id 
                AND bs.showtime_id = :showtimeId
                AND (bs.status = 'booked' OR (bs.status = 'reserved' AND bs.reserved_until > NOW()))
            WHERE r.id = :roomId
              AND r.is_active = true
            GROUP BY r.id
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomId", roomId)
                        .bind("showtimeId", showtimeId)
                        .mapTo(Integer.class)
                        .findOne()
                        .orElse(0)
        );
    }
}