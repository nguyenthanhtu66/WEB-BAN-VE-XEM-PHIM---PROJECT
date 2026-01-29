package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.model.Movie;
import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.model.Banner;
import vn.edu.hcmuaf.fit.demo1.service.MovieService;
import vn.edu.hcmuaf.fit.demo1.service.BannerService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "HomeController", urlPatterns = {"/home"})
public class HomeController extends HttpServlet {

    private final MovieService movieService = new MovieService();
    private final BannerService bannerService = new BannerService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();
        if (servletPath != null && isStaticResource(servletPath)) {
            System.out.println("Bỏ qua file tĩnh: " + servletPath);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        System.out.println("====== TRANG CHỦ ĐƯỢC GỌI ======");

        // Lấy user từ session - CHỈ KIỂM TRA loggedUser
        HttpSession session = request.getSession(false);
        User loggedUser = null;

        if (session != null) {
            loggedUser = (User) session.getAttribute("loggedUser");

            // Nếu không có loggedUser, kiểm tra user cũ
            if (loggedUser == null) {
                loggedUser = (User) session.getAttribute("user");
                if (loggedUser != null) {
                    // Migrate từ user cũ sang loggedUser
                    session.setAttribute("loggedUser", loggedUser);
                    session.removeAttribute("user");
                    System.out.println("🔄 Migrated user attribute to loggedUser");
                }
            }

            if (loggedUser != null) {
                System.out.println("✅ Người dùng đã đăng nhập: " + loggedUser.getEmail());
                // CHỈ SET MỘT ATTRIBUTE DUY NHẤT
                request.setAttribute("user", loggedUser);
            } else {
                System.out.println("❌ Không có người dùng đăng nhập");
            }
        }

        // ========== LẤY BANNER CHO SLIDESHOW ==========
        List<Banner> banners = bannerService.getActiveBannersForHome();
        System.out.println("📊 Loaded " + banners.size() + " banners for slideshow");
        request.setAttribute("banners", banners);

        String statusParam = request.getParameter("status");
        String searchKeyword = request.getParameter("search");

        String normalizedStatus = normalizeStatusParam(statusParam);
        String currentStatus = getCurrentStatus(statusParam);

        List<Movie> movies;
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            movies = movieService.searchMovies(searchKeyword);
            request.setAttribute("searchKeyword", searchKeyword);
        } else {
            movies = movieService.getMoviesByStatusForHome(normalizedStatus);
        }

        request.setAttribute("movies", movies);
        request.setAttribute("currentStatus", currentStatus);
        request.setAttribute("fromServlet", true);

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // ========== CÁC PHƯƠNG THỨC HỖ TRỢ ==========

    /**
     * Kiểm tra đường dẫn có phải là file tĩnh không
     */
    private boolean isStaticResource(String path) {
        if (path == null) return false;
        return path.endsWith(".css") || path.endsWith(".js") ||
                path.endsWith(".png") || path.endsWith(".jpg") ||
                path.endsWith(".jpeg") || path.endsWith(".gif") ||
                path.endsWith(".ico") || path.endsWith(".svg") ||
                path.endsWith(".woff") || path.endsWith(".woff2") ||
                path.endsWith(".ttf") || path.endsWith(".eot") ||
                path.endsWith(".mp4") || path.endsWith(".mp3") ||
                path.endsWith(".webp") || path.endsWith(".avif");
    }

    /**
     * Chuẩn hóa tham số status từ URL
     */
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

    /**
     * Lấy trạng thái hiện tại để hiển thị trên UI
     */
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