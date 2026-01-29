package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import vn.edu.hcmuaf.fit.demo1.model.Movie;
import vn.edu.hcmuaf.fit.demo1.service.MovieService;

import java.io.IOException;
import java.util.ArrayList;
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
            System.out.println("DEBUG: genre = " + genre);
            System.out.println("DEBUG: duration = " + duration);
            System.out.println("DEBUG: age = " + age);

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

                // LẤY TẤT CẢ PHIM THEO STATUS TRƯỚC
                List<Movie> allMovies = movieService.getMoviesByStatus(status);
                System.out.println("DEBUG: Found " + allMovies.size() + " movies with status: " + status);

                // ÁP DỤNG FILTERS TUẦN TỰ
                List<Movie> filteredMovies = new ArrayList<>(allMovies);

                // Filter theo thể loại
                if (genre != null && !genre.trim().isEmpty()) {
                    filteredMovies = filterByGenre(filteredMovies, genre);
                    System.out.println("DEBUG: After genre filter: " + filteredMovies.size() + " movies");
                }

                // Filter theo thời lượng
                if (duration != null && !duration.trim().isEmpty()) {
                    filteredMovies = filterByDuration(filteredMovies, duration);
                    System.out.println("DEBUG: After duration filter: " + filteredMovies.size() + " movies");
                }

                // Filter theo độ tuổi
                if (age != null && !age.trim().isEmpty()) {
                    filteredMovies = filterByAgeRating(filteredMovies, age);
                    System.out.println("DEBUG: After age filter: " + filteredMovies.size() + " movies");
                }

                // Tính tổng số trang sau khi filter
                totalPages = (int) Math.ceil((double) filteredMovies.size() / PAGE_SIZE);
                if (totalPages < 1) totalPages = 1;
                if (page > totalPages) page = totalPages;

                // Lấy phim cho trang hiện tại (phân trang)
                int fromIndex = (page - 1) * PAGE_SIZE;
                int toIndex = Math.min(fromIndex + PAGE_SIZE, filteredMovies.size());

                if (fromIndex < filteredMovies.size()) {
                    movies = filteredMovies.subList(fromIndex, toIndex);
                } else {
                    movies = new ArrayList<>();
                }

                System.out.println("DEBUG: Pagination - page " + page + ", showing " + movies.size() + " movies");
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

    // ========== CÁC PHƯƠNG THỨC FILTER HELPER ==========

    private List<Movie> filterByGenre(List<Movie> movies, String genre) {
        List<Movie> filtered = new ArrayList<>();
        String genreLower = genre.toLowerCase().trim();

        for (Movie movie : movies) {
            if (movie.getGenre() != null &&
                    movie.getGenre().toLowerCase().contains(genreLower)) {
                filtered.add(movie);
            }
        }
        return filtered;
    }

    private List<Movie> filterByDuration(List<Movie> movies, String duration) {
        List<Movie> filtered = new ArrayList<>();
        String durationLower = duration.toLowerCase().trim();

        for (Movie movie : movies) {
            int movieDuration = movie.getDuration();

            switch (durationLower) {
                case "short":
                    if (movieDuration < 90) filtered.add(movie);
                    break;
                case "medium":
                    if (movieDuration >= 90 && movieDuration <= 120) filtered.add(movie);
                    break;
                case "long":
                    if (movieDuration > 120 && movieDuration <= 150) filtered.add(movie);
                    break;
                case "very_long":
                    if (movieDuration > 150) filtered.add(movie);
                    break;
            }
        }
        return filtered;
    }

    private List<Movie> filterByAgeRating(List<Movie> movies, String age) {
        List<Movie> filtered = new ArrayList<>();
        String ageUpper = age.trim().toUpperCase();

        for (Movie movie : movies) {
            String movieAgeRating = movie.getAgeRating();
            if (movieAgeRating != null && movieAgeRating.equalsIgnoreCase(ageUpper)) {
                filtered.add(movie);
            }
        }
        return filtered;
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