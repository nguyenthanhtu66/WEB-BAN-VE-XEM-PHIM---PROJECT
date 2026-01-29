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
        String action = request.getParameter("action");

        // Load list
        if (action == null) {

            List<User> users = userService.getListUsers();
            request.setAttribute("users", users);

            request.getRequestDispatcher("/admin/admin-users.jsp")
                    .forward(request, response);

            return;
        }

        // Get user (edit)
        if ("get".equals(action)) {

            int id = Integer.parseInt(request.getParameter("id"));

            User u = userService.getUserById(id);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String json = """
            {
              "id": %d,
              "fullName": "%s",
              "email": "%s",
              "gender": "%s",
              "birthDate": "%s",
              "role": "%s"
            }
            """.formatted(
                    u.getId(),
                    u.getFullName(),
                    u.getEmail(),
                    u.getGender(),
                    u.getBirthDate(),
                    u.getRole()
            );

            response.getWriter().write(json);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        System.out.println("=== DO POST ADMIN USERS ===");
        System.out.println("ACTION = " + request.getParameter("action"));


        String action = request.getParameter("action");

        // ADD
        if ("add".equals(action)) {

            User u = new User();

            u.setFullName(request.getParameter("fullName"));
            u.setEmail(request.getParameter("email"));
            u.setPassword(request.getParameter("password"));
            u.setGender(request.getParameter("gender"));
            u.setBirthDate(java.sql.Date.valueOf(LocalDate.parse(request.getParameter("birthDate"))));
            u.setRole(request.getParameter("role"));
            u.setActive(true);

            userService.addUser(u);
        }

        // EDIT
        if ("edit".equals(action)) {

            User u = new User();

            u.setId(Integer.parseInt(request.getParameter("id")));
            u.setFullName(request.getParameter("fullName"));
            u.setEmail(request.getParameter("email"));
            u.setGender(request.getParameter("gender"));
            u.setBirthDate(java.sql.Date.valueOf(LocalDate.parse(request.getParameter("birthDate"))));
            u.setRole(request.getParameter("role"));

            userService.update(u);
        }

        // DELETE
        if ("delete".equals(action)) {

            int id = Integer.parseInt(request.getParameter("id"));

            userService.delete(id);
        }

        // BAN
        if ("ban".equals(action)) {

            int id = Integer.parseInt(request.getParameter("id"));

            userService.toggleActive(id);
        }
        System.out.println("EMAIL = " + request.getParameter("email"));
        System.out.println("PASS = " + request.getParameter("password"));
        response.getWriter().write("success");
    }
}