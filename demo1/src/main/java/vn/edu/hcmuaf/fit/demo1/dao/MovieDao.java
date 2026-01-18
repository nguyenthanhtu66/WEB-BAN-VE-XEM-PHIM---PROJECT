package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.Movie;
import java.util.*;

public class MovieDao {

    private static final List<Movie> movies = new ArrayList<>();

    static {
        movies.add(new Movie(1, "Avatar 2", "images/avatar2.jpg",
                "Khoa học viễn tưởng", 190, 8.5, "dang_chieu"));

        movies.add(new Movie(2, "Fast & Furious 10", "images/fast10.jpg",
                "Hành động", 150, 7.9, "dang_chieu"));

        movies.add(new Movie(3, "Dune Part 2", "images/dune2.jpg",
                "Khoa học viễn tưởng", 165, 8.8, "sap_chieu"));

        movies.add(new Movie(4, "Deadpool 3", "images/deadpool3.jpg",
                "Hành động", 130, 8.3, "sap_chieu"));

        movies.add(new Movie(5, "Kung Fu Panda 4", "images/kungfu4.jpg",
                "Hoạt hình", 100, 7.6, "dang_chieu"));

        // Thêm thêm phim để có đủ 8 phim
        movies.add(new Movie(6, "Spider-Man: No Way Home", "images/spiderman.jpg",
                "Hành động", 148, 8.4, "dang_chieu"));

        movies.add(new Movie(7, "The Batman", "images/batman.jpg",
                "Hành động", 176, 7.8, "dang_chieu"));

        movies.add(new Movie(8, "Top Gun: Maverick", "images/topgun.jpg",
                "Hành động", 130, 8.2, "dang_chieu"));

        movies.add(new Movie(9, "Black Panther: Wakanda Forever", "images/blackpanther.jpg",
                "Hành động", 161, 7.2, "sap_chieu"));

        movies.add(new Movie(10, "Avatar 3", "images/avatar3.jpg",
                "Khoa học viễn tưởng", 180, 0.0, "sap_chieu"));
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public List<Movie> getMoviesByStatus(String status) {
        List<Movie> result = new ArrayList<>();
        for (Movie movie : movies) {
            if (movie.getStatus().equalsIgnoreCase(status)) {
                result.add(movie);
            }
        }
        return result;
    }

    // Phương thức tìm kiếm theo tên phim
    public List<Movie> searchMovies(String keyword) {
        List<Movie> result = new ArrayList<>();
        String searchLower = keyword.toLowerCase();

        for (Movie movie : movies) {
            if (movie.getName().toLowerCase().contains(searchLower)) {
                result.add(movie);
            }
        }
        return result;
    }
}