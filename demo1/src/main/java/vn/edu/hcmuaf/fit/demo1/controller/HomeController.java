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

        String servletPath = request.getServletPath();
        if (servletPath != null && isStaticResource(servletPath)) {
            System.out.println("Bỏ qua file tĩnh: " + servletPath);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        System.out.println("====== TRANG CHỦ ĐƯỢC GỌI ======");
        System.out.println("U...");

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

    private boolean isStaticResource(String path) {
        return path.endsWith(".css") || path.endsWith(".js") ||
                path.endsWith(".png") || path.endsWith(".jpg") ||
                path.endsWith(".jpeg") || path.endsWith(".gif") ||
                path.endsWith(".ico") || path.endsWith(".svg");
    }

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