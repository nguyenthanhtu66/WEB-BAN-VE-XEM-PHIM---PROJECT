package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserDao extends BaseDao {

    // Thêm user mới
    public void insert(User user) {
        get().useHandle(handle ->
                handle.createUpdate("""
            INSERT INTO users(
              email, password, full_name, phone, gender, 
              birth_date, city, avatar_url, role, is_active,
              created_at, updated_at
            )
            VALUES (
              :email, :password, :fullName, :phone, :gender,
              :birthDate, :city, :avatarUrl, :role, :isActive,
              :createdAt, :updatedAt
            )
        """)
                        .bindBean(user)
                        .execute()
        );
    }

    // Tìm user bằng email
    public User findByEmail(String email) {
        return get().withHandle(handle ->
                handle.createQuery("SELECT * FROM users WHERE email = :email")
                        .bind("email", email)
                        .mapToBean(User.class)
                        .findOne()
                        .orElse(null)
        );
    }

    // Tìm user bằng ID
    public User findById(int id) {
        return get().withHandle(handle ->
                handle.createQuery("SELECT * FROM users WHERE id = :id")
                        .bind("id", id)
                        .mapToBean(User.class)
                        .findOne()
                        .orElse(null)
        );
    }

    // Kiểm tra email đã tồn tại chưa
    public boolean emailExists(String email) {
        return get().withHandle(handle ->
                handle.createQuery("SELECT COUNT(*) FROM users WHERE email = :email")
                        .bind("email", email)
                        .mapTo(Integer.class)
                        .one() > 0
        );
    }

    // Cập nhật thông tin user
    public void update(User user) {
        get().useHandle(handle ->
                handle.createUpdate("""
            UPDATE users SET
              full_name = :fullName,
              phone = :phone,
              gender = :gender,
              birth_date = :birthDate,
              city = :city,
              avatar_url = :avatarUrl,
              role = :role,
              is_active = :isActive,
              updated_at = :updatedAt,
              last_login = :lastLogin,
              last_logout = :lastLogout
            WHERE id = :id
        """)
                        .bindBean(user)
                        .execute()
        );
    }

    // Cập nhật mật khẩu
    public void updatePassword(int userId, String hashedPassword) {
        get().useHandle(handle ->
                handle.createUpdate("UPDATE users SET password = :password WHERE id = :id")
                        .bind("password", hashedPassword)
                        .bind("id", userId)
                        .execute()
        );
    }

    // Cập nhật thời gian đăng nhập cuối
    public void updateLastLogin(int userId) {
        get().useHandle(handle ->
                handle.createUpdate("UPDATE users SET last_login = NOW() WHERE id = :id")
                        .bind("id", userId)
                        .execute()
        );
    }

    // Cập nhật thời gian đăng xuất cuối
    public void updateLastLogout(int userId) {
        get().useHandle(handle ->
                handle.createUpdate("UPDATE users SET last_logout = NOW() WHERE id = :id")
                        .bind("id", userId)
                        .execute()
        );
    }

    // Xóa user (soft delete - đánh dấu không active)
    public void deactivateUser(int userId) {
        get().useHandle(handle ->
                handle.createUpdate("UPDATE users SET is_active = false WHERE id = :id")
                        .bind("id", userId)
                        .execute()
        );
    }

    // Lấy số lượng user
    public int countUsers() {
        return get().withHandle(handle ->
                handle.createQuery("SELECT COUNT(*) FROM users")
                        .mapTo(Integer.class)
                        .one()
        );
    }

    // Lấy số lượng user theo role
    public int countUsersByRole(String role) {
        return get().withHandle(handle ->
                handle.createQuery("SELECT COUNT(*) FROM users WHERE role = :role AND is_active = true")
                        .bind("role", role)
                        .mapTo(Integer.class)
                        .one()
        );
    }
    // Cập nhật chỉ avatar
    public void updateAvatar(int userId, String avatarUrl) {
        get().useHandle(handle ->
                handle.createUpdate("UPDATE users SET avatar_url = :avatarUrl, updated_at = NOW() WHERE id = :id")
                        .bind("avatarUrl", avatarUrl)
                        .bind("id", userId)
                        .execute()
        );
    }

    // Xóa avatar
    public void removeAvatar(int userId) {
        get().useHandle(handle ->
                handle.createUpdate("UPDATE users SET avatar_url = NULL, updated_at = NOW() WHERE id = :id")
                        .bind("id", userId)
                        .execute()
        );
    }

    // Lấy thống kê user
    public Map<String, Integer> getUserStats(int userId) {
        return get().withHandle(handle -> {
            Map<String, Integer> stats = new HashMap<>();

            // Lấy số vé đã đặt
            Integer totalTickets = handle.createQuery("""
            SELECT COUNT(*) FROM orders o 
            JOIN order_details od ON o.id = od.order_id 
            WHERE o.user_id = :userId AND o.status = 'paid'
        """)
                    .bind("userId", userId)
                    .mapTo(Integer.class)
                    .one();

            // Lấy số phim đã xem (dựa trên số suất chiếu khác nhau)
            Integer moviesWatched = handle.createQuery("""
            SELECT COUNT(DISTINCT s.movie_id) FROM orders o
            JOIN showtimes s ON o.showtime_id = s.id
            WHERE o.user_id = :userId AND o.status = 'paid'
        """)
                    .bind("userId", userId)
                    .mapTo(Integer.class)
                    .one();

            // Lấy tổng chi tiêu
            Integer totalSpent = handle.createQuery("""
            SELECT COALESCE(SUM(o.final_amount), 0) FROM orders o
            WHERE o.user_id = :userId AND o.status = 'paid'
        """)
                    .bind("userId", userId)
                    .mapTo(Integer.class)
                    .one();

            stats.put("totalTickets", totalTickets);
            stats.put("moviesWatched", moviesWatched);
            stats.put("totalSpent", totalSpent);

            return stats;
        });
    }
}