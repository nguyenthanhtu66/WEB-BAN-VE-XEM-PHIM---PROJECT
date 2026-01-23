package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import vn.edu.hcmuaf.fit.demo1.model.Contact;
import vn.edu.hcmuaf.fit.demo1.service.ContactService;

@WebServlet("/contact")
public class ContactController extends HttpServlet {

    private final ContactService contactService = new ContactService();

    // GET: hiển thị form
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Hiển thị thông báo thành công sau redirect (PRG)
        String success = req.getParameter("success");
        if ("1".equals(success)) {
            req.setAttribute("success",
                    "Gửi liên hệ thành công! Chúng tôi sẽ phản hồi sớm.");
        }

        req.getRequestDispatcher("/WEB-INF/views/contact.jsp")
           .forward(req, resp);
    }

    // POST: xử lý submit
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String hoTen = req.getParameter("hoTen");
        String soDienThoai = req.getParameter("soDienThoai");
        String email = req.getParameter("email");
        String dichVu = req.getParameter("dichVu");
        String chiTiet = req.getParameter("chiTiet");
        String dongY = req.getParameter("dy");

        // ===== VALIDATE SERVER-SIDE =====
        if (hoTen == null || hoTen.isBlank()
                || soDienThoai == null || soDienThoai.isBlank()
                || email == null || email.isBlank()
                || dichVu == null || dichVu.isBlank()) {

            req.setAttribute("error", "Vui lòng nhập đầy đủ các trường bắt buộc.");
            forward(req, resp);
            return;
        }

        if (dongY == null) {
            req.setAttribute("error", "Bạn phải đồng ý với điều khoản để tiếp tục.");
            forward(req, resp);
            return;
        }

        // ===== TẠO MODEL =====
        Contact contact = new Contact(
                hoTen,
                soDienThoai,
                email,
                dichVu,
                chiTiet,
                dongY
        );

        // ===== GỌI SERVICE (CHUẨN MVC) =====
        contactService.save(contact);

        // ===== PRG PATTERN =====
        resp.sendRedirect(req.getContextPath() + "/contact?success=1");
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.getRequestDispatcher("/WEB-INF/views/contact.jsp")
           .forward(req, resp);
    }
}
