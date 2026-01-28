package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class CartItem implements Serializable {
    private int movieId;
    private String movieTitle;
    private String moviePoster;
    private int showtimeId;
    private LocalDate showDate;
    private LocalTime showTime;
    private int roomId;
    private String roomName;
    private int seatId;
    private String seatCode;
    private int ticketTypeId;
    private String ticketTypeName;
    private double price;

    public CartItem() {}

    public CartItem(int movieId, String movieTitle, String moviePoster,
                    int showtimeId, LocalDate showDate, LocalTime showTime,
                    int roomId, String roomName,
                    int seatId, String seatCode,
                    int ticketTypeId, String ticketTypeName, double price) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.moviePoster = moviePoster;
        this.showtimeId = showtimeId;
        this.showDate = showDate;
        this.showTime = showTime;
        this.roomId = roomId;
        this.roomName = roomName;
        this.seatId = seatId;
        this.seatCode = seatCode;
        this.ticketTypeId = ticketTypeId;
        this.ticketTypeName = ticketTypeName;
        this.price = price;
    }

    // Getters and Setters
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

    public String getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
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

    public int getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(int ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public String getTicketTypeName() {
        return ticketTypeName;
    }

    public void setTicketTypeName(String ticketTypeName) {
        this.ticketTypeName = ticketTypeName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Helper methods
    public String getFormattedPrice() {
        return String.format("%,.0f đ", price);
    }

    public String getFormattedShowDateTime() {
        return showDate + " " + showTime;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "movieTitle='" + movieTitle + '\'' +
                ", showDate=" + showDate +
                ", showTime=" + showTime +
                ", seatCode='" + seatCode + '\'' +
                ", price=" + price +
                '}';
    }
}
