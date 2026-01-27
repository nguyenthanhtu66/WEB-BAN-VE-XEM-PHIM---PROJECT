package vn.edu.hcmuaf.fit.demo1.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import vn.edu.hcmuaf.fit.demo1.model.CartItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CartDao extends BaseDao {

    // Lưu giỏ hàng vào database (nếu cần)
    public boolean saveCartToDatabase(int userId, String cartData) {
        // Implementation tùy theo yêu cầu
        // Có thể lưu vào session hoặc database tạm thời
        return true;
    }

    // Load giỏ hàng từ database (nếu cần)
    public String loadCartFromDatabase(int userId) {
        // Implementation tùy theo yêu cầu
        return null;
    }

    // Kiểm tra tính khả dụng của ghế khi thêm vào giỏ hàng
    public boolean checkSeatAvailability(int showtimeId, String seatCode) {
        String sql = """
            SELECT COUNT(*) FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            WHERE bs.showtime_id = :showtimeId
            AND s.seat_code = :seatCode
            AND bs.status IN ('reserved', 'booked')
            AND (bs.reserved_until IS NULL OR bs.reserved_until > NOW())
            """;

        int count = get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .bind("seatCode", seatCode)
                        .mapTo(Integer.class)
                        .one()
        );

        return count == 0;
    }

    // Kiểm tra nhiều ghế cùng lúc
    public List<String> checkMultipleSeatsAvailability(int showtimeId, List<String> seatCodes) {
        String sql = """
            SELECT DISTINCT s.seat_code 
            FROM booked_seats bs
            JOIN seats s ON bs.seat_id = s.id
            WHERE bs.showtime_id = :showtimeId
            AND s.seat_code IN (<seatCodes>)
            AND bs.status IN ('reserved', 'booked')
            AND (bs.reserved_until IS NULL OR bs.reserved_until > NOW())
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .bindList("seatCodes", seatCodes)
                        .mapTo(String.class)
                        .list()
        );
    }

    // Lấy thông tin phim cho giỏ hàng
    public CartItem getMovieInfoForCart(int movieId) {
        String sql = """
            SELECT id, title, poster_url FROM movies 
            WHERE id = :movieId
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("movieId", movieId)
                        .map((rs, ctx) -> {
                            CartItem item = new CartItem();
                            item.setMovieId(rs.getInt("id"));
                            item.setMovieTitle(rs.getString("title"));
                            item.setPosterUrl(rs.getString("poster_url"));
                            return item;
                        })
                        .findOne()
                        .orElse(null)
        );
    }
}