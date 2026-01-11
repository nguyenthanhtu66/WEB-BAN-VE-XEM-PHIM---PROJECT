package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.User;

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
}
