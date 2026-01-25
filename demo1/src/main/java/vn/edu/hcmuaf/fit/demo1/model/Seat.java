package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;

public class Seat implements Serializable {
    private int id;
    private int roomId;
    private String seatCode; // A01, B05, C12
    private String rowNumber; // A, B, C
    private int seatNumber; // 1, 2, 3
    private String seatType; // normal, vip
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
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getSeatCode() { return seatCode; }
    public void setSeatCode(String seatCode) { this.seatCode = seatCode; }

    public String getRowNumber() { return rowNumber; }
    public void setRowNumber(String rowNumber) { this.rowNumber = rowNumber; }

    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }

    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
}