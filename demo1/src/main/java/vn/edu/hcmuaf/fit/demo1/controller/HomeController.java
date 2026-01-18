package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.hcmuaf.fit.demo1.model.Movie;
import vn.edu.hcmuaf.fit.demo1.service.MovieService;

import java.io.IOException;
import java.util.List;

// QUAN TRỌNG: Thêm urlPatterns = {"/home", "/", "/index.jsp"}
@WebServlet(name = "HomeController", urlPatterns = {"/home", "/", "/index.jsp"})
public class HomeController extends HttpServlet {

    private MovieService movieService = new MovieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("====== TRANG CHỦ ĐƯỢC GỌI ======");
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Servlet Path: " + request.getServletPath());

        // Lấy tham số từ URL
        String statusParam = request.getParameter("status");
        String searchKeyword = request.getParameter("search");

        List<Movie> movies;

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            // Tìm kiếm phim
            movies = movieService.searchMovies(searchKeyword.trim());
            request.setAttribute("searchKeyword", searchKeyword.trim());
        } else {
            // Lấy phim theo trạng thái
            String status = (statusParam == null || statusParam.trim().isEmpty())
                    ? "dang_chieu"
                    : statusParam.replace("+", "_").toLowerCase();

            movies = movieService.getMoviesByStatus(status);
        }

        // Hiển thị tối đa 8 phim trên trang chủ
        if (movies.size() > 8) {
            movies = movies.subList(0, 8);
        }

        // Debug: Hiển thị số phim
        System.out.println("Số phim: " + movies.size());

        // Gửi dữ liệu đến JSP
        request.setAttribute("movies", movies);
        String currentStatus = (statusParam != null)
                ? statusParam.replace("+", "_").toLowerCase()
                : "dang_chieu";
        request.setAttribute("currentStatus", currentStatus);

        // Chuyển đến trang JSP
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}