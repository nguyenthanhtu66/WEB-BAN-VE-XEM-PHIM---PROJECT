package vn.edu.hcmuaf.fit.demo1.model;

import java.time.LocalDateTime;
import java.util.List;

public class Reservation {
    private String reservationId;
    private int userId;
    private int showtimeId;
    private List<String> seatCodes;
    private LocalDateTime reservedUntil;
    private LocalDateTime createdAt;
    private String status; // active, expired, converted

    // Constructors
    public Reservation() {
        this.reservationId = generateReservationId();
        this.createdAt = LocalDateTime.now();
        this.status = "active";
    }

    public Reservation(int userId, int showtimeId, List<String> seatCodes, int minutes) {
        this();
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.seatCodes = seatCodes;
        this.reservedUntil = LocalDateTime.now().plusMinutes(minutes);
    }

    // Getters & Setters
    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getShowtimeId() { return showtimeId; }
    public void setShowtimeId(int showtimeId) { this.showtimeId = showtimeId; }

    public List<String> getSeatCodes() { return seatCodes; }
    public void setSeatCodes(List<String> seatCodes) { this.seatCodes = seatCodes; }

    public LocalDateTime getReservedUntil() { return reservedUntil; }
    public void setReservedUntil(LocalDateTime reservedUntil) { this.reservedUntil = reservedUntil; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Helper methods
    private String generateReservationId() {
        return "RES-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(reservedUntil);
    }

    public boolean isActive() {
        return "active".equals(status) && !isExpired();
    }

    public int getSeatCount() {
        return seatCodes != null ? seatCodes.size() : 0;
    }
}