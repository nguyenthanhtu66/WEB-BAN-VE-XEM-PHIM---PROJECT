package vn.edu.hcmuaf.fit.demo1.model;

import java.time.LocalDateTime;

public class BookedSeat {
    private int id;
    private int showtimeId;
    private int seatId;
    private Integer orderId;
    private Integer userId;
    private SeatStatus status;
    private String reservationId;
    private LocalDateTime reservedUntil;
    private LocalDateTime createdAt;

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

    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public LocalDateTime getReservedUntil() { return reservedUntil; }
    public void setReservedUntil(LocalDateTime reservedUntil) { this.reservedUntil = reservedUntil; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}