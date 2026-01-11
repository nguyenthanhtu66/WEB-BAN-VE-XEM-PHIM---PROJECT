package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.Movie;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MovieDAO extends BaseDao {

    // RowMapper for Movie
    public static class MovieMapper implements RowMapper<Movie> {
        @Override
        public Movie map(ResultSet rs, StatementContext ctx) throws SQLException {
            Movie movie = new Movie();
            movie.setMovieId(rs.getInt("movie_id"));
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
            movie.setReleaseDate(rs.getDate("release_date"));
            movie.setStatus(rs.getString("status"));
            return movie;
        }
    }

    // Get all showing movies
    public List<Movie> getAllShowingMovies() {
        String sql = "SELECT * FROM movies WHERE status = 'showing' ORDER BY release_date DESC";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new MovieMapper())
                        .list()
        );
    }

    // Get showing movies with pagination
    public List<Movie> getShowingMovies(int limit, int offset) {
        String sql = "SELECT * FROM movies WHERE status = 'showing' ORDER BY release_date DESC LIMIT ? OFFSET ?";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind(0, limit)
                        .bind(1, offset)
                        .map(new MovieMapper())
                        .list()
        );
    }

    // Get movie by ID
    public Movie getMovieById(int movieId) {
        String sql = "SELECT * FROM movies WHERE movie_id = ?";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind(0, movieId)
                        .map(new MovieMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Search movies
    public List<Movie> searchMovies(String keyword) {
        String sql = "SELECT * FROM movies WHERE (title LIKE ? OR genre LIKE ? OR cast LIKE ?) AND status = 'showing'";
        String searchPattern = "%" + keyword + "%";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind(0, searchPattern)
                        .bind(1, searchPattern)
                        .bind(2, searchPattern)
                        .map(new MovieMapper())
                        .list()
        );
    }

    // Get movies by genre
    public List<Movie> getMoviesByGenre(String genre) {
        String sql = "SELECT * FROM movies WHERE genre LIKE ? AND status = 'showing'";
        String genrePattern = "%" + genre + "%";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind(0, genrePattern)
                        .map(new MovieMapper())
                        .list()
        );
    }

    // Get total showing movies count
    public int countShowingMovies() {
        String sql = "SELECT COUNT(*) FROM movies WHERE status = 'showing'";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .mapTo(Integer.class)
                        .one()
        );
    }
}