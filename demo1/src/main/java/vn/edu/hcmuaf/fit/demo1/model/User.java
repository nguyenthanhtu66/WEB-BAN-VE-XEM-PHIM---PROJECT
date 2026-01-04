package vn.edu.hcmuaf.fit.demo1.model;

import java.util.Date;

public class User {
    private int id;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String role; // "USER", "ADMIN"
    private boolean isActive;
    private Date createdAt;

    // Constructors
    public User() {}

    public User(int id, String email, String password, String fullName, String phone, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.isActive = true;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public int getId() {return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}