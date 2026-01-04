package vn.edu.hcmuaf.fit.demo1.model;

import java.util.Date;
import java.util.List;

public class Booking {
    private int id;
    private int userId;
    private int movieId;
    private String movieTitle;
    private String cinemaRoom;
    private Date showtime;
    private List<String> seats;
    private double totalPrice;
    private String status; // "PENDING", "CONFIRMED", "CANCELLED"
    private Date bookingDate;

    // Constructors
    public Booking() {}

    public Booking(int userId, int movieId, String movieTitle, List<String> seats, double totalPrice) {
        this.userId = userId;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.seats = seats;
        this.totalPrice = totalPrice;
        this.status = "PENDING";
        this.bookingDate = new Date();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getCinemaRoom() { return cinemaRoom; }
    public void setCinemaRoom(String cinemaRoom) { this.cinemaRoom = cinemaRoom; }

    public Date getShowtime() { return showtime; }
    public void setShowtime(Date showtime) { this.showtime = showtime; }

    public List<String> getSeats() { return seats; }
    public void setSeats(List<String> seats) { this.seats = seats; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }
}