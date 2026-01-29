package vn.edu.hcmuaf.fit.demo1.controller.admin;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.service.UserService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "AdminListUserController", value = "/admin-users")
public class AdminListUserController extends HttpServlet {
    private final UserService userService = new UserService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
//        List<User> users = userService.getListUsers();

        // --- PHẦN TÌM KIẾM ---
        String keyword = request.getParameter("keyword");
        String roleFilter = request.getParameter("roleFilter");
        List<User> users;

        // Kiểm tra xem người dùng có đang tìm kiếm không
        boolean isSearching = (keyword != null && !keyword.trim().isEmpty())
                || (roleFilter != null && !roleFilter.trim().isEmpty());

        if (isSearching) {
            users = userService.searchUsers(keyword, roleFilter);
        } else {
            users = userService.getListUsers();
        }

        request.setAttribute("users", users);

        request.setAttribute("savedKeyword", keyword);
        request.setAttribute("savedRole", roleFilter);

        // Load list
        if ("edit".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                User userToEdit = userService.getUserById(id);

                if (userToEdit != null) {
                    request.setAttribute("userToEdit", userToEdit);
                    request.setAttribute("showModal", true);
                }
            } catch (NumberFormatException e) {
                // Nếu id không phải số, bỏ qua hoặc log lỗi
                System.err.println("ID không hợp lệ: " + request.getParameter("id"));
            }

        }

        // XÓA USER
        if ("delete".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                userService.delete(id);
            }
            // Xóa xong thì load lại trang ngay lập tức để thấy thay đổi
            response.sendRedirect("admin-users");
            return; // Dừng code tại đây, không chạy xuống phần dưới
        }

        // BAN/UNBAN USER
        if ("ban".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                userService.toggleActive(id);
            }
            response.sendRedirect("admin-users");
            return; // Dừng code
        }

        request.getRequestDispatcher("/admin/admin-users.jsp").forward(request, response);


    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // Lấy action từ form gửi lên
        String action = request.getParameter("action");

        // Debug để xem log (nếu cần)
        System.out.println("=== DO POST ===");
        System.out.println("Action received: " + action);

        // --- XỬ LÝ THÊM MỚI (ADD) ---
        if ("add".equals(action)) {
            User u = new User();
            u.setFullName(request.getParameter("fullName"));
            u.setEmail(request.getParameter("email"));
            u.setPassword(request.getParameter("password"));
            u.setGender(request.getParameter("gender"));
            u.setRole(request.getParameter("role"));

            // Xử lý ngày sinh an toàn (tránh lỗi nếu để trống)
            String birthDateStr = request.getParameter("birthDate");
            if (birthDateStr != null && !birthDateStr.isEmpty()) {
                u.setBirthDate(LocalDate.parse(birthDateStr));
            }

            u.setActive(true);
            userService.addUser(u);
        }

        // --- XỬ LÝ CẬP NHẬT (UPDATE) ---
        else if ("update".equals(action)) {
            User u = new User();

            // Lấy ID từ hidden input
            try {
                u.setId(Integer.parseInt(request.getParameter("id")));
            } catch (NumberFormatException e) {
                e.printStackTrace(); // Log lỗi nếu không lấy được ID
            }

            u.setFullName(request.getParameter("fullName"));
            u.setEmail(request.getParameter("email"));
            u.setGender(request.getParameter("gender"));
            u.setRole(request.getParameter("role"));

            // Xử lý ngày sinh an toàn
            String birthDateStr = request.getParameter("birthDate");
            if (birthDateStr != null && !birthDateStr.isEmpty()) {
                u.setBirthDate(LocalDate.parse(birthDateStr));
            }

            // Gọi hàm update trong service
            userService.update(u);
            System.out.println("Updated user ID: " + u.getId());
        }


        response.sendRedirect("admin-users");
    }
    }
