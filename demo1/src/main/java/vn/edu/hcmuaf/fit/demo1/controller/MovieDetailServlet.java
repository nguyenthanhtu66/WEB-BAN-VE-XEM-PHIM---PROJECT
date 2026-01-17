package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import vn.edu.hcmuaf.fit.demo1.service.MovieService;
import vn.edu.hcmuaf.fit.demo1.model.Movie;

import java.io.IOException;

@WebServlet(name = "MovieDetailServlet", value = "/movie-detail")
public class MovieDetailServlet extends HttpServlet {

    private MovieService movieService;

    @Override
    public void init() throws ServletException {
        super.init();
        movieService = new MovieService();
        System.out.println("MovieDetailServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String movieIdParam = request.getParameter("id");

        try {
            if (movieIdParam == null || movieIdParam.isEmpty()) {
                response.sendRedirect("home");
                return;
            }

            int movieId = Integer.parseInt(movieIdParam);
            System.out.println("Loading movie detail for ID: " + movieId);

            Movie movie = movieService.getMovieDetail(movieId);

            if (movie == null) {
                System.out.println("Movie not found with ID: " + movieId);
                response.sendRedirect("home");
                return;
            }

            System.out.println("Found movie: " + movie.getTitle());

            // Set movie data for JSP
            request.setAttribute("movie", movie);

            // Forward to your template
            request.getRequestDispatcher("/Chi-tiet-phim.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            System.err.println("Invalid movie ID format");
            response.sendRedirect("home");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("home");
        }
    }
}