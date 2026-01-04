package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.model.Movie;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieService {
    // Danh sách phim mẫu
    private List<Movie> sampleMovies;

    public MovieService() {
        initSampleMovies();
    }

    private void initSampleMovies() {
        sampleMovies = new ArrayList<>();

        // Phim đang chiếu
        sampleMovies.add(new Movie(1, "Spider-Man: No Way Home",
                "Peter Parker nhờ Doctor Strange giúp thế giới quên anh là Người Nhện, nhưng phép thuật thất bại và mở ra đa vũ trụ.",
                "Hành động, Phiêu lưu", 149, 8.2,
                "https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg",
                "NOW_SHOWING"));

        sampleMovies.add(new Movie(2, "Avatar: The Way of Water",
                "Jake Sully và gia đình trên hành tinh Pandora, đối mặt với mối đe dọa mới từ loài người.",
                "Khoa học viễn tưởng, Phiêu lưu", 192, 7.6,
                "https://image.tmdb.org/t/p/w500/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg",
                "NOW_SHOWING"));

        sampleMovies.add(new Movie(3, "The Batman",
                "Batman điều tra một loạt vụ án liên quan đến Riddler, kẻ đe dọa thành phố Gotham.",
                "Hành động, Tội phạm, Bí ẩn", 176, 7.8,
                "https://image.tmdb.org/t/p/w500/74xTEgt7R36Fpooo50r9T25onhq.jpg",
                "NOW_SHOWING"));

        // Phim sắp chiếu
        sampleMovies.add(new Movie(4, "Oppenheimer",
                "Câu chuyện về J. Robert Oppenheimer, cha đẻ của bom nguyên tử.",
                "Tiểu sử, Lịch sử, Chính kịch", 180, 8.4,
                "https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg",
                "COMING_SOON"));

        sampleMovies.add(new Movie(5, "Barbie",
                "Barbie rời Barbieland để khám phá thế giới thực.",
                "Hài, Phiêu lưu, Giả tưởng", 114, 7.1,
                "https://image.tmdb.org/t/p/w500/iuFNMS8U5cb6xfzi51Dbkovj7vM.jpg",
                "COMING_SOON"));

        sampleMovies.add(new Movie(6, "Guardians of the Galaxy Vol. 3",
                "Đội Vệ binh Dải Ngân hà bảo vệ vũ trụ và khám phá bí mật về Rocket.",
                "Hành động, Khoa học viễn tưởng", 150, 8.0,
                "https://image.tmdb.org/t/p/w500/r2J02Z2OpNTctfOSN1Ydgii51I3.jpg",
                "COMING_SOON"));
    }

    // Lấy phim đang chiếu
    public List<Movie> getNowShowingMovies() {
        List<Movie> result = new ArrayList<>();
        for (Movie movie : sampleMovies) {
            if ("NOW_SHOWING".equals(movie.getStatus())) {
                result.add(movie);
            }
        }
        return result;
    }

    // Lấy phim sắp chiếu
    public List<Movie> getComingSoonMovies() {
        List<Movie> result = new ArrayList<>();
        for (Movie movie : sampleMovies) {
            if ("COMING_SOON".equals(movie.getStatus())) {
                result.add(movie);
            }
        }
        return result;
    }

    // Lấy phim theo ID
    public Movie getMovieById(int id) {
        for (Movie movie : sampleMovies) {
            if (movie.getId() == id) {
                return movie;
            }
        }
        return null;
    }

    // Tìm kiếm phim
    public List<Movie> searchMovies(String keyword) {
        List<Movie> result = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return getNowShowingMovies();
        }

        String searchTerm = keyword.toLowerCase().trim();

        for (Movie movie : sampleMovies) {
            if (movie.getTitle().toLowerCase().contains(searchTerm) ||
                    movie.getGenre().toLowerCase().contains(searchTerm) ||
                    (movie.getDescription() != null && movie.getDescription().toLowerCase().contains(searchTerm))) {
                result.add(movie);
            }
        }

        return result;
    }

    // Lấy banner images (dữ liệu mẫu)
    public List<String> getBannerImages() {
        List<String> banners = new ArrayList<>();
        banners.add("anh-slideshow-1.jpg");
        banners.add("anh-slideshow-2.jpg");
        banners.add("anh-slideshow-3.jpg");
        return banners;
    }
}