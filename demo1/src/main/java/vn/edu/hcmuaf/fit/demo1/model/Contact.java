package vn.edu.hcmuaf.fit.demo1.model;

public class Contact {
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String dichVu;
    private String chiTiet;
    private String dy;
  
    public Contact(String hoTen, String soDienThoai, String email, String dichVu, String chiTiet, String dy) {
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.dichVu = dichVu;
        this.chiTiet = chiTiet;
        this.dy = dy;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDichVu() {
        return dichVu;
    }

    public void setDichVu(String dichVu) {
        this.dichVu = dichVu;
    }

    public String getChiTiet() {
        return chiTiet;
    }

    public void setChiTiet(String chiTiet) {
        this.chiTiet = chiTiet;
    }

    public String getDy() {
        return dy;
    }

    public void setDy(String dy) {
        this.dy = dy;
    }
   

    
    
}
