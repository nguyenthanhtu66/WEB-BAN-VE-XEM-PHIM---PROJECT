package vn.edu.hcmuaf.fit.demo1.controller.admin;

import vn.edu.hcmuaf.fit.demo1.dao.AdminMovieDao;
import vn.edu.hcmuaf.fit.demo1.model.Movie;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/admin/movies")
@MultipartConfig
public class AdminMovieController extends HttpServlet {

    private AdminMovieDao movieDao;

    @Override
    public void init() {
        movieDao = new AdminMovieDao();
    }

    // HIỂN THỊ DANH SÁCH + LOAD PHIM CẦN SỬA
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Movie> movies = movieDao.getAllMoviesForAdmin();
        request.setAttribute("movies", movies);

        String editId = request.getParameter("editId");
        if (editId != null) {
            try {
                int id = Integer.parseInt(editId);
                Movie editMovie = movieDao.getMovieById(id);
                System.out.println("EDIT MOVIE = " + editMovie.getTitle()); // 👈 debug
                request.setAttribute("editMovie", editMovie);
            } catch (Exception ignored) {
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/admin/admin-movies.jsp")
                .forward(request, response);
    }

    // THÊM HOẶC SỬA PHIM
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String idStr = request.getParameter("id");
        String title = request.getParameter("title");
        String synopsis = request.getParameter("synopsis");
        String description = request.getParameter("description");
        String director = request.getParameter("director");
        String cast = request.getParameter("cast");
        String genre = request.getParameter("genre");
        String country = request.getParameter("country");
        String ageRating = request.getParameter("ageRating");
        String status = request.getParameter("status");

        // number
        int duration = 0;
        String durationStr = request.getParameter("duration");
        if (durationStr != null && !durationStr.isEmpty()) {
            duration = Integer.parseInt(durationStr);
        }

        Movie movie;

        if (idStr != null && !idStr.isEmpty()) {
            // 🔥 UPDATE
            int id = Integer.parseInt(idStr);
            movie = movieDao.getMovieById(id);
        } else {
            // 🔥 INSERT
            movie = new Movie();
            movie.setCreatedBy("admin");
        }

        movie.setTitle(title);
        movie.setSynopsis(synopsis);
        movie.setDescription(description);
        movie.setDirector(director);
        movie.setCast(cast);
        movie.setGenre(genre);
        movie.setCountry(country);
        movie.setAgeRating(ageRating);
        movie.setDuration(duration);
        movie.setStatus(status);

        String releaseDateStr = request.getParameter("releaseDate");
        if (releaseDateStr != null && !releaseDateStr.isEmpty()) {
            movie.setReleaseDate(LocalDate.parse(releaseDateStr));
        }

        if (idStr != null && !idStr.isEmpty()) {
            movieDao.updateMovie(movie);
        } else {
            movieDao.addMovie(movie);
        }

        response.sendRedirect(request.getContextPath() + "/admin/movies");
    }

}
