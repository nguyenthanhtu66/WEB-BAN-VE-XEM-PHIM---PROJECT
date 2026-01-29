package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.MovieDao;
import vn.edu.hcmuaf.fit.demo1.model.Movie;

import java.util.List;

public class MovieService {

    private final MovieDao movieDao = new MovieDao();

    // ==================== PHƯƠNG THỨC CƠ BẢN ====================

    public List<Movie> getAllMovies() {
        return movieDao.getAllMovies();
    }

    // Lấy phim theo status URL (Dang+chieu, Sap+chieu)
    public List<Movie> getMoviesByStatus(String urlStatus) {
        String dbStatus = MovieDao.convertUrlStatusToDbStatus(urlStatus);
        return movieDao.getMoviesByStatus(dbStatus);
    }

    // Lấy phim theo status URL với giới hạn (cho trang chủ)
    public List<Movie> getMoviesByStatusForHome(String urlStatus) {
        String dbStatus = MovieDao.convertUrlStatusToDbStatus(urlStatus);
        return movieDao.getMoviesByStatusWithLimit(dbStatus, 8);
    }

    // Tìm kiếm phim
    public List<Movie> searchMovies(String keyword) {
        return movieDao.searchMovies(keyword);
    }

    // Lấy phim theo ID
    public Movie getMovieById(int id) {
        return movieDao.getMovieById(id);
    }

    // Phân trang phim
    public List<Movie> getMoviesWithPagination(String urlStatus, int page, int pageSize) {
        String dbStatus = MovieDao.convertUrlStatusToDbStatus(urlStatus);
        return movieDao.getMoviesWithPagination(dbStatus, page, pageSize);
    }

    // Đếm tổng số phim theo status URL
    public int countMoviesByStatus(String urlStatus) {
        String dbStatus = MovieDao.convertUrlStatusToDbStatus(urlStatus);
        return movieDao.countMoviesByStatus(dbStatus);
    }

    // Lấy phim theo thể loại và status
    public List<Movie> getMoviesByGenreAndStatus(String genre, String urlStatus) {
        String dbStatus = MovieDao.convertUrlStatusToDbStatus(urlStatus);
        return movieDao.getMoviesByGenreAndStatus(genre, dbStatus);
    }
    // Thêm phương thức filter theo thời lượng (nếu dùng SQL)
    public List<Movie> getMoviesByDurationAndStatus(String durationFilter, String urlStatus) {
        String dbStatus = MovieDao.convertUrlStatusToDbStatus(urlStatus);
        return movieDao.getMoviesByDurationAndStatus(durationFilter, dbStatus);
    }

    // Thêm phương thức filter theo độ tuổi (nếu dùng SQL)
    public List<Movie> getMoviesByAgeRatingAndStatus(String ageRating, String urlStatus) {
        String dbStatus = MovieDao.convertUrlStatusToDbStatus(urlStatus);
        return movieDao.getMoviesByAgeRatingAndStatus(ageRating, dbStatus);
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

    // ==================== HELPER METHODS ====================

    // Chuyển đổi URL status sang database status
    public String convertToDbStatus(String urlStatus) {
        return MovieDao.convertUrlStatusToDbStatus(urlStatus);
    }

    // Chuyển đổi database status sang URL status
    public String convertToUrlStatus(String dbStatus) {
        return MovieDao.convertDbStatusToUrlStatus(dbStatus);
    }

}