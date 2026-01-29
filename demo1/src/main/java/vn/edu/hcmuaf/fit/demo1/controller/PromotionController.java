package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import vn.edu.hcmuaf.fit.demo1.dao.PromoteDao;
import vn.edu.hcmuaf.fit.demo1.model.News;
import vn.edu.hcmuaf.fit.demo1.model.Promote;
import vn.edu.hcmuaf.fit.demo1.service.NewsService;
import vn.edu.hcmuaf.fit.demo1.service.PromoteService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "PromotionController", value = "/khuyen-mai")
public class PromotionController extends HttpServlet {
    private static final int ITEMS_PER_PAGE = 8;
    private final PromoteService promoteService = new PromoteService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = 1;
        if(request.getParameter("page") !=null){
            page = Integer.parseInt(request.getParameter("page"));

        }
        int offset = (page - 1) * ITEMS_PER_PAGE;
        List<Promote> promsList = promoteService.getPromPagni(ITEMS_PER_PAGE,offset);
        int totalProms = promoteService.countPromotions();
        int totalPage = (int) Math.ceil((double) totalProms / ITEMS_PER_PAGE);

        request.setAttribute("promsList", promsList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPage", totalPage);

        request.getRequestDispatcher("Khuyen-mai.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}