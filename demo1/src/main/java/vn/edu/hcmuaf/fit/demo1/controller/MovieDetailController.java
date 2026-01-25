package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.hcmuaf.fit.demo1.model.Movie;
import vn.edu.hcmuaf.fit.demo1.service.MovieService;

import java.io.IOException;

@WebServlet(name = "MovieDetailController", urlPatterns = {"/movie-detail"})
public class MovieDetailController extends HttpServlet {

    private final MovieService movieService = new MovieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // KIỂM TRA NẾU LÀ REQUEST CHO FILE TĨNH -> KHÔNG XỬ LÝ
        String servletPath = request.getServletPath();
        if (servletPath != null &&
                (servletPath.startsWith("/css/") || servletPath.startsWith("/image/") ||
                        servletPath.startsWith("/img/") || servletPath.endsWith(".css") ||
                        servletPath.endsWith(".png") || servletPath.endsWith(".jpg"))) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            int movieId = Integer.parseInt(idParam);
            Movie movie = movieService.getMovieById(movieId);

            if (movie == null) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            request.setAttribute("movie", movie);
            request.getRequestDispatcher("/Chi-tiet-phim.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi hệ thống: " + e.getMessage());
        }
    }
}