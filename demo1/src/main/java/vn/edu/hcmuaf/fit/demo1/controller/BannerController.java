package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import vn.edu.hcmuaf.fit.demo1.service.BannerService;
import java.io.IOException;

@WebServlet(name = "BannerController", urlPatterns = {"/banners"})
public class BannerController extends HttpServlet {
    private final BannerService bannerService = new BannerService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy banners cho trang chủ
        var banners = bannerService.getActiveBannersForHome();
        request.setAttribute("banners", banners);

        // Forward tới trang chủ
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}