package vn.edu.hcmuaf.fit.demo1.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.hcmuaf.fit.demo1.model.Contact;

@WebServlet(name = "ContactController", urlPatterns = { "/xu-ly-lien-he" })
public class ContactController extends HttpServlet {
    private static List<Contact> danhsachgia = new ArrayList<>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
System.out.println("---- DA GOI DUOC VAO SERVLET ROI NE! ----");
        resp.getWriter().println("Servlet da hoat dong!");
        req.setCharacterEncoding("UTF-8");

        String hoTen = req.getParameter("hoTen");
        String soDienThoai = req.getParameter("soDienThoai");
        String email = req.getParameter("email");
        String dichVu = req.getParameter("dichVu");
        String chiTiet = req.getParameter("chiTiet");
        String dy = req.getParameter("dy");

        // Nếu check bị null nghĩa là khách hàng chưa tích (hoặc cố tình lách luật)
        if (dy == null) {
            req.setAttribute("thongBao", "Lỗi: Bạn phải tích vào ô đồng ý!");
            req.getRequestDispatcher("contact.jsp").forward(req, resp);
            return; // Dừng chương trình tại đây, không chạy xuống phần lưu dữ liệu bên dưới
        }

        Contact ngươimoi = new Contact(hoTen, soDienThoai, email, dichVu, chiTiet, dy);
        danhsachgia.add(ngươimoi);

        req.setAttribute("thongbao", "Cảm ơn" + hoTen + "Chúng tôi đã nhận tin");
        req.setAttribute("listTuServlet", danhsachgia);

        req.getRequestDispatcher("Contact.jsp").forward(req, resp);

    }

}
