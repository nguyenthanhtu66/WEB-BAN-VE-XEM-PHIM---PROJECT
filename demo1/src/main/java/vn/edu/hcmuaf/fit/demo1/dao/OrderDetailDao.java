package vn.edu.hcmuaf.fit.demo1.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import vn.edu.hcmuaf.fit.demo1.model.OrderDetail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDao extends BaseDao {

    private static class OrderDetailMapper implements RowMapper<OrderDetail> {
        @Override
        public OrderDetail map(ResultSet rs, StatementContext ctx) throws SQLException {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setId(rs.getInt("id"));
            orderDetail.setOrderId(rs.getInt("order_id"));
            orderDetail.setSeatId(rs.getInt("seat_id"));
            orderDetail.setTicketTypeId(rs.getInt("ticket_type_id"));
            orderDetail.setPrice(rs.getDouble("price"));
            return orderDetail;
        }
    }

    // Tạo chi tiết đơn hàng
    public int createOrderDetail(OrderDetail orderDetail) {
        String sql = """
            INSERT INTO order_details (order_id, seat_id, ticket_type_id, price)
            VALUES (:orderId, :seatId, :ticketTypeId, :price)
            """;

        try {
            return get().withHandle(handle -> {
                handle.createUpdate(sql)
                        .bind("orderId", orderDetail.getOrderId())
                        .bind("seatId", orderDetail.getSeatId())
                        .bind("ticketTypeId", orderDetail.getTicketTypeId())
                        .bind("price", orderDetail.getPrice())
                        .execute();

                // Lấy ID vừa insert
                return handle.createQuery("SELECT LAST_INSERT_ID()")
                        .mapTo(Integer.class)
                        .one();
            });
        } catch (Exception e) {
            System.err.println("Error creating order detail: " + e.getMessage());
            return 0;
        }
    }

    // Lấy order detail theo ID
    public OrderDetail getOrderDetailById(int id) {
        String sql = "SELECT * FROM order_details WHERE id = :id";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("id", id)
                        .map(new OrderDetailMapper())
                        .findOne()
                        .orElse(null)
        );
    }


    // Lấy order detail theo order id
    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        String sql = "SELECT * FROM order_details WHERE order_id = :orderId";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("orderId", orderId)
                        .map(new OrderDetailMapper())
                        .list()
        );
    }
    public boolean deleteByOrderId(int orderId) {
        try {
            System.out.println("🗑️ Deleting order details for order ID: " + orderId);

            String query = "DELETE FROM order_details WHERE order_id = ?";

            int rows = get().withHandle(handle ->
                    handle.createUpdate(query)
                            .bind(0, orderId)
                            .execute()
            );

            System.out.println("✅ Deleted " + rows + " order details for order ID: " + orderId);
            return rows > 0;

        } catch (Exception e) {
            System.err.println("❌ Error deleting order details for order " + orderId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public List<OrderDetail> getByOrderId(int orderId) {
        try {
            String query = "SELECT * FROM order_details WHERE order_id = ?";

            return get().withHandle(handle ->
                    handle.createQuery(query)
                            .bind(0, orderId)
                            .mapToBean(OrderDetail.class)
                            .list()
            );

        } catch (Exception e) {
            System.err.println("❌ Error getting order details for order " + orderId + ": " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}