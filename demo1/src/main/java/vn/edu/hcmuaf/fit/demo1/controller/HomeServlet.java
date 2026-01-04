package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import vn.edu.hcmuaf.fit.demo1.model.Movie;
import vn.edu.hcmuaf.fit.demo1.service.MovieService;
import vn.edu.hcmuaf.fit.demo1.service.UserService;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "HomeServlet", value = {"/", "/home"})
public class HomeServlet extends HttpServlet {
    private MovieService movieService;

    @Override
    public void init() {
        this.movieService = new MovieService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Xử lý tìm kiếm
        String keyword = request.getParameter("keyword");
        if (keyword != null && !keyword.trim().isEmpty()) {
            handleSearch(request, response, keyword);
            return;
        }

        // Lấy loại phim từ parameter (mặc định: đang chiếu)
        String movieType = request.getParameter("type");

        List<Movie> movies;
        String currentMovieType;
        String movieTypeLabel;

        if ("coming-soon".equals(movieType)) {
            movies = movieService.getComingSoonMovies();
            currentMovieType = "coming-soon";
            movieTypeLabel = "PHIM SẮP CHIẾU";
        } else {
            movies = movieService.getNowShowingMovies();
            currentMovieType = "now-showing";
            movieTypeLabel = "PHIM ĐANG CHIẾU";
        }

        // Set dữ liệu cho JSP
        request.setAttribute("movies", movies);
        request.setAttribute("currentMovieType", currentMovieType);
        request.setAttribute("movieTypeLabel", movieTypeLabel);

        // Lấy banner images
        List<String> banners = movieService.getBannerImages();
        request.setAttribute("banners", banners);

        // Các thể loại phim cho filter
        String[] genres = {"Hành động", "Phiêu lưu", "Khoa học viễn tưởng", "Hài",
                "Chính kịch", "Kinh dị", "Tình cảm", "Hoạt hình"};
        request.setAttribute("genres", genres);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
    }

    private void handleSearch(HttpServletRequest request, HttpServletResponse response, String keyword)
            throws ServletException, IOException {

        // Tìm kiếm phim
        List<Movie> searchResults = movieService.searchMovies(keyword);

        request.setAttribute("movies", searchResults);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("currentMovieType", "search");
        request.setAttribute("movieTypeLabel", "KẾT QUẢ TÌM KIẾM: " + keyword);

        // Lấy banner images
        List<String> banners = movieService.getBannerImages();
        request.setAttribute("banners", banners);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
    }
}