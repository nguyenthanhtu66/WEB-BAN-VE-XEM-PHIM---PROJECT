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

        System.out.println("DEBUG: statusParam = " + statusParam);
        System.out.println("DEBUG: searchKeyword = " + searchKeyword);

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
        }

        // Hiển thị trong console để debug
        for (Movie m : movies) {
            System.out.println("Movie: " + m.getName() + " - Status: " + m.getStatus());
        }

        // Đặt attributes
        request.setAttribute("movies", movies);
        request.setAttribute("status", statusParam);

        // Để hiển thị trong JSP
        String displayStatus = "dang_chieu";
        if (statusParam != null) {
            displayStatus = statusParam.replace("+", "_").toLowerCase();
        }
        request.setAttribute("currentStatus", displayStatus);

        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}