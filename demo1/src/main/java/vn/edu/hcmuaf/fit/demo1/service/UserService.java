package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.model.User;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    // Danh sách user mẫu
    private List<User> sampleUsers;

    public UserService() {
        initSampleUsers();
    }

    private void initSampleUsers() {
        sampleUsers = new ArrayList<>();

        // Thêm user mẫu
        sampleUsers.add(new User(1, "admin@dtn.com", "123456", "Admin DTN", "0987654321", "ADMIN"));
        sampleUsers.add(new User(2, "user@dtn.com", "123456", "Nguyễn Văn A", "0912345678", "USER"));
        sampleUsers.add(new User(3, "customer@dtn.com", "123456", "Trần Thị B", "0934567890", "USER"));
    }

    // Đăng nhập
    public User login(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        // Tìm user trong danh sách mẫu
        for (User user : sampleUsers) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }

        return null;
    }

    // Đăng ký (chỉ thêm vào danh sách mẫu)
    public boolean register(User newUser) {
        if (newUser == null || newUser.getEmail() == null) {
            return false;
        }

        // Kiểm tra email đã tồn tại
        for (User user : sampleUsers) {
            if (user.getEmail().equals(newUser.getEmail())) {
                return false;
            }
        }

        // Tạo ID mới
        int newId = sampleUsers.size() + 1;
        newUser.setId(newId);

        // Thêm vào danh sách
        sampleUsers.add(newUser);
        return true;
    }

    // Kiểm tra email hợp lệ
    public boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    // Kiểm tra password mạnh
    public boolean isStrongPassword(String password) {
        return password != null && password.length() >= 6;
    }

    // Lấy user theo ID
    public User getUserById(int id) {
        for (User user : sampleUsers) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
}