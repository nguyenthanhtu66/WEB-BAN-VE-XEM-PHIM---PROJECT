package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.hcmuaf.fit.demo1.dao.UserDao;
import vn.edu.hcmuaf.fit.demo1.dto.UserRegisterForm;
import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.util.PasswordUtils;
import vn.edu.hcmuaf.fit.demo1.validation.UserRegisterValidate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.sql.Date;
import java.util.Map;

@WebServlet(name = "RegisterController", value = "/Register")
public class RegisterController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hiển thị form đăng ký
        request.getRequestDispatcher("/Register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set encoding UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // Lấy dữ liệu từ form
        UserRegisterForm form = new UserRegisterForm();
        form.setFullName(request.getParameter("fullName"));
        form.setEmail(request.getParameter("email"));
        form.setPhone(request.getParameter("phoneNumber"));
        form.setGender(request.getParameter("gender"));
        form.setBirthDate(request.getParameter("birthDate"));
        form.setPassword(request.getParameter("password"));
        form.setConfirmPass(request.getParameter("confirmPassword"));
        form.setCity(request.getParameter("city"));

        System.out.println("=== DEBUG REGISTRATION ===");
        System.out.println("Email: " + form.getEmail());
        System.out.println("Phone: " + form.getPhone());
        System.out.println("Gender: " + form.getGender());
        System.out.println("BirthDate: " + form.getBirthDate());
        System.out.println("City: " + form.getCity());

        // Validate dữ liệu
        UserRegisterValidate validator = new UserRegisterValidate();
        Map<String, String> errors = validator.validate(form);

        // Kiểm tra email đã tồn tại
        if (errors.get("email") == null) { // Nếu email hợp lệ về format
            try {
                UserDao userDao = new UserDao();
                if (userDao.emailExists(form.getEmail())) {
                    errors.put("email", "Email này đã được đăng ký");
                }
            } catch (Exception e) {
                e.printStackTrace();
                errors.put("system", "Lỗi hệ thống khi kiểm tra email: " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            System.out.println("Validation errors: " + errors);
            // Có lỗi, trả về form với thông báo lỗi
            request.setAttribute("errors", errors);
            request.setAttribute("form", form); // Giữ lại dữ liệu đã nhập
            request.getRequestDispatcher("/Register.jsp").forward(request, response);
            return;
        }

        try {
            // Tạo đối tượng User
            User newUser = new User();
            newUser.setEmail(form.getEmail().trim().toLowerCase());
            newUser.setPassword(PasswordUtils.hash(form.getPassword())); // Mã hóa mật khẩu
            newUser.setFullName(form.getFullName().trim());
            newUser.setPhone(form.getPhone().trim());
            newUser.setGender(form.getGender());
            newUser.setCity(form.getCity());

            // Xử lý ngày sinh
            if (form.getBirthDate() != null && !form.getBirthDate().trim().isEmpty()) {
                try {
                    LocalDate birthDate = LocalDate.parse(form.getBirthDate(), DateTimeFormatter.ISO_DATE);
                    newUser.setBirthDate(Date.valueOf(birthDate));
                } catch (DateTimeParseException e) {
                    // Nếu định dạng không đúng, thử định dạng khác
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        LocalDate birthDate = LocalDate.parse(form.getBirthDate(), formatter);
                        newUser.setBirthDate(Date.valueOf(birthDate));
                    } catch (DateTimeParseException e2) {
                        // Bỏ qua nếu không parse được
                        System.err.println("Cannot parse birthDate: " + form.getBirthDate());
                    }
                }
            }

            // Đặt các giá trị mặc định
            newUser.setRole("customer");
            newUser.setIsActive(true);
            newUser.setAvatarUrl(""); // Để trống, có thể set mặc định sau

            // Lưu vào database
            UserDao userDao = new UserDao();
            userDao.insert(newUser);

            System.out.println("✅ User registered successfully: " + newUser.getEmail());
            System.out.println("User ID: " + newUser.getId());

            // Đăng ký thành công
            request.setAttribute("registerSuccess", true);
            request.setAttribute("successMessage", "Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay.");

            // Tự động đăng nhập sau khi đăng ký (tuỳ chọn)
            // request.getSession().setAttribute("user", newUser);
            // response.sendRedirect("home");

            request.getRequestDispatcher("/Register.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Registration error: " + e.getMessage());

            // Lỗi hệ thống
            request.setAttribute("errorMessage", "Đã xảy ra lỗi hệ thống khi đăng ký. Vui lòng thử lại sau.");
            request.setAttribute("form", form);
            request.getRequestDispatcher("/Register.jsp").forward(request, response);
        }
    }
}