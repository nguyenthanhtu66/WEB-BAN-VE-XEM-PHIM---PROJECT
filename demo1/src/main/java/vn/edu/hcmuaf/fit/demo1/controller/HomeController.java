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

@WebServlet(name = "HomeController", urlPatterns = {"/home", "/"})
public class HomeController extends HttpServlet {

    private MovieService movieService = new MovieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("====== TRANG CHỦ ĐƯỢC GỌI ======");
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Servlet Path: " + request.getServletPath());
        System.out.println("Query String: " + request.getQueryString());

        // Đánh dấu request đã qua Servlet để tránh vòng lặp
        request.setAttribute("fromServlet", "true");

        // Lấy tham số từ URL
        String statusParam = request.getParameter("status");
        String searchKeyword = request.getParameter("search");

        List<Movie> movies;

        // Xử lý tìm kiếm
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            movies = movieService.searchMovies(searchKeyword.trim());
            request.setAttribute("searchKeyword", searchKeyword.trim());
            System.out.println("Tìm kiếm với từ khóa: " + searchKeyword);
        } else {
            // Xử lý trạng thái phim
            String status;
            if (statusParam == null || statusParam.trim().isEmpty()) {
                status = "dang_chieu"; // Mặc định hiển thị phim đang chiếu
            } else {
                status = statusParam.replace("+", "_").toLowerCase();
            }

            System.out.println("Lấy phim với status: " + status);
            movies = movieService.getMoviesByStatus(status);
        }

        // Giới hạn 8 phim cho trang chủ
        if (movies.size() > 8) {
            movies = movies.subList(0, 8);
        }

        System.out.println("Số phim hiển thị: " + movies.size());

        // Gửi dữ liệu đến JSP
        request.setAttribute("movies", movies);

        // Xác định currentStatus để highlight tab
        String currentStatus;
        if (statusParam == null || statusParam.trim().isEmpty()) {
            currentStatus = "dang_chieu";
        } else {
            currentStatus = statusParam.replace("+", "_").toLowerCase();
        }
        request.setAttribute("currentStatus", currentStatus);

        // Forward đến index.jsp
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}