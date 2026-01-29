package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.Movie;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MovieDao extends BaseDao {

    // RowMapper để map từ ResultSet sang Movie object (đầy đủ)
    private static class FullMovieMapper implements RowMapper<Movie> {
        @Override
        public Movie map(ResultSet rs, StatementContext ctx) throws SQLException {
            Movie movie = new Movie();
            movie.setId(rs.getInt("id"));
            movie.setTitle(rs.getString("title"));
            movie.setPosterUrl(rs.getString("poster_url"));
            movie.setSynopsis(rs.getString("synopsis"));
            movie.setDescription(rs.getString("description"));
            movie.setDirector(rs.getString("director"));
            movie.setCast(rs.getString("cast"));
            movie.setGenre(rs.getString("genre"));
            movie.setDuration(rs.getInt("duration"));
            movie.setCountry(rs.getString("country"));
            movie.setAgeRating(rs.getString("age_rating"));
            movie.setRating(rs.getDouble("rating"));

            // Xử lý release_date có thể null
            java.sql.Date releaseDate = rs.getDate("release_date");
            if (releaseDate != null) {
                movie.setReleaseDate(releaseDate.toLocalDate());
            }

            movie.setStatus(rs.getString("status"));
            return movie;
        }
    }

    // RowMapper cho các query đơn giản (chỉ lấy thông tin cơ bản)
    private static class BasicMovieMapper implements RowMapper<Movie> {
        @Override
        public Movie map(ResultSet rs, StatementContext ctx) throws SQLException {
            Movie movie = new Movie();
            movie.setId(rs.getInt("id"));  // SỬA: "movie_id" -> "id"
            movie.setTitle(rs.getString("title"));
            movie.setPosterUrl(rs.getString("poster_url"));
            movie.setGenre(rs.getString("genre"));
            movie.setDuration(rs.getInt("duration"));
            movie.setRating(rs.getDouble("rating"));
            movie.setStatus(rs.getString("status"));
            movie.setAgeRating(rs.getString("age_rating"));

            // Thêm các trường cơ bản khác nếu có trong query
            try {
                movie.setDirector(rs.getString("director"));
                movie.setCountry(rs.getString("country"));
                movie.setCast(rs.getString("cast"));
            } catch (SQLException e) {
                // Không bắt buộc
            }

            return movie;
        }
    }

    // ==================== QUERIES CƠ BẢN ====================

    // Lấy tất cả phim (đơn giản)
    public List<Movie> getAllMovies() {
        String sql = """
            SELECT 
                id,           -- SỬA: movie_id -> id
                title, 
                poster_url, 
                genre, 
                duration, 
                rating, 
                status, 
                age_rating,
                director,
                country,
                cast
            FROM movies 
            WHERE status IN ('showing', 'upcoming')
            ORDER BY 
                CASE status 
                    WHEN 'showing' THEN 1 
                    WHEN 'upcoming' THEN 2 
                    ELSE 3 
                END,
                release_date DESC,
                id DESC
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new BasicMovieMapper())
                        .list()
        );
    }

    // Lấy phim theo trạng thái database (showing, upcoming, ended)
    public List<Movie> getMoviesByStatus(String dbStatus) {
        String sql = """
            SELECT 
                id,           -- SỬA: movie_id -> id
                title, 
                poster_url, 
                genre, 
                duration, 
                rating, 
                status, 
                age_rating,
                director,
                country,
                cast
            FROM movies 
            WHERE status = :status
            ORDER BY release_date DESC, id DESC
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("status", dbStatus)
                        .map(new BasicMovieMapper())
                        .list()
        );
    }

    // Lấy phim theo trạng thái với giới hạn số lượng (cho trang chủ)
    public List<Movie> getMoviesByStatusWithLimit(String dbStatus, int limit) {
        String sql = """
            SELECT 
                id,           -- SỬA: movie_id -> id
                title, 
                poster_url, 
                genre, 
                duration, 
                rating, 
                status, 
                age_rating,
                director,
                country,
                cast
            FROM movies 
            WHERE status = :status
            ORDER BY release_date DESC, id DESC
            LIMIT :limit
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("status", dbStatus)
                        .bind("limit", limit)
                        .map(new BasicMovieMapper())
                        .list()
        );
    }

    // Tìm kiếm phim theo từ khóa
    public List<Movie> searchMovies(String keyword) {
        String sql = """
            SELECT 
                id,           -- SỬA: movie_id -> id
                title, 
                poster_url, 
                genre, 
                duration, 
                rating, 
                status, 
                age_rating,
                director,
                country,
                cast
            FROM movies 
            WHERE (title LIKE :keyword OR director LIKE :keyword OR cast LIKE :keyword OR genre LIKE :keyword)
                AND status IN ('showing', 'upcoming')
            ORDER BY 
                CASE WHEN status = 'showing' THEN 1 ELSE 2 END,
                release_date DESC,
                id DESC
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("keyword", "%" + keyword + "%")
                        .map(new BasicMovieMapper())
                        .list()
        );
    }

    // ==================== QUERIES CHI TIẾT ====================

    // Lấy phim theo ID (đầy đủ thông tin)
    public Movie getMovieById(int id) {
        String sql = """
            SELECT 
                id,           
                title, 
                poster_url, 
                synopsis,
                description,
                director,
                cast,
                genre, 
                duration,
                country,
                age_rating, 
                rating,
                release_date,
                status
            FROM movies 
            WHERE id = :id
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("id", id)
                        .map(new FullMovieMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Lấy phim theo thể loại và trạng thái
    public List<Movie> getMoviesByGenreAndStatus(String genre, String dbStatus) {
        String sql = """
            SELECT 
                id,           
                title, 
                poster_url, 
                genre, 
                duration, 
                rating, 
                status, 
                age_rating,
                director,
                country,
                cast
            FROM movies 
            WHERE status = :status
              AND genre LIKE :genre
            ORDER BY release_date DESC, id DESC
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("status", dbStatus)
                        .bind("genre", "%" + genre + "%")
                        .map(new BasicMovieMapper())
                        .list()
        );
    }

    // Phân trang phim
    public List<Movie> getMoviesWithPagination(String dbStatus, int page, int pageSize) {
        int offset = (page - 1) * pageSize;

        String sql = """
            SELECT 
                id,           
                title, 
                poster_url, 
                genre, 
                duration, 
                rating, 
                status, 
                age_rating,
                director,
                country,
                cast
            FROM movies 
            WHERE status = :status
            ORDER BY release_date DESC, id DESC
            LIMIT :pageSize OFFSET :offset
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("status", dbStatus)
                        .bind("pageSize", pageSize)
                        .bind("offset", offset)
                        .map(new BasicMovieMapper())
                        .list()
        );
    }

    // Đếm tổng số phim theo trạng thái
    public int countMoviesByStatus(String dbStatus) {
        String sql = "SELECT COUNT(*) FROM movies WHERE status = :status";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("status", dbStatus)
                        .mapTo(Integer.class)
                        .one()
        );
    }

    // Lấy phim mới nhất
    public List<Movie> getLatestMovies(int limit) {
        String sql = """
            SELECT 
                id,           
                title, 
                poster_url, 
                genre, 
                duration, 
                rating, 
                status, 
                age_rating,
                director,
                country,
                cast
            FROM movies 
            WHERE status IN ('showing', 'upcoming')
            ORDER BY release_date DESC, created_at DESC
            LIMIT :limit
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("limit", limit)
                        .map(new BasicMovieMapper())
                        .list()
        );
    }

    // Lấy phim có rating cao nhất
    public List<Movie> getTopRatedMovies(int limit) {
        String sql = """
            SELECT 
                id,          
                title, 
                poster_url, 
                genre, 
                duration, 
                rating, 
                status, 
                age_rating,
                director,
                country,
                cast
            FROM movies 
            WHERE status IN ('showing', 'upcoming') AND rating > 0
            ORDER BY rating DESC, release_date DESC
            LIMIT :limit
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("limit", limit)
                        .map(new BasicMovieMapper())
                        .list()
        );
    }

    // ==================== ADMIN QUERIES ====================

    // Thêm phim mới
    public boolean addMovie(Movie movie) {
        String sql = """
            INSERT INTO movies (
                title, poster_url, synopsis, description, 
                director, cast, genre, duration, country, 
                age_rating, rating, release_date, status, created_by
            ) VALUES (
                :title, :posterUrl, :synopsis, :description,
                :director, :cast, :genre, :duration, :country,
                :ageRating, :rating, :releaseDate, :status, :createdBy
            )
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bindBean(movie)
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật phim
    public boolean updateMovie(Movie movie) {
        String sql = """
            UPDATE movies SET
                title = :title,
                poster_url = :posterUrl,
                synopsis = :synopsis,
                description = :description,
                director = :director,
                cast = :cast,
                genre = :genre,
                duration = :duration,
                country = :country,
                age_rating = :ageRating,
                rating = :rating,
                release_date = :releaseDate,
                status = :status,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = :id
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bindBean(movie)
                            .bind("id", movie.getId())
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa phim (soft delete - cập nhật status)
    public boolean deleteMovie(int id) {
        String sql = "UPDATE movies SET status = 'ended' WHERE id = :id";

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("id", id)
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== HELPER METHODS ====================

    // Chuyển đổi status từ URL format sang database format
    public static String convertUrlStatusToDbStatus(String urlStatus) {
        if (urlStatus == null || urlStatus.trim().isEmpty()) {
            return "showing";
        }

        String lowerStatus = urlStatus.toLowerCase();
        if (lowerStatus.contains("sap") || lowerStatus.contains("upcoming") ||
                lowerStatus.equals("sap+chieu") || lowerStatus.equals("sap_chieu")) {
            return "upcoming";
        } else if (lowerStatus.contains("dang") || lowerStatus.contains("showing") ||
                lowerStatus.equals("dang+chieu") || lowerStatus.equals("dang_chieu")) {
            return "showing";
        } else {
            return "showing";
        }
    }

    // Chuyển đổi status từ database format sang URL format
    public static String convertDbStatusToUrlStatus(String dbStatus) {
        if (dbStatus == null || dbStatus.trim().isEmpty()) {
            return "Dang+chieu";
        }

        switch (dbStatus.toLowerCase()) {
            case "showing":
            case "đang chiếu":
            case "dang_chieu":
                return "Dang+chieu";
            case "upcoming":
            case "sắp chiếu":
            case "sap_chieu":
                return "Sap+chieu";
            case "ended":
            case "đã chiếu":
                return "Ended";
            default:
                return "Dang+chieu";
        }
    }

    // Lấy danh sách thể loại duy nhất
    public List<String> getAllGenres() {
        String sql = """
            SELECT DISTINCT TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(genre, ',', numbers.n), ',', -1)) as genre
            FROM movies
            CROSS JOIN (
                SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
            ) numbers
            WHERE CHAR_LENGTH(genre) - CHAR_LENGTH(REPLACE(genre, ',', '')) >= numbers.n - 1
            ORDER BY genre
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .mapTo(String.class)
                        .list()
        );
    }

    // Đếm tổng số phim
    public int getTotalMovieCount() {
        String sql = "SELECT COUNT(*) FROM movies WHERE status IN ('showing', 'upcoming')";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .mapTo(Integer.class)
                        .one()
        );
    }
    // Lấy phim đang chiếu
    public List<Movie> getShowingMovies() {
        String sql = """
        SELECT 
            id,           
            title, 
            poster_url, 
            genre, 
            duration, 
            rating, 
            status, 
            age_rating,
            director,
            country,
            cast
        FROM movies 
        WHERE status = 'showing'
        AND release_date <= CURDATE()
        ORDER BY release_date DESC, id DESC
        """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new BasicMovieMapper())
                        .list()
        );
    }

    // Lấy phim sắp chiếu
    public List<Movie> getUpcomingMovies() {
        String sql = """
        SELECT 
            id,           
            title, 
            poster_url, 
            genre, 
            duration, 
            rating, 
            status, 
            age_rating,
            director,
            country,
            cast
        FROM movies 
        WHERE status = 'upcoming'
        AND release_date > CURDATE()
        ORDER BY release_date ASC, id DESC
        """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new BasicMovieMapper())
                        .list()
        );
    }
    public List<Movie> getNowShowing() {
        String sql = """
        SELECT 
            id,           
            title, 
            poster_url, 
            genre, 
            duration, 
            rating, 
            status, 
            age_rating,
            director,
            country,
            cast
        FROM movies 
        WHERE status = 'showing'
        ORDER BY id DESC
        """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new BasicMovieMapper())
                        .list()
        );
    }

    // Thêm phương thức filter theo thời lượng (nếu muốn dùng SQL)
    public List<Movie> getMoviesByDurationAndStatus(String durationFilter, String dbStatus) {
        StringBuilder sql = new StringBuilder("""
            SELECT 
                id,           
                title, 
                poster_url, 
                genre, 
                duration, 
                rating, 
                status, 
                age_rating,
                director,
                country,
                cast
            FROM movies 
            WHERE status = :status
            """);

        // Thêm điều kiện duration
        if (durationFilter != null && !durationFilter.trim().isEmpty()) {
            switch (durationFilter.toLowerCase()) {
                case "short":
                    sql.append(" AND duration < 90");
                    break;
                case "medium":
                    sql.append(" AND duration BETWEEN 90 AND 120");
                    break;
                case "long":
                    sql.append(" AND duration BETWEEN 120 AND 150");
                    break;
                case "very_long":
                    sql.append(" AND duration > 150");
                    break;
            }
        }

        sql.append(" ORDER BY release_date DESC, id DESC");

        return get().withHandle(handle ->
                handle.createQuery(sql.toString())
                        .bind("status", dbStatus)
                        .map(new BasicMovieMapper())
                        .list()
        );
    }

    // Thêm phương thức filter theo độ tuổi (nếu muốn dùng SQL)
    public List<Movie> getMoviesByAgeRatingAndStatus(String ageRating, String dbStatus) {
        String sql = """
            SELECT 
                id,           
                title, 
                poster_url, 
                genre, 
                duration, 
                rating, 
                status, 
                age_rating,
                director,
                country,
                cast
            FROM movies 
            WHERE status = :status
              AND age_rating = :ageRating
            ORDER BY release_date DESC, id DESC
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("status", dbStatus)
                        .bind("ageRating", ageRating)
                        .map(new BasicMovieMapper())
                        .list()
        );
    }



    @Override
    public String toString() {
        return "MovieDao - Total movies in DB: " + getTotalMovieCount();
    }
}