package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.User;

import java.util.HashMap;
import java.util.List;
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
    public void updateProfile(int id,
                              String fullName,
                              String phone,
                              String email,
                              String gender,
                              String birthDate) {

        get().useHandle(handle ->
                handle.createUpdate("""
                    UPDATE users
                    SET full_name  = :fullName,
                        phone      = :phone,
                        email      = :email,
                        gender     = :gender,
                        birth_date = :birthDate
                    WHERE id = :id
                """)
                        .bind("id", id)
                        .bind("fullName", fullName)
                        .bind("phone", phone)
                        .bind("email", email)
                        .bind("gender", gender)
                        .bind("birthDate", birthDate)
                        .execute()
        );
    }
    public List<User> getListUsers(){
        return get().withHandle(handle ->

                handle.createQuery("""
            SELECT 
              id,
              full_name   AS fullName,
              email,
              gender,
              birth_date  AS birthDate,
              role,
              is_active   AS active,
              created_at AS createdAt
            FROM users
        """)
                        .mapToBean(User.class)
                        .list()
        );
    }
    public void addUser(User user){
        get().withHandle(handle ->
                handle.createUpdate("""
            INSERT INTO users(
                full_name,
                email,
                password,
                gender,
                birth_date,
                role,
                is_active
            )
            VALUES (
                :name,
                :email,
                :password,
                :gender,
                :birth,
                :role,
                :active
            )
        """)
                        .bind("name", user.getFullName())
                        .bind("email", user.getEmail())
                        .bind("password", user.getPassword())
                        .bind("gender", user.getGender())
                        .bind("birth", user.getBirthDate())
                        .bind("role", user.getRole())
                        .bind("active", user.isActive())
                        .execute()
        );
    }
    public User getUserById(int id){
        return get().withHandle(handle ->
                handle.createQuery("SELECT * FROM users WHERE id = :id")
                        .bind("id", id)
                        .mapToBean(User.class)
                        .one()
        );
    }
    public void updateUser(User u){
        get().withHandle(handle ->
                handle.createUpdate("""
            UPDATE users 
            SET 
                full_name = :name,
                email = :email,
                gender = :gender,
                birth_date = :birth,
                role = :role
            WHERE id = :id
        """)
                        .bind("name", u.getFullName())
                        .bind("email", u.getEmail())
                        .bind("gender", u.getGender())
                        .bind("birth", u.getBirthDate())
                        .bind("role", u.getRole())
                        .bind("id", u.getId())
                        .execute()
        );
    }
    public void deleteUser(int id){
        get().withHandle(handle ->
                handle.createUpdate("DELETE FROM users WHERE id = :id")
                        .bind("id", id)
                        .execute()
        );
    }
    public void toggleActive(int id){
        get().withHandle(handle ->
                handle.createUpdate("""
            UPDATE users 
            SET is_active = !is_active 
            WHERE id = :id
        """)
                        .bind("id", id)
                        .execute()
        );
    }
}