package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
        HttpSession session = req.getSession();

        String success = (String) session.getAttribute("success");
        if (success != null) {
            req.setAttribute("success", success);
            session.removeAttribute("success"); // ⭐ QUAN TRỌNG
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

        // ===== TẠO MODEL NGAY TỪ ĐẦU =====
        Contact contact = new Contact(
                hoTen,
                soDienThoai,
                email,
                dichVu,
                chiTiet,
                dongY);

        // ===== VALIDATE SERVER-SIDE =====
        if (hoTen == null || hoTen.isBlank()
                || soDienThoai == null || soDienThoai.isBlank()
                || email == null || email.isBlank()
                || dichVu == null || dichVu.isBlank()) {

            req.setAttribute("error", "Vui lòng nhập đầy đủ các trường bắt buộc.");
            req.setAttribute("contact", contact); // ⭐ QUAN TRỌNG
            forward(req, resp);
            return;
        }

        if (dongY == null) {
            req.setAttribute("error", "Bạn phải đồng ý với điều khoản để tiếp tục.");
            req.setAttribute("contact", contact); // ⭐ QUAN TRỌNG
            forward(req, resp);
            return;
        }

        // ===== LƯU DB =====
        contactService.save(contact);

        // ===== PRG PATTERN =====
        req.getSession().setAttribute(
                "success",
                "Gửi liên hệ thành công! Chúng tôi sẽ phản hồi sớm.");

        resp.sendRedirect(req.getContextPath() + "/contact");

    }

    private void forward(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.getRequestDispatcher("/WEB-INF/views/contact.jsp")
                .forward(req, resp);
    }
}
