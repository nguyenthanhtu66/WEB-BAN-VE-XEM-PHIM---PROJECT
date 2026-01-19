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

@WebServlet(name = "HomeController", urlPatterns = {"/home"})
public class HomeController extends HttpServlet {

    private final MovieService movieService = new MovieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // KIỂM TRA NẾU LÀ REQUEST CHO FILE TĨNH -> KHÔNG XỬ LÝ
        String servletPath = request.getServletPath();
        if (servletPath != null && isStaticResource(servletPath)) {
            System.out.println("Bỏ qua file tĩnh: " + servletPath);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        System.out.println("====== TRANG CHỦ ĐƯỢC GỌI ======");
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Servlet Path: " + servletPath);
        System.out.println("Query String: " + request.getQueryString());

        try {
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
                    status = "Dang+chieu"; // Mặc định
                } else {
                    status = normalizeStatusParam(statusParam);
                }

                System.out.println("Lấy phim với status: " + status);
                // Lấy 8 phim cho trang chủ
                movies = movieService.getMoviesByStatusForHome(status);
            }

            System.out.println("Số phim hiển thị: " + (movies != null ? movies.size() : 0));

            // Gửi dữ liệu đến JSP
            request.setAttribute("movies", movies);

            // Xác định currentStatus để highlight tab
            String currentStatus = getCurrentStatus(statusParam);
            request.setAttribute("currentStatus", currentStatus);

            // Thêm statusParam để sử dụng trong JSP
            String displayStatusParam = statusParam != null ? statusParam : "Dang+chieu";
            request.setAttribute("statusParam", displayStatusParam);

            // Forward đến index.jsp
            System.out.println("Forwarding to index.jsp...");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            System.out.println("Forward completed.");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi hệ thống: " + e.getMessage());
        }
    }

    // Kiểm tra xem có phải là file tĩnh không
    private boolean isStaticResource(String path) {
        return path.startsWith("/css/") || path.startsWith("/image/") ||
                path.startsWith("/img/") || path.startsWith("/js/") ||
                path.endsWith(".css") || path.endsWith(".js") ||
                path.endsWith(".png") || path.endsWith(".jpg") ||
                path.endsWith(".jpeg") || path.endsWith(".gif") ||
                path.endsWith(".ico") || path.endsWith(".svg");
    }

    // Chuẩn hóa status parameter
    private String normalizeStatusParam(String statusParam) {
        if (statusParam == null) return "Dang+chieu";

        String lowerParam = statusParam.toLowerCase();
        if (lowerParam.contains("sap") || lowerParam.equals("sap+chieu") ||
                lowerParam.equals("sap_chieu") || lowerParam.equals("upcoming")) {
            return "Sap+chieu";
        } else {
            return "Dang+chieu";
        }
    }

    // Lấy currentStatus để highlight tab
    private String getCurrentStatus(String statusParam) {
        if (statusParam == null || statusParam.trim().isEmpty()) {
            return "dang_chieu";
        }

        String lowerParam = statusParam.toLowerCase();
        if (lowerParam.contains("sap") || lowerParam.equals("sap+chieu") ||
                lowerParam.equals("sap_chieu") || lowerParam.equals("upcoming")) {
            return "sap_chieu";
        } else {
            return "dang_chieu";
        }
    }
}