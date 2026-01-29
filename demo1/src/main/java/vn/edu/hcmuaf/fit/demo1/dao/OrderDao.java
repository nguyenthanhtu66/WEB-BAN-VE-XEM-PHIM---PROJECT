// OrderDao.java
package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.Order;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.core.mapper.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDao extends BaseDao {

    private static class OrderMapper implements RowMapper<Order> {
        @Override
        public Order map(ResultSet rs, StatementContext ctx) throws SQLException {
            Order order = new Order();
            order.setId(rs.getInt("id"));
            order.setOrderCode(rs.getString("order_code"));
            order.setUserId(rs.getInt("user_id"));
            order.setShowtimeId(rs.getInt("showtime_id"));
            order.setTotalQuantity(rs.getInt("total_quantity"));
            order.setTotalAmount(rs.getDouble("total_amount"));
            order.setPromotionId(rs.getInt("promotion_id"));
            order.setFinalAmount(rs.getDouble("final_amount"));
            order.setStatus(rs.getString("status"));
            order.setPaymentMethod(rs.getString("payment_method"));

            Timestamp bookingDate = rs.getTimestamp("booking_date");
            if (bookingDate != null) {
                order.setBookingDate(bookingDate.toLocalDateTime());
            }

            Timestamp paymentDate = rs.getTimestamp("payment_date");
            if (paymentDate != null) {
                order.setPaymentDate(paymentDate.toLocalDateTime());
            }

            return order;
        }
    }

    public int insertOrder(Order order) {
        String sql = """
            INSERT INTO orders (
                order_code, user_id, showtime_id, total_quantity, 
                total_amount, final_amount, status, payment_method, 
                booking_date, payment_date
            ) VALUES (
                CONCAT('#', LPAD(CAST(SUBSTRING_INDEX(CAST(RAND() * 1000000 AS CHAR), '.', 1) AS UNSIGNED), 6, '0')),
                :userId, :showtimeId, :totalQuantity, :totalAmount, 
                :finalAmount, :status, :paymentMethod, 
                NOW(), NOW()
            )
            """;

        try {
            return get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("userId", order.getUserId())
                            .bind("showtimeId", order.getShowtimeId())
                            .bind("totalQuantity", order.getTotalQuantity())
                            .bind("totalAmount", order.getTotalAmount())
                            .bind("finalAmount", order.getFinalAmount())
                            .bind("status", order.getStatus())
                            .bind("paymentMethod", order.getPaymentMethod())
                            .executeAndReturnGeneratedKeys("id")
                            .mapTo(Integer.class)
                            .one()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}