package vn.edu.hcmuaf.fit.demo1.model;

public class Contact {
    private String hoTen;
    private String sdt;
    private String email;
    private String dichVu;
    private String noiDung;

    public Contact() {
    }

    public Contact(String hoTen, String sdt, String email, String dichVu, String noiDung) {
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.email = email;
        this.dichVu = dichVu;
        this.noiDung = noiDung;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
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

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }
}