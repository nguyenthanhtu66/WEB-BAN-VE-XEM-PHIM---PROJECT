package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import vn.edu.hcmuaf.fit.demo1.dao.UserDao;
import vn.edu.hcmuaf.fit.demo1.dto.UserRegisterForm;
import vn.edu.hcmuaf.fit.demo1.model.User;
import vn.edu.hcmuaf.fit.demo1.util.PasswordUtils;
import vn.edu.hcmuaf.fit.demo1.validation.UserRegisterValidate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

@WebServlet(name = "Register", value = "/Register")
public class RegisterController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserRegisterForm form = new UserRegisterForm();
        form.setFullName(request.getParameter("fullName"));
        form.setEmail(request.getParameter("email"));
        form.setPhone(request.getParameter("phoneNumber"));
        form.setGender(request.getParameter("gender"));
        form.setBirthDate(request.getParameter("birthDate"));
        form.setPassword(request.getParameter("password"));
        form.setConfirmPass(request.getParameter("confirmPassword"));
        form.setCity(request.getParameter("city"));
        UserRegisterValidate validate = new UserRegisterValidate();
        Map<String, String> errors = validate.validate(form);

        if(!errors.isEmpty()){
            request.setAttribute("errors", errors);
            request.setAttribute("form", form);
            request.getRequestDispatcher("/Register.jsp").forward(request,response);
            return;
        }
        System.out.println("ERRORS = " + errors);
        User user = new User();
        user.setEmail(form.getEmail());
        user.setPassword(PasswordUtils.hash(form.getPassword()));
        user.setFullName(form.getFullName());
        user.setPhone(form.getPhone());
        user.setGender(form.getGender());
        user.setBirthDate(LocalDate.parse(form.getBirthDate()));
        user.setCity(form.getCity());
        user.setRole("customer");
        user.setActive(true);

        System.out.println(">>> INSERT USER <<<");
        new UserDao().insert(user);
        System.out.println(">>> INSERT DONE <<<");

        response.sendRedirect("login.jsp");
    }
}