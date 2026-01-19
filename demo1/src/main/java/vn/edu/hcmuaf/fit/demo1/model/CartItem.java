package vn.edu.hcmuaf.fit.demo1.model;

import java.util.Objects;

public class CartItem {
    private String id;
    private int movieId;
    private String movieTitle;
    private String posterUrl;
    private int showtimeId;
    private String showtime;
    private int roomId;
    private String room;
    private String ticketType; // adult, student, child
    private int quantity;
    private String seats; // "A01, A02, A03"
    private double unitPrice;
    private double total;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public int getShowtimeId() { return showtimeId; }
    public void setShowtimeId(int showtimeId) { this.showtimeId = showtimeId; }

    public String getShowtime() { return showtime; }
    public void setShowtime(String showtime) { this.showtime = showtime; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getTicketType() { return ticketType; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.total = this.unitPrice * quantity;
    }

    public String getSeats() { return seats; }
    public void setSeats(String seats) { this.seats = seats; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.total = unitPrice * quantity;
    }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    // Override equals và hashCode để kiểm tra trùng lặp
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return movieId == cartItem.movieId &&
                showtimeId == cartItem.showtimeId &&
                roomId == cartItem.roomId &&
                Objects.equals(ticketType, cartItem.ticketType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, showtimeId, roomId, ticketType);
    }
}