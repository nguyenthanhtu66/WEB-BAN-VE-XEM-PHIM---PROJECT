package vn.edu.hcmuaf.fit.demo1.model;

public class Room {
    private int id;
    private String roomName;
    private int totalSeats;
    private String roomType; // 2D, 3D, VIP
    private boolean isActive;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}