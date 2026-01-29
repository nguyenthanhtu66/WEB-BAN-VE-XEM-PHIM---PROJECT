package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import vn.edu.hcmuaf.fit.demo1.model.News;
import vn.edu.hcmuaf.fit.demo1.service.NewsService;

import java.util.List;
import java.io.IOException;

@WebServlet(name = "NewsReviewController", value = "/binh-luan-phim")
public class NewsReviewController extends HttpServlet {
    private static final int ITEMS_PER_PAGE = 8;
    private final NewsService newsService = new NewsService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = 1;
        if(request.getParameter("page") !=null){
            page = Integer.parseInt(request.getParameter("page"));

        }
        int offset = (page - 1) * ITEMS_PER_PAGE;
        List<News> newsList = newsService.getReviewNews(ITEMS_PER_PAGE,offset);
        int totalNews = newsService.countReviewNews();
        int totalPage = (int) Math.ceil((double) totalNews / ITEMS_PER_PAGE);

        request.setAttribute("newsList", newsList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPage", totalPage);
        request.setAttribute("activeTab", "binhluan");
        request.getRequestDispatcher("Binh-luan-phim.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}