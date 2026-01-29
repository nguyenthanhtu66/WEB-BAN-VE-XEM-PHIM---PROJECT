package vn.edu.hcmuaf.fit.demo1.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class TicketWarehouse {
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
    private String ticketStatus;
    private int ticketTypeId; // ĐÃ THÊM

    // GETTERS AND SETTERS
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getOrderDetailId() { return orderDetailId; }
    public void setOrderDetailId(int orderDetailId) { this.orderDetailId = orderDetailId; }

    public String getTicketCode() { return ticketCode; }
    public void setTicketCode(String ticketCode) { this.ticketCode = ticketCode; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public int getShowtimeId() { return showtimeId; }
    public void setShowtimeId(int showtimeId) { this.showtimeId = showtimeId; }

    public LocalDate getShowDate() { return showDate; }
    public void setShowDate(LocalDate showDate) { this.showDate = showDate; }

    public LocalTime getShowTime() { return showTime; }
    public void setShowTime(LocalTime showTime) { this.showTime = showTime; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }

    public String getSeatCode() { return seatCode; }
    public void setSeatCode(String seatCode) { this.seatCode = seatCode; }

    public String getTicketStatus() { return ticketStatus; }
    public void setTicketStatus(String ticketStatus) { this.ticketStatus = ticketStatus; }

    // QUAN TRỌNG: Thêm getter/setter cho ticketTypeId
    public int getTicketTypeId() { return ticketTypeId; }
    public void setTicketTypeId(int ticketTypeId) { this.ticketTypeId = ticketTypeId; }

    // QUAN TRỌNG: Thêm method getTicketTypeName()
    public String getTicketTypeName() {
        if (ticketTypeId <= 0) {
            return "Thường";
        }

        switch (ticketTypeId) {
            case 1: return "Người lớn";
            case 2: return "Học sinh/Sinh viên";
            case 3: return "Trẻ em";
            case 4: return "U22";
            default: return "Thường";
        }
    }

    // Helper methods cho JSP
    public String getFormattedShowDate() {
        if (showDate == null) return "N/A";
        return String.format("%02d/%02d/%04d",
                showDate.getDayOfMonth(),
                showDate.getMonthValue(),
                showDate.getYear());
    }

    public String getFormattedShowTime() {
        if (showTime == null) return "N/A";
        return String.format("%02d:%02d",
                showTime.getHour(),
                showTime.getMinute());
    }
}