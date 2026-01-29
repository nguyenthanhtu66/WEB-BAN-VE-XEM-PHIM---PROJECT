// File: TicketWarehouseDao.java
package vn.edu.hcmuaf.fit.demo1.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import vn.edu.hcmuaf.fit.demo1.model.OrderDetail;
import vn.edu.hcmuaf.fit.demo1.model.TicketWarehouse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TicketWarehouseDao extends BaseDao {

    private static class TicketWarehouseMapper implements RowMapper<TicketWarehouse> {
        @Override
        public TicketWarehouse map(ResultSet rs, StatementContext ctx) throws SQLException {
            TicketWarehouse ticket = new TicketWarehouse();
            ticket.setId(rs.getInt("id"));
            ticket.setUserId(rs.getInt("user_id"));
            ticket.setOrderId(rs.getInt("order_id"));
            ticket.setOrderDetailId(rs.getInt("order_detail_id"));
            ticket.setTicketCode(rs.getString("ticket_code"));
            ticket.setMovieId(rs.getInt("movie_id"));
            ticket.setMovieTitle(rs.getString("movie_title"));
            ticket.setShowtimeId(rs.getInt("showtime_id"));

            if (rs.getDate("show_date") != null) {
                ticket.setShowDate(rs.getDate("show_date").toLocalDate());
            }
            if (rs.getTime("show_time") != null) {
                ticket.setShowTime(rs.getTime("show_time").toLocalTime());
            }

            ticket.setRoomId(rs.getInt("room_id"));
            ticket.setRoomName(rs.getString("room_name"));
            ticket.setSeatId(rs.getInt("seat_id"));
            ticket.setSeatCode(rs.getString("seat_code"));
            ticket.setTicketStatus(rs.getString("ticket_status"));

            return ticket;
        }
    }

    // Tạo vé mới trong kho vé
    public boolean createTicket(TicketWarehouse ticket) {
        String sql = """
            INSERT INTO ticket_warehouse 
            (user_id, order_id, order_detail_id, ticket_code, 
             movie_id, movie_title, showtime_id, show_date, show_time,
             room_id, room_name, seat_id, seat_code, ticket_status)
            VALUES 
            (:userId, :orderId, :orderDetailId, :ticketCode,
             :movieId, :movieTitle, :showtimeId, :showDate, :showTime,
             :roomId, :roomName, :seatId, :seatCode, :ticketStatus)
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("userId", ticket.getUserId())
                            .bind("orderId", ticket.getOrderId())
                            .bind("orderDetailId", ticket.getOrderDetailId())
                            .bind("ticketCode", ticket.getTicketCode())
                            .bind("movieId", ticket.getMovieId())
                            .bind("movieTitle", ticket.getMovieTitle())
                            .bind("showtimeId", ticket.getShowtimeId())
                            .bind("showDate", ticket.getShowDate())
                            .bind("showTime", ticket.getShowTime())
                            .bind("roomId", ticket.getRoomId())
                            .bind("roomName", ticket.getRoomName())
                            .bind("seatId", ticket.getSeatId())
                            .bind("seatCode", ticket.getSeatCode())
                            .bind("ticketStatus", ticket.getTicketStatus())
                            .execute()
            );

            return rows > 0;
        } catch (Exception e) {
            System.err.println("Error creating ticket: " + e.getMessage());
            return false;
        }
    }

    public List<TicketWarehouse> getTicketsByUserId(int userId) {
        try {
            String query = "SELECT tw.*, od.ticket_type_id, " +
                    "DATE(tw.show_date) as show_date, " +
                    "TIME(tw.show_time) as show_time " +
                    "FROM ticket_warehouse tw " +
                    "JOIN order_details od ON tw.order_detail_id = od.id " +
                    "WHERE tw.user_id = ? " +
                    "ORDER BY tw.show_date DESC, tw.show_time DESC";

            return get().withHandle(handle ->
                    handle.createQuery(query)
                            .bind(0, userId)
                            .map((rs, ctx) -> {
                                TicketWarehouse ticket = new TicketWarehouse();
                                ticket.setId(rs.getInt("id"));
                                ticket.setUserId(rs.getInt("user_id"));
                                ticket.setOrderId(rs.getInt("order_id"));
                                ticket.setOrderDetailId(rs.getInt("order_detail_id"));
                                ticket.setTicketCode(rs.getString("ticket_code"));
                                ticket.setMovieId(rs.getInt("movie_id"));
                                ticket.setMovieTitle(rs.getString("movie_title"));
                                ticket.setShowtimeId(rs.getInt("showtime_id"));

                                // Date conversion
                                java.sql.Date sqlDate = rs.getDate("show_date");
                                if (sqlDate != null) {
                                    ticket.setShowDate(sqlDate.toLocalDate());
                                }

                                // Time conversion
                                java.sql.Time sqlTime = rs.getTime("show_time");
                                if (sqlTime != null) {
                                    ticket.setShowTime(sqlTime.toLocalTime());
                                }

                                ticket.setRoomId(rs.getInt("room_id"));
                                ticket.setRoomName(rs.getString("room_name"));
                                ticket.setSeatId(rs.getInt("seat_id"));
                                ticket.setSeatCode(rs.getString("seat_code"));
                                ticket.setTicketStatus(rs.getString("ticket_status"));

                                // QUAN TRỌNG: Lấy ticket_type_id từ order_details
                                ticket.setTicketTypeId(rs.getInt("ticket_type_id"));
                                return ticket;
                            })
                            .list()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<TicketWarehouse> searchTicketsByUserId(int userId, String searchTerm) {
        String sql = """
        SELECT * FROM ticket_warehouse 
        WHERE user_id = :userId 
          AND (movie_title LIKE :search OR ticket_code LIKE :search)
        ORDER BY show_date DESC, show_time DESC
        """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("userId", userId)
                        .bind("search", "%" + searchTerm + "%")
                        .map(new TicketWarehouseMapper())
                        .list()
        );
    }
    public List<TicketWarehouse> getTicketsByStatus(int userId, String status) {
        String sql = """
        SELECT * FROM ticket_warehouse 
        WHERE user_id = :userId 
          AND ticket_status = :status
        ORDER BY show_date DESC, show_time DESC
        """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("userId", userId)
                        .bind("status", status)
                        .map(new TicketWarehouseMapper())
                        .list()
        );
    }
    public int getLastInsertId() {
        String sql = "SELECT LAST_INSERT_ID()";

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .mapTo(Integer.class)
                        .one()
        );
    }
    public int createOrderDetailAndGetId(OrderDetail orderDetail) {
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
            System.err.println("Error creating order detail and getting ID: " + e.getMessage());
            return 0;
        }
    }
    public boolean isTicketOwnedByUser(int ticketId, int userId) {
        try {
            String query = "SELECT COUNT(*) as count FROM ticket_warehouse " +
                    "WHERE id = ? AND user_id = ?";

            Integer count = get().withHandle(handle ->
                    handle.createQuery(query)
                            .bind(0, ticketId)
                            .bind(1, userId)
                            .mapTo(Integer.class)
                            .findOne()
                            .orElse(0)
            );

            return count != null && count > 0;

        } catch (Exception e) {
            System.err.println("❌ Error checking ticket ownership: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy trạng thái hiện tại của vé
     */
    public String getTicketStatus(int ticketId) {
        try {
            String query = "SELECT ticket_status FROM ticket_warehouse WHERE id = ?";

            return get().withHandle(handle ->
                    handle.createQuery(query)
                            .bind(0, ticketId)
                            .mapTo(String.class)
                            .findOne()
                            .orElse("unknown")
            );

        } catch (Exception e) {
            System.err.println("❌ Error getting ticket status: " + e.getMessage());
            return "unknown";
        }
    }

    /**
     * Hủy vé (chuyển status thành cancelled)
     */
    public boolean cancelTicket(int ticketId) {
        try {
            String query = "UPDATE ticket_warehouse SET ticket_status = 'cancelled' " +
                    "WHERE id = ? AND ticket_status = 'valid'";

            int rows = get().withHandle(handle ->
                    handle.createUpdate(query)
                            .bind(0, ticketId)
                            .execute()
            );

            System.out.println("✅ Cancelled ticket ID: " + ticketId + ", rows affected: " + rows);
            return rows > 0;

        } catch (Exception e) {
            System.err.println("❌ Error cancelling ticket: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy showtimeId từ ticket
     */
    public int getShowtimeId(int ticketId) {
        try {
            String query = "SELECT showtime_id FROM ticket_warehouse WHERE id = ?";

            return get().withHandle(handle ->
                    handle.createQuery(query)
                            .bind(0, ticketId)
                            .mapTo(Integer.class)
                            .findOne()
                            .orElse(0)
            );

        } catch (Exception e) {
            System.err.println("❌ Error getting showtimeId: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Lấy seatId từ ticket
     */
    public int getSeatId(int ticketId) {
        try {
            String query = "SELECT seat_id FROM ticket_warehouse WHERE id = ?";

            return get().withHandle(handle ->
                    handle.createQuery(query)
                            .bind(0, ticketId)
                            .mapTo(Integer.class)
                            .findOne()
                            .orElse(0)
            );

        } catch (Exception e) {
            System.err.println("❌ Error getting seatId: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Lấy orderId từ ticket
     */
    public int getOrderId(int ticketId) {
        try {
            String query = "SELECT order_id FROM ticket_warehouse WHERE id = ?";

            return get().withHandle(handle ->
                    handle.createQuery(query)
                            .bind(0, ticketId)
                            .mapTo(Integer.class)
                            .findOne()
                            .orElse(0)
            );

        } catch (Exception e) {
            System.err.println("❌ Error getting orderId: " + e.getMessage());
            return 0;
        }
    }
}