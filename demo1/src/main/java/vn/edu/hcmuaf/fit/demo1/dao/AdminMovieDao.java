package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.Movie;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AdminMovieDao extends BaseDao {

    private static class AdminMovieMapper implements RowMapper<Movie> {
        @Override
        public Movie map(ResultSet rs, StatementContext ctx) throws SQLException {
            Movie m = new Movie();
            m.setId(rs.getInt("id"));
            m.setTitle(rs.getString("title"));
            m.setPosterUrl(rs.getString("poster_url"));
            m.setSynopsis(rs.getString("synopsis"));
            m.setDescription(rs.getString("description"));
            m.setDirector(rs.getString("director"));
            m.setCast(rs.getString("cast"));
            m.setGenre(rs.getString("genre"));
            m.setDuration(rs.getInt("duration"));
            m.setCountry(rs.getString("country"));
            m.setAgeRating(rs.getString("age_rating"));
            m.setRating(rs.getDouble("rating"));

            if (rs.getDate("release_date") != null) {
                m.setReleaseDate(rs.getDate("release_date").toLocalDate());
            }

            m.setStatus(rs.getString("status"));
            return m;
        }
    }

    /* ================= QUERY ================= */

    public List<Movie> getAllMoviesForAdmin() {
        String sql = """
                    SELECT
                        id,
                        title,
                        poster_url,
                        synopsis,
                        description,
                        director,
                        `cast`,
                        genre,
                        duration,
                        country,
                        age_rating,
                        rating,
                        release_date,
                        status
                    FROM movies
                   WHERE status <> 'ended'
                    ORDER BY id DESC
                """;

        return get().withHandle(h -> h.createQuery(sql)
                .map(new AdminMovieMapper())
                .list());
    }

    public Movie getMovieById(int id) {
        String sql = """
                    SELECT
                        id,
                        title,
                        poster_url,
                        synopsis,
                        description,
                        director,
                        `cast`,
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

        return get().withHandle(h -> h.createQuery(sql)
                .bind("id", id)
                .map(new AdminMovieMapper())
                .findOne()
                .orElse(null));
    }

    /* ================= COMMAND ================= */

    public boolean addMovie(Movie movie) {
        String sql = """
                    INSERT INTO movies (
                        title,
                        poster_url,
                        synopsis,
                        description,
                        director,
                        `cast`,
                        genre,
                        duration,
                        country,
                        age_rating,
                        rating,
                        release_date,
                        status
                    ) VALUES (
                        :title,
                        :posterUrl,
                        :synopsis,
                        :description,
                        :director,
                        :cast,
                        :genre,
                        :duration,
                        :country,
                        :ageRating,
                        :rating,
                        :releaseDate,
                        :status
                    )
                """;

        return get().withHandle(h -> h.createUpdate(sql)
                .bindBean(movie)
                .execute() > 0);
    }

    public boolean updateMovie(Movie movie) {
        String sql = """
                    UPDATE movies SET
                        title = :title,
                        poster_url = :posterUrl,
                        synopsis = :synopsis,
                        description = :description,
                        director = :director,
                        `cast` = :cast,
                        genre = :genre,
                        duration = :duration,
                        country = :country,
                        age_rating = :ageRating,
                        rating = :rating,
                        release_date = :releaseDate,
                        status = :status
                    WHERE id = :id
                """;

        return get().withHandle(h -> h.createUpdate(sql)
                .bindBean(movie)
                .execute() > 0);
    }

    public boolean hideMovie(int id) {
        return get().withHandle(h -> h.createUpdate("UPDATE movies SET status='ended' WHERE id=:id")
                .bind("id", id)
                .execute() > 0);
    }

}
