package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BookedSeat implements Serializable {
    private int id;
    private int showtimeId;
    private int seatId;
    private Integer orderId;
    private Integer userId;
    private String status; // reserved, booked, released
    private LocalDateTime reservedUntil;
    private LocalDateTime createdAt;

    // Thông tin bổ sung
    private String seatCode;
    private String rowNumber;
    private int seatNumber;

    public BookedSeat() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getShowtimeId() { return showtimeId; }
    public void setShowtimeId(int showtimeId) { this.showtimeId = showtimeId; }

    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getReservedUntil() { return reservedUntil; }
    public void setReservedUntil(LocalDateTime reservedUntil) { this.reservedUntil = reservedUntil; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getSeatCode() { return seatCode; }
    public void setSeatCode(String seatCode) { this.seatCode = seatCode; }

    public String getRowNumber() { return rowNumber; }
    public void setRowNumber(String rowNumber) { this.rowNumber = rowNumber; }

    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

    // Helper methods
    public boolean isExpired() {
        if (reservedUntil == null) return false;
        return reservedUntil.isBefore(LocalDateTime.now());
    }

    public boolean isReserved() {
        return "reserved".equals(status) && !isExpired();
    }

    public boolean isBooked() {
        return "booked".equals(status);
    }
}