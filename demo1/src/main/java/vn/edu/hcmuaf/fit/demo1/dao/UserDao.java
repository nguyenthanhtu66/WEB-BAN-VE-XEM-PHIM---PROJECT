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

    // ===== THÊM MỚI =====
    public User findById(int id){
        return get().withHandle(handle ->
                handle.createQuery("SELECT * FROM users WHERE id = :id")
                        .bind("id", id)
                        .mapToBean(User.class)
                        .findOne()
                        .orElse(null)
        );
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
}
