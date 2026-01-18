package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// Import các lớp Model và DAO của bạn
import vn.edu.hcmuaf.fit.demo1.model.Contact;
import vn.edu.hcmuaf.fit.demo1.dao.ContactDAO;

@WebServlet(name = "ContactController", value = "/gui-lien-he")
public class ContactController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Cấu hình tiếng Việt để nhận dữ liệu không bị lỗi font
        request.setCharacterEncoding("UTF-8");

        // 2. Lấy dữ liệu từ Form (dựa trên thuộc tính name="..." trong contact.jsp)
        String hoTen = request.getParameter("ho_ten");
        String sdt = request.getParameter("so_dien_thoai");
        String email = request.getParameter("email");
        String dichVu = request.getParameter("dich_vu");
        String noiDung = request.getParameter("noi_dung");

        try {
            // 3. Đóng gói dữ liệu vào Model và gọi DAO để lưu vào Database
            Contact contact = new Contact(hoTen, sdt, email, dichVu, noiDung);
            ContactDAO dao = new ContactDAO();
            dao.insertContact(contact);

            // 4. TẠO TIN NHẮN THÀNH CÔNG
            request.setAttribute("successMsg", "Gửi yêu cầu thành công! Chúng tôi sẽ sớm liên hệ với bạn.");

        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, bạn có thể gửi tin nhắn lỗi
            request.setAttribute("errorMsg", "Đã xảy ra lỗi hệ thống, vui lòng thử lại sau.");
        }

        // 5. QUAN TRỌNG: Forward ngược lại trang contact.jsp thay vì trang kết quả mới
        // Lưu ý: Tên file phải là .jsp thì code Java hiển thị tin nhắn mới chạy được
        request.getRequestDispatcher("contact.jsp").forward(request, response);
    }
}