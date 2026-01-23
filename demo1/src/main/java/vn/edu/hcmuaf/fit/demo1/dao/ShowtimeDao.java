package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.Showtime;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

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

            if (rs.getDate("show_date") != null) {
                showtime.setShowDate(rs.getDate("show_date").toLocalDate());
            }

            if (rs.getTime("show_time") != null) {
                showtime.setShowTime(rs.getTime("show_time").toLocalTime());
            }

            showtime.setActive(rs.getBoolean("is_active"));

            if (rs.getTimestamp("created_at") != null) {
                showtime.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }

            return showtime;
        }
    }

    // Lấy showtime theo ID
    public Showtime getShowtimeById(int id) {
        String sql = "SELECT * FROM showtimes WHERE id = :id AND is_active = TRUE";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("id", id)
                        .map(new ShowtimeMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Lấy tất cả showtime của một phim
    public List<Showtime> getShowtimesByMovie(int movieId) {
        String sql = """
            SELECT st.* FROM showtimes st
            WHERE st.movie_id = :movieId 
            AND st.is_active = TRUE
            AND st.show_date >= CURDATE()
            ORDER BY st.show_date, st.show_time
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("movieId", movieId)
                        .map(new ShowtimeMapper())
                        .list()
        );
    }

    // Lấy showtime theo phim và phòng
    public List<Showtime> getShowtimesByMovieAndRoom(int movieId, int roomId) {
        String sql = """
            SELECT st.* FROM showtimes st
            WHERE st.movie_id = :movieId 
            AND st.room_id = :roomId
            AND st.is_active = TRUE
            AND st.show_date >= CURDATE()
            ORDER BY st.show_date, st.show_time
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("movieId", movieId)
                        .bind("roomId", roomId)
                        .map(new ShowtimeMapper())
                        .list()
        );
    }

    // Tìm showtime theo thời gian cụ thể
    public Showtime findShowtime(int movieId, int roomId, LocalDateTime dateTime) {
        String sql = """
            SELECT * FROM showtimes 
            WHERE movie_id = :movieId 
            AND room_id = :roomId
            AND show_date = :showDate
            AND show_time = :showTime
            AND is_active = TRUE
            LIMIT 1
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("movieId", movieId)
                        .bind("roomId", roomId)
                        .bind("showDate", dateTime.toLocalDate())
                        .bind("showTime", dateTime.toLocalTime())
                        .map(new ShowtimeMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Tạo showtime mới
    public boolean createShowtime(Showtime showtime) {
        String sql = """
            INSERT INTO showtimes (
                movie_id, room_id, show_date, show_time, is_active
            ) VALUES (
                :movieId, :roomId, :showDate, :showTime, :isActive
            )
            """;

        return get().withHandle(handle -> {
            int rows = handle.createUpdate(sql)
                    .bind("movieId", showtime.getMovieId())
                    .bind("roomId", showtime.getRoomId())
                    .bind("showDate", showtime.getShowDate())
                    .bind("showTime", showtime.getShowTime())
                    .bind("isActive", showtime.isActive())
                    .execute();
            return rows > 0;
        });
    }

    // Cập nhật showtime
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

        return get().withHandle(handle -> {
            int rows = handle.createUpdate(sql)
                    .bind("movieId", showtime.getMovieId())
                    .bind("roomId", showtime.getRoomId())
                    .bind("showDate", showtime.getShowDate())
                    .bind("showTime", showtime.getShowTime())
                    .bind("isActive", showtime.isActive())
                    .bind("id", showtime.getId())
                    .execute();
            return rows > 0;
        });
    }

    // Xóa showtime (soft delete)
    public boolean deleteShowtime(int id) {
        String sql = "UPDATE showtimes SET is_active = FALSE WHERE id = :id";

        return get().withHandle(handle -> {
            int rows = handle.createUpdate(sql)
                    .bind("id", id)
                    .execute();
            return rows > 0;
        });
    }

    // Lấy showtime trong khoảng thời gian
    public List<Showtime> getShowtimesByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT st.* FROM showtimes st
            WHERE st.show_date BETWEEN :startDate AND :endDate
            AND st.is_active = TRUE
            ORDER BY st.show_date, st.show_time
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("startDate", startDate)
                        .bind("endDate", endDate)
                        .map(new ShowtimeMapper())
                        .list()
        );
    }

    // Lấy showtime cho hôm nay
    public List<Showtime> getTodayShowtimes() {
        String sql = """
            SELECT st.* FROM showtimes st
            WHERE st.show_date = CURDATE()
            AND st.is_active = TRUE
            AND st.show_time > CURTIME()
            ORDER BY st.show_time
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new ShowtimeMapper())
                        .list()
        );
    }

    // Lấy showtime cho ngày mai
    public List<Showtime> getTomorrowShowtimes() {
        String sql = """
            SELECT st.* FROM showtimes st
            WHERE st.show_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY)
            AND st.is_active = TRUE
            ORDER BY st.show_time
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new ShowtimeMapper())
                        .list()
        );
    }

    // Lấy showtime cho cuối tuần
    public List<Showtime> getWeekendShowtimes() {
        String sql = """
            SELECT st.* FROM showtimes st
            WHERE DAYOFWEEK(st.show_date) IN (1, 7) -- Chủ nhật (1) và Thứ 7 (7)
            AND st.show_date >= CURDATE()
            AND st.show_date < DATE_ADD(CURDATE(), INTERVAL 7 DAY)
            AND st.is_active = TRUE
            ORDER BY st.show_date, st.show_time
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new ShowtimeMapper())
                        .list()
        );
    }

    // Kiểm tra xem phòng có trống vào thời gian đó không
    public boolean isRoomAvailable(int roomId, LocalDate date, LocalTime time) {
        String sql = """
            SELECT COUNT(*) FROM showtimes
            WHERE room_id = :roomId
            AND show_date = :date
            AND show_time = :time
            AND is_active = TRUE
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomId", roomId)
                        .bind("date", date)
                        .bind("time", time)
                        .mapTo(Integer.class)
                        .one() == 0
        );
    }
}