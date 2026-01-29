package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.User;

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
}