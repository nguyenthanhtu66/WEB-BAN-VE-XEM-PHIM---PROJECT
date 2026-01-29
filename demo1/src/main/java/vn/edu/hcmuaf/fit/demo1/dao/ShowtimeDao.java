package vn.edu.hcmuaf.fit.demo1.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import vn.edu.hcmuaf.fit.demo1.model.Showtime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ShowtimeDao extends BaseDao {

    private static class ShowtimeMapper implements RowMapper<Showtime> {
        @Override
        public Showtime map(ResultSet rs, StatementContext ctx) throws SQLException {
            Showtime showtime = new Showtime();
            showtime.setId(rs.getInt("id"));
            showtime.setMovieId(rs.getInt("movie_id"));
            showtime.setRoomId(rs.getInt("room_id"));

            // Xử lý LocalDate - đảm bảo lấy đúng
            java.sql.Date sqlDate = rs.getDate("show_date");
            if (sqlDate != null) {
                showtime.setShowDate(sqlDate.toLocalDate());
            }

            // Xử lý LocalTime
            java.sql.Time sqlTime = rs.getTime("show_time");
            if (sqlTime != null) {
                showtime.setShowTime(sqlTime.toLocalTime());
            }

            showtime.setActive(rs.getBoolean("is_active"));

            // Xử lý các trường tùy chọn (từ JOIN)
            try {
                showtime.setMovieTitle(rs.getString("movie_title"));
            } catch (SQLException e) {
                // Không bắt buộc
            }

            try {
                showtime.setRoomName(rs.getString("room_name"));
            } catch (SQLException e) {
                // Không bắt buộc
            }

            return showtime;
        }
    }

    // Lấy tất cả suất chiếu đang hoạt động
    public List<Showtime> getAllActiveShowtimes() {
        String sql = """
            SELECT s.*, m.title as movie_title, r.room_name
            FROM showtimes s
            LEFT JOIN movies m ON s.movie_id = m.id
            LEFT JOIN rooms r ON s.room_id = r.id
            WHERE s.is_active = true
              AND s.show_date >= CURDATE()
            ORDER BY s.show_date, s.show_time
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new ShowtimeMapper())
                        .list()
        );
    }

    // Lấy suất chiếu theo movie và room
    public List<Showtime> getShowtimesByMovieAndRoom(int movieId, int roomId) {
        String sql = """
            SELECT s.*, m.title as movie_title, r.room_name
            FROM showtimes s
            LEFT JOIN movies m ON s.movie_id = m.id
            LEFT JOIN rooms r ON s.room_id = r.id
            WHERE s.movie_id = :movieId 
              AND s.room_id = :roomId
              AND s.is_active = true
              AND s.show_date >= CURDATE()
            ORDER BY s.show_date, s.show_time
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("movieId", movieId)
                        .bind("roomId", roomId)
                        .map(new ShowtimeMapper())
                        .list()
        );
    }

    // Lấy suất chiếu theo movie
    public List<Showtime> getShowtimesByMovieId(int movieId) {
        String sql = """
            SELECT s.*, m.title as movie_title, r.room_name
            FROM showtimes s
            LEFT JOIN movies m ON s.movie_id = m.id
            LEFT JOIN rooms r ON s.room_id = r.id
            WHERE s.movie_id = :movieId
              AND s.is_active = true
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

    // Lấy các ngày chiếu có sẵn cho movie và room - FIXED VERSION
    public List<LocalDate> getAvailableDatesByMovieAndRoom(int movieId, int roomId) {
        System.out.println("=== DEBUG: Getting dates for movie " + movieId + ", room " + roomId + " ===");

        // SQL đơn giản, chỉ lấy DATE
        String sql = """
        SELECT DISTINCT DATE(show_date) as show_date
        FROM showtimes
        WHERE movie_id = :movieId 
          AND room_id = :roomId
          AND is_active = true
          AND DATE(show_date) >= CURDATE()
        ORDER BY show_date
        """;

        try {
            List<LocalDate> dates = get().withHandle(handle -> {
                // Đầu tiên debug raw data
                System.out.println("Executing SQL with params: movieId=" + movieId + ", roomId=" + roomId);

                return handle.createQuery(sql)
                        .bind("movieId", movieId)
                        .bind("roomId", roomId)
                        .map((rs, ctx) -> {
                            try {
                                // CÁCH TỐT NHẤT: Lấy string và parse
                                String dateStr = rs.getString("show_date");
                                System.out.println("Raw date string from DB: '" + dateStr + "'");

                                if (dateStr == null || dateStr.trim().isEmpty()) {
                                    return null;
                                }

                                // Loại bỏ tất cả không phải số và dấu -
                                String cleaned = dateStr.replaceAll("[^0-9-]", "").trim();
                                System.out.println("Cleaned date: '" + cleaned + "'");

                                // Parse
                                if (cleaned.matches("\\d{4}-\\d{2}-\\d{2}")) {
                                    LocalDate date = LocalDate.parse(cleaned);
                                    System.out.println("Successfully parsed: " + date);
                                    return date;
                                } else {
                                    System.err.println("Invalid date format: " + cleaned);
                                    return null;
                                }
                            } catch (Exception e) {
                                System.err.println("Error parsing date: " + e.getMessage());
                                return null;
                            }
                        })
                        .filter(date -> date != null)
                        .list();
            });

            System.out.println("Returning " + dates.size() + " dates: " + dates);
            System.out.println("=== END DEBUG ===");
            return dates;

        } catch (Exception e) {
            System.err.println("Error in getAvailableDatesByMovieAndRoom: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    // Lấy các giờ chiếu có sẵn cho movie, room và date - FIXED VERSION
    public List<LocalTime> getAvailableTimesByMovieRoomAndDate(int movieId, int roomId, LocalDate showDate) {
        String sql = """
            SELECT show_time
            FROM showtimes
            WHERE movie_id = :movieId 
              AND room_id = :roomId
              AND show_date = :showDate
              AND is_active = true
            ORDER BY show_time
            """;

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("movieId", movieId)
                            .bind("roomId", roomId)
                            .bind("showDate", showDate)
                            .map((rs, ctx) -> {
                                try {
                                    // Cách 1: Lấy qua java.sql.Time
                                    java.sql.Time sqlTime = rs.getTime("show_time");
                                    if (sqlTime != null) {
                                        return sqlTime.toLocalTime();
                                    }

                                    // Cách 2: Nếu cách 1 không được, lấy string và parse
                                    String timeStr = rs.getString("show_time");
                                    if (timeStr != null && !timeStr.trim().isEmpty()) {
                                        return LocalTime.parse(timeStr);
                                    }
                                    return null;
                                } catch (Exception e) {
                                    System.err.println("Error parsing time from DB: " + e.getMessage());
                                    return null;
                                }
                            })
                            .filter(time -> time != null)
                            .list()
            );
        } catch (Exception e) {
            System.err.println("Error in getAvailableTimesByMovieRoomAndDate: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    // Lấy showtime theo ID
    public Showtime getShowtimeById(int id) {
        String sql = """
            SELECT s.*, m.title as movie_title, r.room_name
            FROM showtimes s
            LEFT JOIN movies m ON s.movie_id = m.id
            LEFT JOIN rooms r ON s.room_id = r.id
            WHERE s.id = :id
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("id", id)
                        .map(new ShowtimeMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    public Integer getShowtimeIdByDetails(int movieId, int roomId, LocalDate showDate, LocalTime showTime) {
        System.out.println("=== ShowtimeDao.getShowtimeIdByDetails ===");
        System.out.println("Params: movieId=" + movieId + ", roomId=" + roomId +
                ", showDate=" + showDate + ", showTime=" + showTime);

        String sql = """
        SELECT id
        FROM showtimes
        WHERE movie_id = :movieId 
          AND room_id = :roomId
          AND show_date = :showDate
          AND show_time = :showTime
          AND is_active = true
        LIMIT 1
        """;

        try {
            Integer showtimeId = get().withHandle(handle -> {
                System.out.println("Executing SQL...");
                return handle.createQuery(sql)
                        .bind("movieId", movieId)
                        .bind("roomId", roomId)
                        .bind("showDate", showDate)
                        .bind("showTime", showTime)
                        .mapTo(Integer.class)
                        .findOne()
                        .orElse(null);
            });

            System.out.println("Query result: " + showtimeId);
            System.out.println("=== END getShowtimeIdByDetails ===");

            return showtimeId;

        } catch (Exception e) {
            System.err.println("❌ Error in getShowtimeIdByDetails: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Kiểm tra showtime có tồn tại không
    public boolean isShowtimeValid(int showtimeId, int movieId, int roomId) {
        String sql = """
            SELECT COUNT(*) 
            FROM showtimes 
            WHERE id = :showtimeId 
              AND movie_id = :movieId 
              AND room_id = :roomId
              AND is_active = true
              AND show_date >= CURDATE()
            """;

        try {
            int count = get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("showtimeId", showtimeId)
                            .bind("movieId", movieId)
                            .bind("roomId", roomId)
                            .mapTo(Integer.class)
                            .one()
            );
            return count > 0;
        } catch (Exception e) {
            System.err.println("Error in isShowtimeValid: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Lấy showtimes cho phòng cụ thể
    public List<Showtime> getShowtimesByRoomId(int roomId) {
        String sql = """
            SELECT s.*, m.title as movie_title, r.room_name
            FROM showtimes s
            LEFT JOIN movies m ON s.movie_id = m.id
            LEFT JOIN rooms r ON s.room_id = r.id
            WHERE s.room_id = :roomId
              AND s.is_active = true
              AND s.show_date >= CURDATE()
            ORDER BY s.show_date, s.show_time
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("roomId", roomId)
                        .map(new ShowtimeMapper())
                        .list()
        );
    }

    // Lấy showtimes cho ngày cụ thể
    public List<Showtime> getShowtimesByDate(LocalDate date) {
        String sql = """
            SELECT s.*, m.title as movie_title, r.room_name
            FROM showtimes s
            LEFT JOIN movies m ON s.movie_id = m.id
            LEFT JOIN rooms r ON s.room_id = r.id
            WHERE s.show_date = :date
              AND s.is_active = true
            ORDER BY s.show_time
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("date", date)
                        .map(new ShowtimeMapper())
                        .list()
        );
    }

    // Kiểm tra xem có showtime nào trùng lịch không
    public boolean hasConflictingShowtime(int roomId, LocalDate showDate, LocalTime showTime, int excludeShowtimeId) {
        String sql = """
            SELECT COUNT(*) 
            FROM showtimes 
            WHERE room_id = :roomId 
              AND show_date = :showDate 
              AND show_time = :showTime
              AND id != :excludeShowtimeId
              AND is_active = true
            """;

        try {
            int count = get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("roomId", roomId)
                            .bind("showDate", showDate)
                            .bind("showTime", showTime)
                            .bind("excludeShowtimeId", excludeShowtimeId)
                            .mapTo(Integer.class)
                            .one()
            );
            return count > 0;
        } catch (Exception e) {
            System.err.println("Error in hasConflictingShowtime: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Thêm showtime mới
    public boolean addShowtime(Showtime showtime) {
        String sql = """
            INSERT INTO showtimes (movie_id, room_id, show_date, show_time, is_active)
            VALUES (:movieId, :roomId, :showDate, :showTime, :active)
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("movieId", showtime.getMovieId())
                            .bind("roomId", showtime.getRoomId())
                            .bind("showDate", showtime.getShowDate())
                            .bind("showTime", showtime.getShowTime())
                            .bind("active", showtime.isActive())
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            System.err.println("Error adding showtime: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật showtime
    public boolean updateShowtime(Showtime showtime) {
        String sql = """
            UPDATE showtimes SET
                movie_id = :movieId,
                room_id = :roomId,
                show_date = :showDate,
                show_time = :showTime,
                is_active = :active
            WHERE id = :id
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("movieId", showtime.getMovieId())
                            .bind("roomId", showtime.getRoomId())
                            .bind("showDate", showtime.getShowDate())
                            .bind("showTime", showtime.getShowTime())
                            .bind("active", showtime.isActive())
                            .bind("id", showtime.getId())
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            System.err.println("Error updating showtime: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Xóa showtime (soft delete)
    public boolean deleteShowtime(int id) {
        String sql = "UPDATE showtimes SET is_active = false WHERE id = :id";

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("id", id)
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            System.err.println("Error deleting showtime: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Helper method để format date
    public static String formatDateForDisplay(LocalDate date) {
        if (date == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    // Helper method để format time
    public static String formatTimeForDisplay(LocalTime time) {
        if (time == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

    // Debug method để xem dữ liệu trong DB
    public void debugShowtimeData() {
        String sql = """
            SELECT 
                id,
                movie_id,
                room_id,
                show_date,
                DATE_FORMAT(show_date, '%Y-%m-%d') as formatted_date,
                show_time,
                is_active
            FROM showtimes
            LIMIT 10
            """;

        try {
            get().withHandle(handle ->
                    handle.createQuery(sql)
                            .map((rs, ctx) -> {
                                System.out.println("Showtime ID: " + rs.getInt("id"));
                                System.out.println("  Movie ID: " + rs.getInt("movie_id"));
                                System.out.println("  Room ID: " + rs.getInt("room_id"));
                                System.out.println("  Raw Date: " + rs.getDate("show_date"));
                                System.out.println("  String Date: " + rs.getString("show_date"));
                                System.out.println("  Formatted Date: " + rs.getString("formatted_date"));
                                System.out.println("  Show Time: " + rs.getTime("show_time"));
                                System.out.println("  Active: " + rs.getBoolean("is_active"));
                                System.out.println("---");
                                return null;
                            })
                            .list()
            );
        } catch (Exception e) {
            System.err.println("Error in debugShowtimeData: " + e.getMessage());
            e.printStackTrace();
        }
    }

}