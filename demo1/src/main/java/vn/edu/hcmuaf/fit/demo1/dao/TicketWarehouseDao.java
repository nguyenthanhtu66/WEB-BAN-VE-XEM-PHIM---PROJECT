// TicketWarehouseDao.java
package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.TicketWarehouse;

public class TicketWarehouseDao extends BaseDao {

    public boolean insertTicket(TicketWarehouse ticket) {
        String sql = """
            INSERT INTO ticket_warehouse (
                user_id, order_id, order_detail_id, movie_id, 
                movie_title, showtime_id, show_date, show_time,
                room_id, room_name, seat_id, seat_code, ticket_status
            ) VALUES (
                :userId, :orderId, :orderDetailId, :movieId,
                (SELECT title FROM movies WHERE id = :movieId),
                :showtimeId, 
                (SELECT show_date FROM showtimes WHERE id = :showtimeId),
                (SELECT show_time FROM showtimes WHERE id = :showtimeId),
                (SELECT room_id FROM showtimes WHERE id = :showtimeId),
                (SELECT room_name FROM rooms WHERE id = (SELECT room_id FROM showtimes WHERE id = :showtimeId)),
                :seatId,
                (SELECT seat_code FROM seats WHERE id = :seatId),
                :ticketStatus
            )
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bindBean(ticket)
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}