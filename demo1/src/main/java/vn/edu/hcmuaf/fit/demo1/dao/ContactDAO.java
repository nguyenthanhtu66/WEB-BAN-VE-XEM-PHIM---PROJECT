package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.Contact;
import vn.edu.hcmuaf.fit.demo1.util.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO {

    private static final String INSERT_SQL = "INSERT INTO contacts (ho_ten, so_dien_thoai, email, dich_vu, chi_tiet) " +
            "VALUES (?, ?, ?, ?, ?)";

    public void insert(Contact contact) {
        DBContext db = new DBContext();

        try (
                Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            ps.setString(1, contact.getHoTen());
            ps.setString(2, contact.getSoDienThoai());
            ps.setString(3, contact.getEmail());
            ps.setString(4, contact.getDichVu());
            ps.setString(5, contact.getChiTiet());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Insert contact failed", e);
        }
    }

    public List<Contact> getAll() {
        List<Contact> list = new ArrayList<>();
        String sql = "SELECT * FROM contacts ORDER BY id DESC";

        try (
                Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Contact c = new Contact(
                        rs.getString("ho_ten"),
                        rs.getString("so_dien_thoai"),
                        rs.getString("email"),
                        rs.getString("dich_vu"),
                        rs.getString("chi_tiet"));
                c.setId(rs.getInt("id"));
                c.setStatus(rs.getString("status"));
                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void markDone(int id) {
        String sql = "UPDATE contacts SET status='done' WHERE id=?";

        try (
                Connection conn = new DBContext().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}