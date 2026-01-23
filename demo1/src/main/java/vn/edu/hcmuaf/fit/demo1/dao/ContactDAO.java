package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.Contact;
import vn.edu.hcmuaf.fit.demo1.util.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ContactDAO {

    private static final String INSERT_SQL =
        "INSERT INTO contacts (ho_ten, so_dien_thoai, email, dich_vu, chi_tiet) " +
        "VALUES (?, ?, ?, ?, ?)";

    public void insert(Contact contact) {
        DBContext db = new DBContext();

        try (
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(INSERT_SQL)
        ) {

            ps.setString(1, contact.getHoTen());
            ps.setString(2, contact.getSoDienThoai());
            ps.setString(3, contact.getEmail());
            ps.setString(4, contact.getDichVu());
            ps.setString(5, contact.getChiTiet());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
