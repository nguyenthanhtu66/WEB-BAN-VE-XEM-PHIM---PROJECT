package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import vn.edu.hcmuaf.fit.demo1.model.Movie;
import vn.edu.hcmuaf.fit.demo1.service.MovieService;

import java.io.IOException;
import java.util.List;

@WebServlet("/list-product")
public class ListProduct extends HttpServlet {

    private MovieService movieService = new MovieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy parameter từ URL
        String statusParam = request.getParameter("status");
        String searchKeyword = request.getParameter("search");
        String genre = request.getParameter("genre");
        String duration = request.getParameter("duration");
        String age = request.getParameter("age");
        String pageStr = request.getParameter("page");

        System.out.println("DEBUG: statusParam = " + statusParam);
        System.out.println("DEBUG: searchKeyword = " + searchKeyword);
        System.out.println("DEBUG: genre = " + genre);
        System.out.println("DEBUG: duration = " + duration);

        List<Movie> movies;

        // Xử lý tìm kiếm
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            movies = movieService.searchMovies(searchKeyword.trim());
            request.setAttribute("searchKeyword", searchKeyword.trim());
            System.out.println("DEBUG: Search mode, found " + movies.size() + " movies");
        } else {
            // Xử lý status
            String status;
            if (statusParam == null || statusParam.trim().isEmpty()) {
                status = "dang_chieu"; // Mặc định
            } else {
                // Chuyển đổi từ URL encoding (Dang+chieu -> dang_chieu)
                status = statusParam.replace("+", "_").toLowerCase();
            }

            System.out.println("DEBUG: Status to filter = " + status);
            movies = movieService.getMoviesByStatus(status);
            System.out.println("DEBUG: Found " + movies.size() + " movies with status: " + status);

            // Áp dụng filters
            if (genre != null && !genre.isEmpty()) {
                movies = filterByGenre(movies, genre);
                System.out.println("DEBUG: After genre filter: " + movies.size() + " movies");
            }

            if (duration != null && !duration.isEmpty()) {
                movies = filterByDuration(movies, duration);
                System.out.println("DEBUG: After duration filter: " + movies.size() + " movies");
            }
        }

        // Phân trang
        int page = 1;
        int pageSize = 12; // 12 phim mỗi trang
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        int totalMovies = movies.size();
        int totalPages = (int) Math.ceil((double) totalMovies / pageSize);

        // Lấy sublist cho trang hiện tại
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalMovies);
        if (fromIndex < totalMovies) {
            movies = movies.subList(fromIndex, toIndex);
        } else {
            movies = List.of();
        }

        // Hiển thị trong console để debug
        System.out.println("DEBUG: Displaying " + movies.size() + " movies on page " + page);
        for (Movie m : movies) {
            System.out.println("Movie: " + m.getName() + " - Status: " + m.getStatus());
        }

        // Đặt attributes
        request.setAttribute("movies", movies);
        request.setAttribute("status", statusParam);
        request.setAttribute("genre", genre);
        request.setAttribute("duration", duration);
        request.setAttribute("age", age);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);

        // Để hiển thị trong JSP
        String displayStatus = "dang_chieu";
        if (statusParam != null) {
            displayStatus = statusParam.replace("+", "_").toLowerCase();
        }
        request.setAttribute("currentStatus", displayStatus);

        request.getRequestDispatcher("Phim-chieu.jsp").forward(request, response);
    }

    // Filter theo thể loại
    private List<Movie> filterByGenre(List<Movie> movies, String genre) {
        return movies.stream()
                .filter(movie -> movie.getCategory() != null &&
                        movie.getCategory().contains(genre))
                .toList();
    }

    // Filter theo thời lượng
    private List<Movie> filterByDuration(List<Movie> movies, String duration) {
        return movies.stream()
                .filter(movie -> {
                    int movieDuration = movie.getDuration();
                    switch (duration) {
                        case "short":
                            return movieDuration < 90;
                        case "medium":
                            return movieDuration >= 90 && movieDuration <= 120;
                        case "long":
                            return movieDuration > 120 && movieDuration <= 150;
                        case "very_long":
                            return movieDuration > 150;
                        default:
                            return true;
                    }
                })
                .toList();
    }
}