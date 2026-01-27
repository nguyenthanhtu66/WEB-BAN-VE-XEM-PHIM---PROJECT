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

    private final MovieService movieService = new MovieService();
    private final int PAGE_SIZE = 12; // 12 phim mỗi trang

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();
        if (servletPath != null && isStaticResource(servletPath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        System.out.println("====== TRANG DANH SÁCH PHIM ======");
        System.out.println("Servlet Path: " + servletPath);
        System.out.println("Query String: " + request.getQueryString());

        try {
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
                String status = normalizeStatusParam(statusParam);
                System.out.println("DEBUG: Status to filter = " + status);

                // Phân trang
                if (pageStr != null && !pageStr.isEmpty()) {
                    try {
                        page = Integer.parseInt(pageStr);
                        if (page < 1) page = 1;
                    } catch (NumberFormatException e) {
                        page = 1;
                    }
                }

                // Tính tổng số trang
                int totalMovies = movieService.countMoviesByStatus(status);
                totalPages = (int) Math.ceil((double) totalMovies / PAGE_SIZE);
                if (totalPages < 1) totalPages = 1;
                if (page > totalPages) page = totalPages;

                // Lấy phim theo trang
                movies = movieService.getMoviesWithPagination(status, page, PAGE_SIZE);
                System.out.println("DEBUG: Found " + movies.size() + " movies with status: " + status);

                // Áp dụng filters nếu có
                if (genre != null && !genre.isEmpty()) {
                    movies = movieService.getMoviesByGenreAndStatus(genre, status);
                }
            }

            // Đặt attributes
            request.setAttribute("movies", movies);
            request.setAttribute("status", statusParam);
            request.setAttribute("genre", genre);
            request.setAttribute("duration", duration);
            request.setAttribute("age", age);
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);

            // Xác định currentStatus để highlight tab
            String currentStatus = getCurrentStatus(statusParam);
            request.setAttribute("currentStatus", currentStatus);

            // Thêm statusParam để sử dụng trong JSP
            String displayStatusParam = statusParam != null ? statusParam : "Dang+chieu";
            request.setAttribute("statusParam", displayStatusParam);

            request.getRequestDispatcher("/Phim-chieu.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi hệ thống: " + e.getMessage());
        }
    }

    // Kiểm tra xem có phải là file tĩnh không
    private boolean isStaticResource(String path) {
        return path.startsWith("/css/") || path.startsWith("/image/") ||
                path.startsWith("/img/") || path.endsWith(".css") ||
                path.endsWith(".png") || path.endsWith(".jpg") ||
                path.endsWith(".jpeg");
    }

    // Chuẩn hóa status parameter
    private String normalizeStatusParam(String statusParam) {
        if (statusParam == null || statusParam.trim().isEmpty()) {
            return "Dang+chieu";
        }

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