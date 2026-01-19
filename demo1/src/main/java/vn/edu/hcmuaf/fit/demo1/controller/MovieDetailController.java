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

    private MovieService movieService = new MovieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
            request.getRequestDispatcher("Chi-tiet-phim.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}