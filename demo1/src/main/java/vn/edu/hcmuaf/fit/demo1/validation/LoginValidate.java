package vn.edu.hcmuaf.fit.demo1.validation;

import vn.edu.hcmuaf.fit.demo1.dto.LoginForm;

import java.util.HashMap;
import java.util.Map;

public class LoginValidate {
    public Map<String, String> loginValidate(LoginForm form){
        Map<String, String> errors = new HashMap<>();
        if (form.getEmail() == null || form.getEmail().trim().isEmpty()) {
            errors.put("email", "Vui lòng nhập email");
        }
        if (form.getPassword() == null || form.getPassword().trim().isEmpty()) {
            errors.put("password", "Vui lòng nhập mật khẩu");
        }
        return errors;
    }
}