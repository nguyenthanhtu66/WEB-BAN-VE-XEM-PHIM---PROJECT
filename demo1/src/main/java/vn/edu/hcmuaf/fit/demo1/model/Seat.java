package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;

public class Seat implements Serializable {
    private int id;
    private int roomId;
    private String seatCode;
    private String rowNumber;
    private int seatNumber;
    private String seatType;
    private boolean isActive;

    public Seat() {}

    public Seat(int id, int roomId, String seatCode, String rowNumber,
                int seatNumber, String seatType, boolean isActive) {
        this.id = id;
        this.roomId = roomId;
        this.seatCode = seatCode;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public String getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(String rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", roomId=" + roomId +
                ", seatCode='" + seatCode + '\'' +
                ", rowNumber='" + rowNumber + '\'' +
                ", seatNumber=" + seatNumber +
                ", seatType='" + seatType + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}