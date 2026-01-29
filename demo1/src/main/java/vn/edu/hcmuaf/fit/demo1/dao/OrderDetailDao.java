// OrderDetailDao.java
package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.OrderDetail;

public class OrderDetailDao extends BaseDao {

    public boolean insertOrderDetail(OrderDetail orderDetail) {
        String sql = """
            INSERT INTO order_details (order_id, seat_id, ticket_type_id, price)
            VALUES (:orderId, :seatId, :ticketTypeId, :price)
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("orderId", orderDetail.getOrderId())
                            .bind("seatId", orderDetail.getSeatId())
                            .bind("ticketTypeId", orderDetail.getTicketTypeId())
                            .bind("price", orderDetail.getPrice())
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}