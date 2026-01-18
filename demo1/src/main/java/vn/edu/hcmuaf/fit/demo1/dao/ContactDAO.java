package vn.edu.hcmuaf.fit.demo1.dao;
import vn.edu.hcmuaf.fit.demo1.util.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;

import vn.edu.hcmuaf.fit.demo1.model.Contact;

public class ContactDAO {
    public void insertContact(Contact c) {
        String sql = "INSERT INTO Contacts VALUES (?,?,?,?,?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getHoTen());
            ps.setString(2, c.getSdt());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getDichVu());
            ps.setString(5, c.getNoiDung());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
