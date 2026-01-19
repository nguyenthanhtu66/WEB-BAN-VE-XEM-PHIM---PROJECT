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

@WebServlet(name = "HomeController", urlPatterns = {"/home"}) // CHỈ CÓ /home, KHÔNG CÓ /
public class HomeController extends HttpServlet {

    private MovieService movieService = new MovieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("====== TRANG CHỦ ĐƯỢC GỌI ======");
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Servlet Path: " + request.getServletPath());

        // KIỂM TRA NẾU LÀ REQUEST CHO FILE TĨNH (CSS, IMAGE, JS) -> KHÔNG XỬ LÝ
        String path = request.getServletPath();
        if (path.startsWith("/css/") || path.startsWith("/image/") || path.startsWith("/img/") ||
                path.startsWith("/js/") || path.endsWith(".css") || path.endsWith(".js") ||
                path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg")) {

            System.out.println("Bỏ qua file tĩnh: " + path);
            // Cho phép container xử lý file tĩnh
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

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
                status = statusParam;
            }

            System.out.println("Lấy phim với status: " + status);
            // Lấy 8 phim cho trang chủ
            movies = movieService.getMoviesByStatusForHome(status);
        }

        System.out.println("Số phim hiển thị: " + (movies != null ? movies.size() : 0));

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
        System.out.println("Forwarding to index.jsp...");
        request.getRequestDispatcher("/index.jsp").forward(request, response);
        System.out.println("Forward completed.");
    }
}