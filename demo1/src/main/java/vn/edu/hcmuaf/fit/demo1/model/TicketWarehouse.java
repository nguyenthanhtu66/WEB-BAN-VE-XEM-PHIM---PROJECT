package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class TicketWarehouse implements Serializable {
    private int id;
    private int userId;
    private int orderId;
    private int orderDetailId;
    private String ticketCode;
    private int movieId;
    private String movieTitle;
    private int showtimeId;
    private LocalDate showDate;
    private LocalTime showTime;
    private int roomId;
    private String roomName;
    private int seatId;
    private String seatCode;
    private String ticketStatus; // valid, used, cancelled, expired

    // Constructors
    public TicketWarehouse() {}

    public TicketWarehouse(int id, int userId, int orderId, int orderDetailId,
                           String ticketCode, int movieId, String movieTitle,
                           int showtimeId, LocalDate showDate, LocalTime showTime,
                           int roomId, String roomName, int seatId, String seatCode,
                           String ticketStatus) {
        this.id = id;
        this.userId = userId;
        this.orderId = orderId;
        this.orderDetailId = orderDetailId;
        this.ticketCode = ticketCode;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.showtimeId = showtimeId;
        this.showDate = showDate;
        this.showTime = showTime;
        this.roomId = roomId;
        this.roomName = roomName;
        this.seatId = seatId;
        this.seatCode = seatCode;
        this.ticketStatus = ticketStatus;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public int getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(int showtimeId) {
        this.showtimeId = showtimeId;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    public void setShowDate(LocalDate showDate) {
        this.showDate = showDate;
    }

    public LocalTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalTime showTime) {
        this.showTime = showTime;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    // Helper methods
    public String getFormattedShowDateTime() {
        if (showDate != null && showTime != null) {
            return showDate + " " + showTime;
        }
        return "";
    }

    public boolean isValid() {
        return "valid".equals(ticketStatus);
    }

    public boolean isUsed() {
        return "used".equals(ticketStatus);
    }

    public boolean isCancelled() {
        return "cancelled".equals(ticketStatus);
    }

    public boolean isExpired() {
        return "expired".equals(ticketStatus);
    }

    @Override
    public String toString() {
        return "TicketWarehouse{" +
                "id=" + id +
                ", ticketCode='" + ticketCode + '\'' +
                ", movieTitle='" + movieTitle + '\'' +
                ", showDate=" + showDate +
                ", showTime=" + showTime +
                ", seatCode='" + seatCode + '\'' +
                ", ticketStatus='" + ticketStatus + '\'' +
                '}';
    }
}