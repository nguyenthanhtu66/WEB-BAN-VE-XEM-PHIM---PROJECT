package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.model.Movie;
import java.util.ArrayList;
import java.util.List;

public class MovieService {

    public MovieService() {
        System.out.println("MovieService initialized with sample data");
    }

    public List<Movie> getHomepageMovies() {
        System.out.println("Returning sample movies for homepage");
        return getSampleMovies();
    }

    public int getTotalShowingMovies() {
        return getSampleMovies().size();
    }

    public Movie getMovieDetail(int movieId) {
        for (Movie movie : getSampleMovies()) {
            if (movie.getMovieId() == movieId) {
                return movie;
            }
        }
        return null;
    }

    public List<Movie> searchMovies(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getHomepageMovies();
        }

        String searchTerm = keyword.toLowerCase().trim();
        List<Movie> results = new ArrayList<>();

        for (Movie movie : getSampleMovies()) {
            if (movie.getTitle().toLowerCase().contains(searchTerm) ||
                    movie.getGenre().toLowerCase().contains(searchTerm)) {
                results.add(movie);
            }
        }

        return results;
    }

    public List<Movie> getMoviesByGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            return getHomepageMovies();
        }

        String genreTerm = genre.toLowerCase().trim();
        List<Movie> results = new ArrayList<>();

        for (Movie movie : getSampleMovies()) {
            if (movie.getGenre().toLowerCase().contains(genreTerm)) {
                results.add(movie);
            }
        }

        return results;
    }

    private List<Movie> getSampleMovies() {
        List<Movie> movies = new ArrayList<>();

        // Movie 1
        Movie movie1 = new Movie();
        movie1.setMovieId(1);
        movie1.setTitle("Spider-Man: No Way Home");
        movie1.setPosterUrl("https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg");
        movie1.setGenre("Hành động, Phiêu lưu");
        movie1.setDuration(149);
        movie1.setRating(8.2);
        movie1.setDescription("Peter Parker yêu cầu sự giúp đỡ của Doctor Strange để khôi phục bí mật của mình. Tuy nhiên, câu thần chú đã làm vỡ đa vũ trụ.");
        movie1.setDirector("Jon Watts");
        movie1.setCast("Tom Holland, Zendaya, Benedict Cumberbatch");
        movies.add(movie1);

        // Movie 2
        Movie movie2 = new Movie();
        movie2.setMovieId(2);
        movie2.setTitle("Avatar: The Way of Water");
        movie2.setPosterUrl("https://image.tmdb.org/t/p/w500/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg");
        movie2.setGenre("Khoa học viễn tưởng, Phiêu lưu");
        movie2.setDuration(192);
        movie2.setRating(7.6);
        movie2.setDescription("Jake Sully sống với gia đình mới trên hành tinh Pandora. Khi mối đe dọa quen thuộc quay trở lại, Jake phải hợp tác với Neytiri.");
        movie2.setDirector("James Cameron");
        movie2.setCast("Sam Worthington, Zoe Saldana, Sigourney Weaver");
        movies.add(movie2);

        // Movie 3
        Movie movie3 = new Movie();
        movie3.setMovieId(3);
        movie3.setTitle("The Batman");
        movie3.setPosterUrl("https://image.tmdb.org/t/p/w500/74xTEgt7R36Fpooo50r9T25onhq.jpg");
        movie3.setGenre("Hành động, Tội phạm, Bí ẩn");
        movie3.setDuration(176);
        movie3.setRating(7.8);
        movie3.setDescription("Sau hai năm săn lùng tội phạm trên đường phố Gotham, Batman phát hiện ra tham nhũng sâu rộng trong thành phố.");
        movie3.setDirector("Matt Reeves");
        movie3.setCast("Robert Pattinson, Zoë Kravitz, Paul Dano");
        movies.add(movie3);

        // Movie 4
        Movie movie4 = new Movie();
        movie4.setMovieId(4);
        movie4.setTitle("Top Gun: Maverick");
        movie4.setPosterUrl("https://upload.wikimedia.org/wikipedia/vi/1/1c/Top_Gun_Maverick_Poster_VN.jpg");
        movie4.setGenre("Hành động, Chính kịch");
        movie4.setDuration(131);
        movie4.setRating(8.3);
        movie4.setDescription("Sau hơn ba mươi năm phục vụ, Maverick đang ở vị trí cao nhất, né tránh những lần thăng chức khiến anh phải ở trên mặt đất.");
        movie4.setDirector("Joseph Kosinski");
        movie4.setCast("Tom Cruise, Miles Teller, Jennifer Connelly");
        movies.add(movie4);

        // Movie 5
        Movie movie5 = new Movie();
        movie5.setMovieId(5);
        movie5.setTitle("Oppenheimer");
        movie5.setPosterUrl("https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg");
        movie5.setGenre("Tiểu sử, Lịch sử, Chính kịch");
        movie5.setDuration(180);
        movie5.setRating(8.4);
        movie5.setDescription("Câu chuyện về nhà vật lý J. Robert Oppenheimer, người đóng vai trò quan trọng trong Dự án Manhattan.");
        movie5.setDirector("Christopher Nolan");
        movie5.setCast("Cillian Murphy, Emily Blunt, Matt Damon");
        movies.add(movie5);

        // Movie 6
        Movie movie6 = new Movie();
        movie6.setMovieId(6);
        movie6.setTitle("Barbie");
        movie6.setPosterUrl("https://image.tmdb.org/t/p/w500/iuFNMS8U5cb6xfzi51Dbkovj7vM.jpg");
        movie6.setGenre("Hài, Phiêu lưu, Giả tưởng");
        movie6.setDuration(114);
        movie6.setRating(7.1);
        movie6.setDescription("Barbie sống trong Barbieland, nơi mọi Barbie và Ken đều sống một cuộc sống hoàn hảo, cho đến khi một ngày Barbie bắt đầu nghi ngờ.");
        movie6.setDirector("Greta Gerwig");
        movie6.setCast("Margot Robbie, Ryan Gosling, America Ferrera");
        movies.add(movie6);

        // Movie 7
        Movie movie7 = new Movie();
        movie7.setMovieId(7);
        movie7.setTitle("Guardians of the Galaxy Vol. 3");
        movie7.setPosterUrl("https://image.tmdb.org/t/p/w500/r2J02Z2OpNTctfOSN1Ydgii51I3.jpg");
        movie7.setGenre("Hành động, Khoa học viễn tưởng");
        movie7.setDuration(150);
        movie7.setRating(8.0);
        movie7.setDescription("Nhóm Guardians bảo vệ vũ trụ và khám phá quá khứ của Rocket. Họ phải đối mặt với kẻ thù mới và thực hiện nhiệm vụ nguy hiểm.");
        movie7.setDirector("James Gunn");
        movie7.setCast("Chris Pratt, Zoe Saldana, Dave Bautista");
        movies.add(movie7);

        // Movie 8
        Movie movie8 = new Movie();
        movie8.setMovieId(8);
        movie8.setTitle("Dune");
        movie8.setPosterUrl("https://image.tmdb.org/t/p/original/d5NXSklXo0qyIYkgV94XAgMIckC.jpg");
        movie8.setGenre("Khoa học viễn tưởng, Phiêu lưu");
        movie8.setDuration(155);
        movie8.setRating(8.0);
        movie8.setDescription("Paul Atreides, một thiếu niên tài năng sinh ra trong một gia đình vĩ đại, du hành đến hành tinh Arrakis.");
        movie8.setDirector("Denis Villeneuve");
        movie8.setCast("Timothée Chalamet, Rebecca Ferguson, Oscar Isaac");
        movies.add(movie8);

        return movies;
    }

    // Thêm phương thức utility
    public String getMovieInfo(int movieId) {
        Movie movie = getMovieDetail(movieId);
        if (movie == null) {
            return "Phim không tồn tại";
        }
        return String.format("%s | %s | %d phút | ★ %.1f/10",
                movie.getTitle(), movie.getGenre(), movie.getDuration(), movie.getRating());
    }
}