package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;

public class Room implements Serializable {
    private int id;
    private String roomName;
    private int totalSeats;
    private String roomType;
    private boolean isActive;

    public Room() {}

    public Room(int id, String roomName, int totalSeats, String roomType, boolean isActive) {
        this.id = id;
        this.roomName = roomName;
        this.totalSeats = totalSeats;
        this.roomType = roomType;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomName='" + roomName + '\'' +
                ", totalSeats=" + totalSeats +
                ", roomType='" + roomType + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}