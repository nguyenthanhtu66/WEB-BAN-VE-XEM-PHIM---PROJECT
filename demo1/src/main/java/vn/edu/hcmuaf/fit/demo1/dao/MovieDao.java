package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.Movie;
import java.util.*;

public class MovieDao {

    private static final List<Movie> movies = new ArrayList<>();

    static {
        // PHIM ĐANG CHIẾU
        movies.add(new Movie(1, "Avatar 2", "images/avatar2.jpg",
                "Khoa học viễn tưởng, Phiêu lưu", 190, 8.5, "dang_chieu", "P"));

        movies.add(new Movie(2, "Fast & Furious 10", "images/fast10.jpg",
                "Hành động, Tội phạm, Giật gân", 150, 7.9, "dang_chieu", "T13"));

        movies.add(new Movie(3, "Kung Fu Panda 4", "images/kungfu4.jpg",
                "Hoạt hình, Gia đình, Phiêu lưu", 100, 7.6, "dang_chieu", "P"));

        movies.add(new Movie(4, "Spider-Man: No Way Home", "images/spiderman.jpg",
                "Hành động, Phiêu lưu, Khoa học viễn tưởng", 148, 8.4, "dang_chieu", "P"));

        movies.add(new Movie(5, "The Batman", "images/batman.jpg",
                "Hành động, Tội phạm, Bí ẩn", 176, 7.8, "dang_chieu", "T13"));

        movies.add(new Movie(6, "Top Gun: Maverick", "images/topgun.jpg",
                "Hành động, Chính kịch", 130, 8.2, "dang_chieu", "T13"));

        movies.add(new Movie(7, "Oppenheimer", "images/oppenheimer.jpg",
                "Tiểu sử, Lịch sử, Chính kịch", 180, 8.4, "dang_chieu", "T16"));

        movies.add(new Movie(8, "Barbie", "images/barbie.jpg",
                "Hài, Phiêu lưu, Giả tưởng", 114, 7.1, "dang_chieu", "P"));

        // PHIM SẮP CHIẾU (THÊM MỚI ĐỂ TEST)
        movies.add(new Movie(9, "Dune Part 2", "images/dune2.jpg",
                "Khoa học viễn tưởng, Phiêu lưu", 165, 8.8, "sap_chieu", "T13"));

        movies.add(new Movie(10, "Deadpool 3", "images/deadpool3.jpg",
                "Hành động, Hài, Khoa học viễn tưởng", 130, 8.3, "sap_chieu", "T18"));

        movies.add(new Movie(11, "Black Panther: Wakanda Forever", "images/blackpanther.jpg",
                "Hành động, Phiêu lưu, Khoa học viễn tưởng", 161, 7.2, "sap_chieu", "T13"));

        movies.add(new Movie(12, "Avatar 3", "images/avatar3.jpg",
                "Khoa học viễn tưởng, Phiêu lưu", 180, 0.0, "sap_chieu", "P"));

        movies.add(new Movie(13, "Transformers: Rise of the Beasts", "images/transformers.jpg",
                "Hành động, Khoa học viễn tưởng", 127, 0.0, "sap_chieu", "T13"));

        movies.add(new Movie(14, "The Little Mermaid", "images/mermaid.jpg",
                "Hoạt hình, Gia đình, Phiêu lưu", 135, 0.0, "sap_chieu", "P"));

        movies.add(new Movie(15, "Mission: Impossible 8", "images/mission8.jpg",
                "Hành động, Giật gân", 155, 0.0, "sap_chieu", "T13"));

        movies.add(new Movie(16, "Indiana Jones 5", "images/indiana.jpg",
                "Phiêu lưu, Hành động", 142, 0.0, "sap_chieu", "T13"));
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