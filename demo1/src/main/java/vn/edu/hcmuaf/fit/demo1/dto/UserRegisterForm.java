package vn.edu.hcmuaf.fit.demo1.dto;

public class UserRegisterForm {
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private String birthDate;
    private String password;
    private String confirmPass;
    private String city;

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPass() {
        return confirmPass;
    }

    public String getCity() {
        return city;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }

    public void setCity(String city) {
        this.city = city;
    }
}