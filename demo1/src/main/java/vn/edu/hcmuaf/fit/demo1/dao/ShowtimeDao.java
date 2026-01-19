package vn.edu.hcmuaf.fit.demo1.dao;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import vn.edu.hcmuaf.fit.demo1.model.Showtime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ShowtimeDao extends BaseDao {

    private static class ShowtimeMapper implements RowMapper<Showtime> {
        @Override
        public Showtime map(ResultSet rs, StatementContext ctx) throws SQLException {
            Showtime showtime = new Showtime();
            showtime.setId(rs.getInt("id"));
            showtime.setMovieId(rs.getInt("movie_id"));
            showtime.setRoomId(rs.getInt("room_id"));
            showtime.setShowDate(rs.getDate("show_date").toLocalDate());
            showtime.setShowTime(rs.getTime("show_time").toLocalTime());
            showtime.setActive(rs.getBoolean("is_active"));

            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                showtime.setCreatedAt(createdAt.toLocalDateTime());
            }

            return showtime;
        }
    }

    // Lấy suất chiếu theo ID
    public Showtime getShowtimeById(int showtimeId) {
        String sql = """
            SELECT * FROM showtimes 
            WHERE id = :showtimeId 
              AND is_active = true
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .map(new ShowtimeMapper())
                            .findOne()
                            .orElse(null)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lấy suất chiếu theo phim
    public List<Showtime> getShowtimesByMovie(int movieId) {
        String sql = """
            SELECT * FROM showtimes 
            WHERE movie_id = :movieId 
              AND is_active = true
              AND (show_date > CURDATE() OR (show_date = CURDATE() AND show_time > CURTIME()))
            ORDER BY show_date, show_time
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("movieId", movieId)
                            .map(new ShowtimeMapper())
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Lấy suất chiếu theo ngày
    public List<Showtime> getShowtimesByDate(LocalDate date) {
        String sql = """
            SELECT * FROM showtimes 
            WHERE show_date = :date 
              AND is_active = true
              AND (show_date > CURDATE() OR (show_date = CURDATE() AND show_time > CURTIME()))
            ORDER BY show_time
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("date", date)
                            .map(new ShowtimeMapper())
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Lấy suất chiếu theo phim và ngày
    public List<Showtime> getShowtimesByMovieAndDate(int movieId, LocalDate date) {
        String sql = """
            SELECT * FROM showtimes 
            WHERE movie_id = :movieId 
              AND show_date = :date 
              AND is_active = true
              AND (show_date > CURDATE() OR (show_date = CURDATE() AND show_time > CURTIME()))
            ORDER BY show_time
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("movieId", movieId)
                            .bind("date", date)
                            .map(new ShowtimeMapper())
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Lấy suất chiếu theo phòng
    public List<Showtime> getShowtimesByRoom(int roomId) {
        String sql = """
            SELECT * FROM showtimes 
            WHERE room_id = :roomId 
              AND is_active = true
              AND (show_date > CURDATE() OR (show_date = CURDATE() AND show_time > CURTIME()))
            ORDER BY show_date, show_time
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("roomId", roomId)
                            .map(new ShowtimeMapper())
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Lấy suất chiếu sắp tới (hôm nay và ngày mai)
    public List<Showtime> getUpcomingShowtimes(int limit) {
        String sql = """
            SELECT * FROM showtimes 
            WHERE is_active = true
              AND (show_date = CURDATE() OR show_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY))
              AND (show_date > CURDATE() OR (show_date = CURDATE() AND show_time > CURTIME()))
            ORDER BY show_date, show_time
            LIMIT :limit
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("limit", limit)
                            .map(new ShowtimeMapper())
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Kiểm tra xem có xung đột thời gian không
    public boolean hasTimeConflict(int roomId, LocalDate date, LocalTime time, int duration) {
        String sql = """
            SELECT COUNT(*) > 0
            FROM showtimes
            WHERE room_id = :roomId
              AND show_date = :date
              AND is_active = true
              AND (
                  (show_time <= :time AND ADDTIME(show_time, SEC_TO_TIME(:duration * 60)) > :time)
                  OR (show_time > :time AND show_time < ADDTIME(:time, SEC_TO_TIME(:duration * 60)))
              )
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("roomId", roomId)
                            .bind("date", date)
                            .bind("time", time)
                            .bind("duration", duration)
                            .mapTo(Boolean.class)
                            .one()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy thông tin phòng từ suất chiếu
    public Map<String, Object> getRoomInfo(int roomId) {
        String sql = """
            SELECT 
                id, 
                room_name, 
                total_seats, 
                room_type, 
                is_active
            FROM rooms 
            WHERE id = :roomId 
              AND is_active = true
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("roomId", roomId)
                            .map((rs, ctx) -> {
                                Map<String, Object> room = new HashMap<>();
                                room.put("id", rs.getInt("id"));
                                room.put("roomName", rs.getString("room_name"));
                                room.put("totalSeats", rs.getInt("total_seats"));
                                room.put("roomType", rs.getString("room_type"));
                                room.put("isActive", rs.getBoolean("is_active"));
                                return room;
                            })
                            .findOne()
                            .orElse(null)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lấy tất cả suất chiếu trong khoảng thời gian
    public List<Showtime> getShowtimesInDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT * FROM showtimes 
            WHERE show_date BETWEEN :startDate AND :endDate
              AND is_active = true
            ORDER BY show_date, show_time
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("startDate", startDate)
                            .bind("endDate", endDate)
                            .map(new ShowtimeMapper())
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Lấy suất chiếu theo loại phòng
    public List<Showtime> getShowtimesByRoomType(String roomType) {
        String sql = """
            SELECT st.* 
            FROM showtimes st
            JOIN rooms r ON st.room_id = r.id
            WHERE r.room_type = :roomType
              AND st.is_active = true
              AND (st.show_date > CURDATE() OR (st.show_date = CURDATE() AND st.show_time > CURTIME()))
            ORDER BY st.show_date, st.show_time
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("roomType", roomType)
                            .map(new ShowtimeMapper())
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Lấy suất chiếu với thông tin phim
    public List<Map<String, Object>> getShowtimesWithMovieInfo(LocalDate date) {
        String sql = """
            SELECT 
                st.*,
                m.title as movie_title,
                m.poster_url,
                m.duration,
                m.genre,
                r.room_name,
                r.room_type
            FROM showtimes st
            JOIN movies m ON st.movie_id = m.id
            JOIN rooms r ON st.room_id = r.id
            WHERE st.show_date = :date
              AND st.is_active = true
              AND m.status IN ('showing', 'upcoming')
              AND (st.show_date > CURDATE() OR (st.show_date = CURDATE() AND st.show_time > CURTIME()))
            ORDER BY st.show_time, r.room_name
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("date", date)
                            .map((rs, ctx) -> {
                                Map<String, Object> showtimeInfo = new HashMap<>();

                                // Thông tin suất chiếu
                                Showtime showtime = new ShowtimeMapper().map(rs, ctx);
                                showtimeInfo.put("showtime", showtime);

                                // Thông tin phim
                                showtimeInfo.put("movieTitle", rs.getString("movie_title"));
                                showtimeInfo.put("posterUrl", rs.getString("poster_url"));
                                showtimeInfo.put("duration", rs.getInt("duration"));
                                showtimeInfo.put("genre", rs.getString("genre"));

                                // Thông tin phòng
                                showtimeInfo.put("roomName", rs.getString("room_name"));
                                showtimeInfo.put("roomType", rs.getString("room_type"));

                                return showtimeInfo;
                            })
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Đếm số suất chiếu của phim
    public int countShowtimesByMovie(int movieId) {
        String sql = """
            SELECT COUNT(*) 
            FROM showtimes 
            WHERE movie_id = :movieId 
              AND is_active = true
              AND (show_date > CURDATE() OR (show_date = CURDATE() AND show_time > CURTIME()))
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("movieId", movieId)
                            .mapTo(Integer.class)
                            .one()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Kiểm tra suất chiếu có còn chỗ không
    public boolean hasAvailableSeats(int showtimeId) {
        String sql = """
            SELECT 
                (r.total_seats - COALESCE((
                    SELECT COUNT(DISTINCT bs.seat_id)
                    FROM booked_seats bs
                    WHERE bs.showtime_id = :showtimeId
                      AND (bs.status = 'booked' OR (bs.status = 'reserved' AND bs.reserved_until > NOW()))
                ), 0)) > 0
            FROM showtimes st
            JOIN rooms r ON st.room_id = r.id
            WHERE st.id = :showtimeId
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .mapTo(Boolean.class)
                            .one()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy số ghế còn trống
    public int getAvailableSeatsCount(int showtimeId) {
        String sql = """
            SELECT 
                r.total_seats - COALESCE((
                    SELECT COUNT(DISTINCT bs.seat_id)
                    FROM booked_seats bs
                    WHERE bs.showtime_id = :showtimeId
                      AND (bs.status = 'booked' OR (bs.status = 'reserved' AND bs.reserved_until > NOW()))
                ), 0) as available_seats
            FROM showtimes st
            JOIN rooms r ON st.room_id = r.id
            WHERE st.id = :showtimeId
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

    // Lấy thời gian chiếu gần nhất của phim
    public Showtime getNearestShowtimeForMovie(int movieId) {
        String sql = """
            SELECT * FROM showtimes 
            WHERE movie_id = :movieId 
              AND is_active = true
              AND (show_date > CURDATE() OR (show_date = CURDATE() AND show_time > CURTIME()))
            ORDER BY show_date, show_time
            LIMIT 1
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("movieId", movieId)
                            .map(new ShowtimeMapper())
                            .findOne()
                            .orElse(null)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lấy danh sách ngày có suất chiếu cho phim
    public List<LocalDate> getShowDatesForMovie(int movieId) {
        String sql = """
            SELECT DISTINCT show_date
            FROM showtimes
            WHERE movie_id = :movieId
              AND is_active = true
              AND (show_date > CURDATE() OR (show_date = CURDATE() AND show_time > CURTIME()))
            ORDER BY show_date
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("movieId", movieId)
                            .map((rs, ctx) -> rs.getDate("show_date").toLocalDate())
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Tạo suất chiếu mới
    public boolean createShowtime(Showtime showtime) {
        String sql = """
            INSERT INTO showtimes (movie_id, room_id, show_date, show_time, is_active)
            VALUES (:movieId, :roomId, :showDate, :showTime, true)
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("movieId", showtime.getMovieId())
                            .bind("roomId", showtime.getRoomId())
                            .bind("showDate", showtime.getShowDate())
                            .bind("showTime", showtime.getShowTime())
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật suất chiếu
    public boolean updateShowtime(Showtime showtime) {
        String sql = """
            UPDATE showtimes SET
                movie_id = :movieId,
                room_id = :roomId,
                show_date = :showDate,
                show_time = :showTime,
                is_active = :isActive
            WHERE id = :id
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("id", showtime.getId())
                            .bind("movieId", showtime.getMovieId())
                            .bind("roomId", showtime.getRoomId())
                            .bind("showDate", showtime.getShowDate())
                            .bind("showTime", showtime.getShowTime())
                            .bind("isActive", showtime.isActive())
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa suất chiếu (soft delete)
    public boolean deleteShowtime(int showtimeId) {
        String sql = """
            UPDATE showtimes SET
                is_active = false
            WHERE id = :showtimeId
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("showtimeId", showtimeId)
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}