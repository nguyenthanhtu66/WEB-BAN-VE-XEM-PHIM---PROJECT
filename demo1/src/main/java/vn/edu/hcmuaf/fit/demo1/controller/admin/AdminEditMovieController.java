package vn.edu.hcmuaf.fit.demo1.controller.admin;

import vn.edu.hcmuaf.fit.demo1.dao.AdminMovieDao;
import vn.edu.hcmuaf.fit.demo1.model.Movie;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;

@WebServlet("/admin/movie-edit")
@MultipartConfig
public class AdminEditMovieController extends HttpServlet {

    private AdminMovieDao movieDao;

    @Override
    public void init() {
        movieDao = new AdminMovieDao();
    }

    // ===== LOAD PHIM LÊN FORM =====
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/movies");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/movies");
            return;
        }

        Movie movie = movieDao.getMovieById(id);
        if (movie == null) {
            response.sendRedirect(request.getContextPath() + "/admin/movies");
            return;
        }

        request.setAttribute("movie", movie);
        request.getRequestDispatcher("/admin/admin-movies.jsp")
                .forward(request, response);
    }

    // ===== CẬP NHẬT PHIM =====
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(request.getParameter("id"));

        String title = request.getParameter("title");
        String synopsis = request.getParameter("synopsis");
        String status = request.getParameter("status");

        Movie movie = movieDao.getMovieById(id);
        if (movie == null) {
            response.sendRedirect(request.getContextPath() + "/admin/movies");
            return;
        }

        movie.setTitle(title);
        movie.setSynopsis(synopsis);
        movie.setStatus(status);

        // ===== UPLOAD POSTER (NẾU CÓ) =====
        Part posterPart = request.getPart("poster");
        if (posterPart != null && posterPart.getSize() > 0) {

            String fileName = posterPart.getSubmittedFileName();
            String uploadPath = request.getServletContext().getRealPath("/uploads");

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists())
                uploadDir.mkdirs();

            posterPart.write(uploadPath + File.separator + fileName);
            movie.setPosterUrl("uploads/" + fileName);
        }

        movieDao.updateMovie(movie);

        response.sendRedirect(request.getContextPath() + "/admin/movies");
    }
}
