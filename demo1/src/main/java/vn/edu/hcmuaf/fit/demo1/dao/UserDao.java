package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.User;

import java.util.List;

public class UserDao extends BaseDao {
    public void insert(User user){
        get().useHandle(handle ->
                handle.createUpdate("""
            insert into users(
              email, password, full_name, phone, gender, birth_date, role, is_active
            )
            values (
              :email, :password, :fullName, :phone, :gender, :birthDate, :role, :active
            )
        """)
                        .bindBean(user)
                        .execute()
        );
    }
    public User findByEmail(String email){
        return get().withHandle(handle ->
                handle.createQuery("SELECT * FROM users WHERE email = :email")
                        .bind("email", email)
                        .mapToBean(User.class)
                        .findOne()
                        .orElse(null)
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
                handle.createQuery("""
                SELECT 
                    id, 
                    full_name AS fullName, 
                    email, 
                    gender, 
                    birth_date AS birthDate, 
                    role, 
                    is_active AS active 
                FROM users WHERE id = :id
            """)
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
    public List<User> searchUsers(String keyword, String roleFilter) {
        return get().withHandle(handle -> {
            StringBuilder sql = new StringBuilder("""
            SELECT 
              id,
              full_name   AS fullName,
              email,
              gender,
              birth_date  AS birthDate,
              role,
              is_active   AS active,
              created_at  AS createdAt
            FROM users
            WHERE 1=1 
        """);

            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (full_name LIKE :keyword OR email LIKE :keyword) ");
            }

            if (roleFilter != null && !roleFilter.trim().isEmpty()) {
                sql.append(" AND role = :role ");
            }

            sql.append(" ORDER BY id DESC");

            var query = handle.createQuery(sql.toString());

            if (keyword != null && !keyword.trim().isEmpty()) {
                query.bind("keyword", "%" + keyword + "%");
            }
            if (roleFilter != null && !roleFilter.trim().isEmpty()) {
                query.bind("role", roleFilter);
            }
            return query.mapToBean(User.class).list();
        });
    }
}
