package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import vn.edu.hcmuaf.fit.demo1.service.MovieService;
import vn.edu.hcmuaf.fit.demo1.model.Movie;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "HomeServlet", value = {"/home", "/index", "/"})
public class HomeServlet extends HttpServlet {

    private MovieService movieService;

    @Override
    public void init() throws ServletException {
        super.init();
        movieService = new MovieService();
        System.out.println("✅ HomeServlet initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get movies from service (sample data)
            List<Movie> showingMovies = movieService.getHomepageMovies();
            int totalMovies = movieService.getTotalShowingMovies();

            // Debug log
            System.out.println("📦 Loaded " + showingMovies.size() + " movies for homepage");

            // Set attributes for JSP
            request.setAttribute("showingMovies", showingMovies);
            request.setAttribute("totalMovies", totalMovies);

            // Forward to index.jsp
            request.getRequestDispatcher("/index.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            // Show simple error page
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<html><body style='font-family: Arial; padding: 20px;'>");
            response.getWriter().println("<h1 style='color: #ff6600;'>DTN Movie - Lỗi</h1>");
            response.getWriter().println("<p>Không thể tải trang chủ. Chi tiết lỗi: " + e.getMessage() + "</p>");
            response.getWriter().println("<a href='home' style='color: #ff6600;'>Thử lại</a>");
            response.getWriter().println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}