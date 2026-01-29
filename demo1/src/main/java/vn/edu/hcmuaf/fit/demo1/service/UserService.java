package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.UserDao;
import vn.edu.hcmuaf.fit.demo1.model.User;

public class UserService {

    private final UserDao userDao = new UserDao();

    public void updateProfile(int userId,
                              String fullName,
                              String phone,
                              String email,
                              String gender,
                              String birthDate) {

        userDao.updateProfile(userId, fullName, phone, email, gender, birthDate);
    }

    public User getById(int id) {
        return userDao.findById(id);
    }
}
