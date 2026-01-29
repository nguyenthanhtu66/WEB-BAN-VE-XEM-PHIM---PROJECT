package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.Ticket;
import vn.edu.hcmuaf.fit.demo1.util.DBContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketWarehouseDAO {

    // ===== Lấy danh sách vé theo user =====
    public List<Ticket> findByUserId(int userId) {
        List<Ticket> tickets = new ArrayList<>();

        String sql = """
                    SELECT
                        id,
                        movie_name,
                        show_time,
                        seats,
                        total_price,
                        status
                    FROM tickets
                    WHERE user_id = ?
                    ORDER BY show_time DESC
                """;

        try (Connection con = DBContext.getConnection();

                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Ticket t = new Ticket();
                t.setId(rs.getInt("id"));
                t.setMovieName(rs.getString("movie_name"));
                t.setShowTime(rs.getTimestamp("show_time"));
                t.setSeats(rs.getString("seats"));
                t.setTotalPrice(rs.getInt("total_price"));
                t.setStatus(rs.getString("status"));
                tickets.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tickets;
    }

    // ===== Hủy vé =====
    public boolean cancelTicket(int ticketId, int userId) {
    String sql = """
        UPDATE tickets
        SET status = 'CANCELLED'
        WHERE id = ? AND user_id = ? AND status = 'CONFIRMED'
    """;

    try (Connection con = DBContext.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, ticketId);
        ps.setInt(2, userId);
        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;

}

 // ===== Xóa vé đã hủy =====
    public boolean deleteCancelledTicket(int ticketId, int userId) {
        String sql = """
                    DELETE FROM tickets
                    WHERE id = ?
                      AND user_id = ?
                      AND status = 'CANCELLED'
                """;

        try (Connection con = DBContext.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ticketId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getTotalPriceByUser(int userId) {
        String sql = """
                    SELECT COALESCE(SUM(total_price), 0)
                    FROM tickets
                    WHERE user_id = ?
                      AND status = 'CONFIRMED'
                """;

        try (Connection con = DBContext.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countActiveTicketsByUser(int userId) {
        String sql = """
                    SELECT COUNT(*)
                    FROM tickets
                    WHERE user_id = ?
                      AND status = 'CONFIRMED'
                """;

        try (Connection con = DBContext.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
