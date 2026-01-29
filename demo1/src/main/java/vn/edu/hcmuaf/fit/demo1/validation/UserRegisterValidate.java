package vn.edu.hcmuaf.fit.demo1.validation;

import vn.edu.hcmuaf.fit.demo1.dto.UserRegisterForm;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterValidate {
    public Map<String, String> validate(UserRegisterForm form){
        Map<String, String> errors = new HashMap<>();
        //Họ và tên:
        if (form.getFullName() == null || form.getFullName().trim().isEmpty()) {
            errors.put("fullName", "Vui lòng nhập họ và tên");
        } else if (!form.getFullName().matches("^[\\p{L}]+(\\s[\\p{L}]+)+$")) {
            errors.put("fullName", "Họ và tên phải có ít nhất 2 từ và chỉ chứa chữ cái");
        }
        //Email:
        if(form.getEmail() == null || form.getEmail().trim().isEmpty()){
            errors.put("email", "Vui lòng nhập email, đây là thông tin bắt buộc");
        }
        else if(!form.getEmail().matches("[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")){
            errors.put("email", "Email nhập vào không hợp lệ, vui lòng nhập lại");
        }
        //Số điện thoại:
        if(form.getPhone() == null || form.getPhone().trim().isEmpty()){
            errors.put("phone", "Vui lòng nhập số điện thoại, đây là thông tin bắt buộc");
        }
        //Giới tính:
        if(form.getGender() == null || form.getGender().trim().isEmpty()){
            errors.put("gender", "Vui lòng chọn giới tính");
        }
        //Ngày sinh:
        if(form.getBirthDate() == null || form.getBirthDate().trim().isEmpty()){
            errors.put("birthDate", "Vui lòng nhập ngày tháng năm sinh, đây là thông tin bắt buộc");
        }
        //Mật khẩu:
        if(form.getPassword() == null || form.getPassword().trim().isEmpty()){
            errors.put("password", "Vui lòng nhập mật khẩu, đây là thông tin bắt buộc");
        }
        else if(!form.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-])\\S{8,}$")){
            errors.put("password", "Mật khẩu không hợp lệ, mật khẩu phải dài hơn 8 ký tự, có ít nhất: 1 chữ số, 1 chữ thường, 1 chữ hoa, 1 ký tự đặc biệt và không có khoảng trắng");
        }
        if(form.getConfirmPass() == null || form.getConfirmPass().trim().isEmpty()){
            errors.put("confirmPass", "Vui lòng nhập mật khẩu xác nhận");
        }
        else if(!form.getPassword().equals(form.getConfirmPass())){
            errors.put("confirmPass", "Mật khẩu xác nhận không trùng khớp");
        }
        else if(form.getConfirmPass() == null || form.getConfirmPass().trim().isEmpty()){
            errors.put("confirmPass", "Vui lòng nhập mật khẩu xác nhận giống ở trên");
        }
        //Tỉnh/Thành phố:
        if(form.getCity() == null || form.getCity().trim().isEmpty()){
            errors.put("city", "Vui lòng chọn tỉnh/thành phố, đây là thông tin bắt buộc");
        }
        return errors;
    }
}