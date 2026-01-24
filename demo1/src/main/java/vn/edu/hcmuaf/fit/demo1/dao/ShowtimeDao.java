package vn.edu.hcmuaf.fit.demo1.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import vn.edu.hcmuaf.fit.demo1.model.Showtime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ShowtimeDao extends BaseDao {

    private static class ShowtimeMapper implements RowMapper<Showtime> {
        @Override
        public Showtime map(ResultSet rs, StatementContext ctx) throws SQLException {
            Showtime showtime = new Showtime();
            showtime.setId(rs.getInt("id"));
            showtime.setMovieId(rs.getInt("movie_id"));
            showtime.setRoomId(rs.getInt("room_id"));

            java.sql.Date showDate = rs.getDate("show_date");
            if (showDate != null) {
                showtime.setShowDate(showDate.toLocalDate());
            }

            java.sql.Time showTime = rs.getTime("show_time");
            if (showTime != null) {
                showtime.setShowTime(showTime.toLocalTime());
            }

            showtime.setIsActive(rs.getBoolean("is_active"));

            try {
                showtime.setMovieTitle(rs.getString("movie_title"));
                showtime.setRoomName(rs.getString("room_name"));
                showtime.setRoomType(rs.getString("room_type"));
            } catch (SQLException e) {
                // Không bắt buộc
            }

            return showtime;
        }
    }

    // PHƯƠNG THỨC MỚI: Tìm showtime theo movieId, roomId và datetime
    public Showtime findShowtime(int movieId, int roomId, LocalDateTime dateTime) {
        LocalDate showDate = dateTime.toLocalDate();
        LocalTime showTime = dateTime.toLocalTime();

        String sql = """
            SELECT s.*, m.title as movie_title, r.room_name, r.room_type
            FROM showtimes s
            JOIN movies m ON s.movie_id = m.id
            JOIN rooms r ON s.room_id = r.id
            WHERE s.movie_id = :movieId
            AND s.room_id = :roomId
            AND s.show_date = :showDate
            AND s.show_time = :showTime
            AND s.is_active = TRUE
            LIMIT 1
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("movieId", movieId)
                        .bind("roomId", roomId)
                        .bind("showDate", showDate)
                        .bind("showTime", showTime)
                        .map(new ShowtimeMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // PHƯƠNG THỨC MỚI: Tìm hoặc tạo showtime
    public Showtime findOrCreateShowtime(int movieId, int roomId, LocalDateTime dateTime) {
        // Tìm showtime đã tồn tại
        Showtime existing = findShowtime(movieId, roomId, dateTime);
        if (existing != null) {
            return existing;
        }

        // Tạo showtime mới
        LocalDate showDate = dateTime.toLocalDate();
        LocalTime showTime = dateTime.toLocalTime();

        // Kiểm tra phòng có trùng lịch không
        if (!isRoomAvailable(roomId, showDate, showTime)) {
            return null;
        }

        String sql = """
            INSERT INTO showtimes (movie_id, room_id, show_date, show_time, is_active)
            VALUES (:movieId, :roomId, :showDate, :showTime, TRUE)
            """;

        try {
            Integer id = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("movieId", movieId)
                            .bind("roomId", roomId)
                            .bind("showDate", showDate)
                            .bind("showTime", showTime)
                            .executeAndReturnGeneratedKeys("id")
                            .mapTo(Integer.class)
                            .one()
            );

            if (id != null) {
                return getShowtimeById(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Kiểm tra phòng có sẵn không
    public boolean isRoomAvailable(int roomId, LocalDate date, LocalTime time) {
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

    // Lấy showtime theo ID
    public Showtime getShowtimeById(int showtimeId) {
        String sql = """
            SELECT s.*, m.title as movie_title, r.room_name, r.room_type
            FROM showtimes s
            JOIN movies m ON s.movie_id = m.id
            JOIN rooms r ON s.room_id = r.id
            WHERE s.id = :showtimeId
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .map(new ShowtimeMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Lấy tất cả showtime theo phim
    public List<Showtime> getShowtimesByMovie(int movieId) {
        String sql = """
            SELECT s.*, m.title as movie_title, r.room_name, r.room_type
            FROM showtimes s
            JOIN movies m ON s.movie_id = m.id
            JOIN rooms r ON s.room_id = r.id
            WHERE s.movie_id = :movieId 
            AND s.is_active = TRUE
            AND s.show_date >= CURDATE()
            ORDER BY s.show_date, s.show_time
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("movieId", movieId)
                        .map(new ShowtimeMapper())
                        .list()
        );
    }

    // Lấy showtime theo phim và ngày
    public List<Showtime> getShowtimesByMovieAndDate(int movieId, LocalDate date) {
        String sql = """
            SELECT s.*, m.title as movie_title, r.room_name, r.room_type
            FROM showtimes s
            JOIN movies m ON s.movie_id = m.id
            JOIN rooms r ON s.room_id = r.id
            WHERE s.movie_id = :movieId 
            AND s.show_date = :date
            AND s.is_active = TRUE
            ORDER BY s.show_time
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("movieId", movieId)
                        .bind("date", date)
                        .map(new ShowtimeMapper())
                        .list()
        );
    }

    // Tạo showtime mới
    public Integer createShowtime(int movieId, int roomId, LocalDate showDate, LocalTime showTime) {
        // Kiểm tra xem đã tồn tại chưa
        Showtime existing = findShowtime(movieId, roomId, LocalDateTime.of(showDate, showTime));
        if (existing != null) {
            return existing.getId();
        }

        // Kiểm tra phòng có sẵn không
        if (!isRoomAvailable(roomId, showDate, showTime)) {
            return null;
        }

        String sql = """
            INSERT INTO showtimes (movie_id, room_id, show_date, show_time, is_active)
            VALUES (:movieId, :roomId, :showDate, :showTime, TRUE)
            """;

        try {
            return get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("movieId", movieId)
                            .bind("roomId", roomId)
                            .bind("showDate", showDate)
                            .bind("showTime", showTime)
                            .executeAndReturnGeneratedKeys("id")
                            .mapTo(Integer.class)
                            .one()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}