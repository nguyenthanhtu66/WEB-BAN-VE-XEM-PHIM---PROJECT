package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.UserDao;
import vn.edu.hcmuaf.fit.demo1.model.User;

import java.util.List;

public class UserService {
    private final UserDao userDao = new UserDao();
    public List<User> getListUsers(){
        return userDao.getListUsers();
    }
    public void addUser(User user){
        userDao.addUser(user);
    }
    public void update(User u){ userDao.updateUser(u); }

    public void delete(int id){ userDao.deleteUser(id); }

    public void toggleActive(int id){ userDao.toggleActive(id); }
    public User getUserById(int id){
        return userDao.getUserById(id);
    }
    public List<User> searchUsers(String keyword, String role) {
        return userDao.searchUsers(keyword, role);
    }
}
