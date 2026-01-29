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

    public BookedSeat() {}

    public BookedSeat(int id, int showtimeId, int seatId, Integer orderId,
                      Integer userId, String status, LocalDateTime reservedUntil,
                      LocalDateTime createdAt) {
        this.id = id;
        this.showtimeId = showtimeId;
        this.seatId = seatId;
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.reservedUntil = reservedUntil;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(int showtimeId) {
        this.showtimeId = showtimeId;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getReservedUntil() {
        return reservedUntil;
    }

    public void setReservedUntil(LocalDateTime reservedUntil) {
        this.reservedUntil = reservedUntil;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public boolean isExpired() {
        return reservedUntil != null && LocalDateTime.now().isAfter(reservedUntil);
    }

    public boolean isBooked() {
        return "booked".equals(status);
    }

    public boolean isReserved() {
        return "reserved".equals(status) && !isExpired();
    }

    @Override
    public String toString() {
        return "BookedSeat{" +
                "id=" + id +
                ", showtimeId=" + showtimeId +
                ", seatId=" + seatId +
                ", orderId=" + orderId +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", reservedUntil=" + reservedUntil +
                ", createdAt=" + createdAt +
                '}';
    }
}
