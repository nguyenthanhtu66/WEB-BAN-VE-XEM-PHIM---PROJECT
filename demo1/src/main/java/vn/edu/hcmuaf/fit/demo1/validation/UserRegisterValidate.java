package vn.edu.hcmuaf.fit.demo1.validation;

import vn.edu.hcmuaf.fit.demo1.dto.UserRegisterForm;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterValidate {
    public Map<String, String> validate(UserRegisterForm form){
        Map<String, String> errors = new HashMap<>();
        //Họ và tên:
        if(form.getFullName() == null){
            errors.put("fullName", "Vui lòng nhập họ và tên, đây là thông tin bắt buộc");
        }
        if(!form.getFullName().matches("^[A-Z][a-z]*(\\\\s[A-Z][a-z]*)+$")) {
            errors.put("fullName", "Họ và tên không hợp lệ, vui lòng nhập lại");
        }
        //Email:
        if(form.getEmail() == null){
            errors.put("email", "Vui lòng nhập email, đây là thông tin bắt buộc");
        }
        if(!form.getEmail().matches("[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")){
            errors.put("email", "Email nhập vào không hợp lệ, vui lòng nhập lại");
        }
        //Số điện thoại:
        if(form.getPhone() == null){
            errors.put("phone", "Vui lòng nhập số điện thoại, đây là thông tin bắt buộc");
        }
        if(!form.getPhone().matches("/(84|0[3|5|7|8|9])+([0-9]{8})\\b/g")){
            errors.put("phone", "Số điện thoại không hợp lệ, vui lòng nhập lại");
        }
        //Giới tính:
        if(form.getGender() == null){
            errors.put("gender", "Vui lòng chọn giới tính");
        }
        //Ngày sinh:
        if(form.getBrDate() == null){
            errors.put("brDate", "Vui lòng nhập ngày tháng năm sinh, đây là thông tin bắt buộc");
        }
        //Mật khẩu:
        if(form.getPassword() == null){
            errors.put("password", "Vui lòng nhập mật khẩu, đây là thông tin bắt buộc");
        }
        if(!form.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-]).{8,}$\n")){
            errors.put("password", "Mật khẩu không hợp lệ, mật khẩu phải dài hơn 8 ký tự, có ít nhất: 1 chữ số, 1 chữ thường, 1 chữ hoa, 1 ký tự đặc biệt và không có khoảng trắng");
        }
        if(form.getPassword().equals(form.getConfirmPass())){
            errors.put("confirmPass", "Mật khẩu xác nhận không trùng khớp với mật khẩu, vui lòng nhập lại");
        }
        //Tỉnh/Thành phố:
        if(form.getCity() == null){
            errors.put("city", "Vui lòng chọn tỉnh/thành phố, đây là thông tin bắt buộc");
        }
        return errors;
    }
}
