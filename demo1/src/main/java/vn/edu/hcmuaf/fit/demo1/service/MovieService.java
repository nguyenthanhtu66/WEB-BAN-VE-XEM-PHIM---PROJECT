package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.MovieDao;
import vn.edu.hcmuaf.fit.demo1.model.Movie;
import java.util.List;

public class MovieService {

    private MovieDao movieDao = new MovieDao();

    // ==================== PHƯƠNG THỨC CƠ BẢN ====================

    public List<Movie> getAllMovies() {
        return movieDao.getAllMovies();
    }

    public List<Movie> getMoviesByStatus(String status) {
        return movieDao.getMoviesByStatus(status);
    }

    public List<Movie> getMoviesByStatusForHome(String status) {
        return movieDao.getMoviesByStatusWithLimit(status, 8);
    }

    public List<Movie> searchMovies(String keyword) {
        return movieDao.searchMovies(keyword);
    }

    public Movie getMovieById(int id) {
        return movieDao.getMovieById(id);
    }

    public List<Movie> getMoviesWithPagination(String status, int page, int pageSize) {
        return movieDao.getMoviesWithPagination(status, page, pageSize);
    }

    public int countMoviesByStatus(String status) {
        return movieDao.countMoviesByStatus(status);
    }

    public List<Movie> getMoviesByGenreAndStatus(String genre, String status) {
        return movieDao.getMoviesByGenreAndStatus(genre, status);
    }

    // ==================== PHƯƠNG THỨC NÂNG CAO ====================

    public List<Movie> getLatestMovies(int limit) {
        return movieDao.getLatestMovies(limit);
    }

    public List<Movie> getTopRatedMovies(int limit) {
        return movieDao.getTopRatedMovies(limit);
    }

    public List<String> getAllGenres() {
        return movieDao.getAllGenres();
    }

    public int getTotalMovieCount() {
        return movieDao.getTotalMovieCount();
    }

    // ==================== PHƯƠNG THỨC ADMIN ====================

    public boolean addMovie(Movie movie) {
        return movieDao.addMovie(movie);
    }

    public boolean updateMovie(Movie movie) {
        return movieDao.updateMovie(movie);
    }

    public boolean deleteMovie(int id) {
        return movieDao.deleteMovie(id);
    }
}