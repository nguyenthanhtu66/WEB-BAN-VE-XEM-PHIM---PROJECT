package vn.edu.hcmuaf.fit.demo1.controller.admin;

import vn.edu.hcmuaf.fit.demo1.dao.AdminMovieDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/admin/movie-hide")
public class AdminHideMovieController extends HttpServlet {

    private AdminMovieDao movieDao;

    @Override
    public void init() {
        movieDao = new AdminMovieDao();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int movieId = Integer.parseInt(request.getParameter("id"));

        // XÓA MỀM
        movieDao.hideMovie(movieId); // status = 'ended'

        response.sendRedirect(request.getContextPath() + "/admin/movies");
    }
}


