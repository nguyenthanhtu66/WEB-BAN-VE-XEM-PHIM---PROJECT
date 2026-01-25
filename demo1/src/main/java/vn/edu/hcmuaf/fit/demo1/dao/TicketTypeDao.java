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
            TicketType type = new TicketType();
            type.setId(rs.getInt("id"));
            type.setTypeName(rs.getString("type_name"));
            type.setDescription(rs.getString("description"));
            type.setPrice(rs.getDouble("price"));
            type.setIsActive(rs.getBoolean("is_active"));
            return type;
        }
    }

    // Lấy tất cả loại vé đang hoạt động
    public List<TicketType> getAllActiveTicketTypes() {
        String sql = """
            SELECT * FROM ticket_types 
            WHERE is_active = TRUE 
            ORDER BY id
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new TicketTypeMapper())
                        .list()
        );
    }

    // Lấy loại vé theo ID
    public TicketType getTicketTypeById(int typeId) {
        String sql = "SELECT * FROM ticket_types WHERE id = :typeId";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("typeId", typeId)
                        .map(new TicketTypeMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Lấy loại vé theo tên
    public TicketType getTicketTypeByName(String typeName) {
        String sql = "SELECT * FROM ticket_types WHERE type_name = :typeName AND is_active = TRUE";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("typeName", typeName)
                        .map(new TicketTypeMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Lấy giá vé theo loại
    public double getPriceByType(String typeName) {
        String sql = "SELECT price FROM ticket_types WHERE type_name = :typeName AND is_active = TRUE";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("typeName", typeName)
                        .mapTo(Double.class)
                        .findOne()
                        .orElse(100000.0) // Giá mặc định
        );
    }
}