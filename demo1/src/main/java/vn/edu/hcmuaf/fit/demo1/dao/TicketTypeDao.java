package vn.edu.hcmuaf.fit.demo1.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import vn.edu.hcmuaf.fit.demo1.model.TicketType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TicketTypeDao extends BaseDao {

    private static class TicketTypeMapper implements RowMapper<TicketType> {
        @Override
        public TicketType map(ResultSet rs, StatementContext ctx) throws SQLException {
            TicketType ticketType = new TicketType();
            ticketType.setId(rs.getInt("id"));
            ticketType.setTypeName(rs.getString("type_name"));
            ticketType.setDescription(rs.getString("description"));
            ticketType.setPrice(rs.getBigDecimal("price"));
            ticketType.setActive(rs.getBoolean("is_active"));
            return ticketType;
        }
    }

    // Lấy tất cả loại vé đang hoạt động
    public List<TicketType> getAllActiveTicketTypes() {
        String sql = """
            SELECT * FROM ticket_types 
            WHERE is_active = true 
            ORDER BY price DESC
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new TicketTypeMapper())
                        .list()
        );
    }

    // Lấy ticket type theo ID
    public TicketType getTicketTypeById(int id) {
        String sql = "SELECT * FROM ticket_types WHERE id = :id";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("id", id)
                        .map(new TicketTypeMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Kiểm tra ticket type có hợp lệ không
    public boolean isTicketTypeValid(int ticketTypeId) {
        String sql = "SELECT is_active FROM ticket_types WHERE id = :id";

        Boolean isActive = get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("id", ticketTypeId)
                        .mapTo(Boolean.class)
                        .findOne()
                        .orElse(false)
        );

        return isActive;
    }
}
