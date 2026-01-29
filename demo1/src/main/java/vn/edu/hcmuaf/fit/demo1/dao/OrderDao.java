package vn.edu.hcmuaf.fit.demo1.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import vn.edu.hcmuaf.fit.demo1.model.Order;
import vn.edu.hcmuaf.fit.demo1.model.OrderDetail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            order.setFinalAmount(rs.getDouble("final_amount"));
            order.setStatus(rs.getString("status"));

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

    // Lấy số order tiếp theo cho order code
    private int getNextOrderNumber() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(order_code, 2) AS UNSIGNED)), 0) + 1 FROM orders";

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .mapTo(Integer.class)
                            .one()
            );
        } catch (Exception e) {
            System.err.println("Error getting next order number: " + e.getMessage());
            // Nếu lỗi, trả về 1
            return 1;
        }
    }

    // Tạo đơn hàng mới với order code tự động
    public boolean createOrder(Order order) {
        String sql = """
            INSERT INTO orders (order_code, user_id, showtime_id, total_quantity, 
                              total_amount, final_amount, status, booking_date, payment_date)
            VALUES (:orderCode, :userId, :showtimeId, :totalQuantity, 
                   :totalAmount, :finalAmount, :status, NOW(), NOW())
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("orderCode", order.getOrderCode())
                            .bind("userId", order.getUserId())
                            .bind("showtimeId", order.getShowtimeId())
                            .bind("totalQuantity", order.getTotalQuantity())
                            .bind("totalAmount", order.getTotalAmount())
                            .bind("finalAmount", order.getFinalAmount())
                            .bind("status", order.getStatus())
                            .execute()
            );

            return rows > 0;
        } catch (Exception e) {
            System.err.println("Error creating order: " + e.getMessage());
            return false;
        }
    }

    // Phương thức đơn giản không cần order code - SỬA LẠI
    public int createSimpleOrder(Order order) {
        // Tạo order_code duy nhất
        String orderCode = generateOrderCode();

        String sql = """
        INSERT INTO orders 
        (order_code, user_id, showtime_id, total_quantity, total_amount, 
         final_amount, status, booking_date, payment_date)
        VALUES 
        (:orderCode, :userId, :showtimeId, :totalQuantity, :totalAmount, 
         :finalAmount, :status, :bookingDate, :paymentDate)
        """;

        try {
            return get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("orderCode", orderCode)
                            .bind("userId", order.getUserId())
                            .bind("showtimeId", order.getShowtimeId())
                            .bind("totalQuantity", order.getTotalQuantity())
                            .bind("totalAmount", order.getTotalAmount())
                            .bind("finalAmount", order.getFinalAmount())
                            .bind("status", order.getStatus())
                            .bind("bookingDate", order.getBookingDate() != null ? order.getBookingDate() : LocalDateTime.now())
                            .bind("paymentDate", order.getPaymentDate() != null ? order.getPaymentDate() : LocalDateTime.now())
                            .executeAndReturnGeneratedKeys("id")
                            .mapTo(Integer.class)
                            .one()
            );
        } catch (Exception e) {
            System.err.println("Error creating simple order: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    // Lấy order theo ID
    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM orders WHERE id = :id";

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("id", orderId)
                            .map(new OrderMapper())
                            .findOne()
                            .orElse(null)
            );
        } catch (Exception e) {
            System.err.println("Error getting order: " + e.getMessage());
            return null;
        }
    }

    // Lấy last insert ID
    public int getLastInsertId(int userId) {
        String sql = "SELECT id FROM orders WHERE user_id = :userId ORDER BY id DESC LIMIT 1";

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .bind("userId", userId)
                            .mapTo(Integer.class)
                            .findOne()
                            .orElse(0)
            );
        } catch (Exception e) {
            System.err.println("Error getting last insert ID: " + e.getMessage());
            return 0;
        }
    }

    // Tạo order detail - SỬA LẠI
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

    // Cập nhật tổng tiền đơn hàng
    public boolean updateOrderAmount(int orderId, double totalAmount) {
        String sql = """
            UPDATE orders 
            SET total_amount = :totalAmount, final_amount = :finalAmount
            WHERE id = :orderId
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("totalAmount", totalAmount)
                            .bind("finalAmount", totalAmount)
                            .bind("orderId", orderId)
                            .execute()
            );

            return rows > 0;
        } catch (Exception e) {
            System.err.println("Error updating order amount: " + e.getMessage());
            return false;
        }
    }
    private String generateOrderCode() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf((int) (Math.random() * 1000));
        return "ORDER-" + timestamp.substring(7) + "-" + random;
    }
    private String generateOrderCodeFromDB() {
        String sql = "SELECT CONCAT('ORDER-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', LPAD(COALESCE(MAX(CAST(SUBSTRING(order_code, -3) AS UNSIGNED)), 0) + 1, 3, '0')) " +
                "FROM orders WHERE order_code LIKE CONCAT('ORDER-', DATE_FORMAT(NOW(), '%Y%m%d'), '-%')";

        try {
            return get().withHandle(handle ->
                    handle.createQuery(sql)
                            .mapTo(String.class)
                            .findOne()
                            .orElseGet(() -> "ORDER-" +
                                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                                    "-001")
            );
        } catch (Exception e) {
            System.err.println("Error generating order code: " + e.getMessage());
            return "ORDER-" + System.currentTimeMillis();
        }
    }
    public boolean deleteOrder(int orderId) {
        try {
            System.out.println("🗑️ Deleting order ID: " + orderId);

            // 1. Kiểm tra xem order có tồn tại không
            Order order = getById(orderId);
            if (order == null) {
                System.out.println("ℹ️ Order " + orderId + " does not exist");
                return true; // Không tồn tại cũng coi như đã xóa
            }

            // 2. Xóa order
            String query = "DELETE FROM orders WHERE id = ?";

            int rows = get().withHandle(handle ->
                    handle.createUpdate(query)
                            .bind(0, orderId)
                            .execute()
            );

            if (rows > 0) {
                System.out.println("✅ Deleted order ID: " + orderId);

                // 3. Cập nhật booked_seats liên quan (nếu cần)
                try {
                    String updateSeatsQuery = "UPDATE booked_seats SET order_id = NULL WHERE order_id = ?";
                    get().withHandle(handle ->
                            handle.createUpdate(updateSeatsQuery)
                                    .bind(0, orderId)
                                    .execute()
                    );
                    System.out.println("✅ Updated booked_seats for order ID: " + orderId);
                } catch (Exception e) {
                    System.err.println("⚠️ Could not update booked_seats: " + e.getMessage());
                }

                return true;
            } else {
                System.out.println("❌ Failed to delete order ID: " + orderId);
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ Error deleting order " + orderId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy order theo ID
     */
    public Order getById(int orderId) {
        try {
            String query = "SELECT * FROM orders WHERE id = ?";

            return get().withHandle(handle ->
                    handle.createQuery(query)
                            .bind(0, orderId)
                            .mapToBean(Order.class)
                            .findOne()
                            .orElse(null)
            );

        } catch (Exception e) {
            System.err.println("❌ Error getting order " + orderId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Rollback tất cả orders trong một transaction
     */
    public boolean rollbackOrders(List<Integer> orderIds) {
        System.out.println("🔄 Rolling back " + orderIds.size() + " orders...");

        boolean allSuccess = true;
        for (int orderId : orderIds) {
            if (!deleteOrder(orderId)) {
                allSuccess = false;
            }
        }

        System.out.println("🔄 Rollback completed: " + (allSuccess ? "SUCCESS" : "PARTIAL"));
        return allSuccess;
    }
}