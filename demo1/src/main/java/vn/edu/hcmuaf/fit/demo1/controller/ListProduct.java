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
    private final int PAGE_SIZE = 12; // 12 phim mỗi trang

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

        List<Movie> movies;
        int page = 1;
        int totalPages = 1;

        // Xử lý tìm kiếm
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            movies = movieService.searchMovies(searchKeyword.trim());
            request.setAttribute("searchKeyword", searchKeyword.trim());
            System.out.println("DEBUG: Search mode, found " + movies.size() + " movies");
            totalPages = (int) Math.ceil((double) movies.size() / PAGE_SIZE);
        } else {
            // Xử lý status
            String status;
            if (statusParam == null || statusParam.trim().isEmpty()) {
                status = "dang_chieu"; // Mặc định
            } else {
                status = statusParam;
            }

            System.out.println("DEBUG: Status to filter = " + status);

            // Phân trang
            if (pageStr != null && !pageStr.isEmpty()) {
                try {
                    page = Integer.parseInt(pageStr);
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }

            // Tính tổng số trang
            int totalMovies = movieService.countMoviesByStatus(status);
            totalPages = (int) Math.ceil((double) totalMovies / PAGE_SIZE);

            // Lấy phim theo trang
            movies = movieService.getMoviesWithPagination(status, page, PAGE_SIZE);
            System.out.println("DEBUG: Found " + movies.size() + " movies with status: " + status);
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
}