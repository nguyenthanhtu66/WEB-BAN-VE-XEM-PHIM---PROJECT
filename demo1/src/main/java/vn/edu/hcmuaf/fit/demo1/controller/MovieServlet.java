package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import vn.edu.hcmuaf.fit.demo1.model.Movie;
import vn.edu.hcmuaf.fit.demo1.service.MovieService;
import java.io.IOException;

@WebServlet(name = "MovieServlet", value = {"/movies", "/movie-detail"})
public class MovieServlet extends HttpServlet {
    private MovieService movieService;

    @Override
    public void init() {
        this.movieService = new MovieService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("detail".equals(action)) {
            showMovieDetail(request, response);
        } else {
            String type = request.getParameter("type");

            if ("coming-soon".equals(type)) {
                showComingSoonMovies(request, response);
            } else {
                showNowShowingMovies(request, response);
            }
        }
    }

    private void showNowShowingMovies(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("movies", movieService.getNowShowingMovies());
        request.setAttribute("pageTitle", "Phim Đang Chiếu");
        request.setAttribute("movieType", "now-showing");

        RequestDispatcher dispatcher = request.getRequestDispatcher("/Phim-Dang-Chieu.jsp");
        dispatcher.forward(request, response);
    }

    private void showComingSoonMovies(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("movies", movieService.getComingSoonMovies());
        request.setAttribute("pageTitle", "Phim Sắp Chiếu");
        request.setAttribute("movieType", "coming-soon");

        RequestDispatcher dispatcher = request.getRequestDispatcher("/Phim-Sap-Chieu.jsp");
        dispatcher.forward(request, response);
    }

    private void showMovieDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String movieIdStr = request.getParameter("id");

        if (movieIdStr == null || movieIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            int movieId = Integer.parseInt(movieIdStr);
            Movie movie = movieService.getMovieById(movieId);

            if (movie == null) {
                request.setAttribute("error", "Không tìm thấy phim!");
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            request.setAttribute("movie", movie);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Chi-tiet-phim.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}