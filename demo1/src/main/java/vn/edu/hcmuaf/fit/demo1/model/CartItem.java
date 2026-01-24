package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String id; // UUID cho mỗi item
    private int movieId;
    private String movieTitle;
    private String posterUrl;
    private int showtimeId;
    private String showtime;
    private int roomId;
    private String room;
    private String ticketType;
    private int quantity;
    private String seats; // Danh sách ghế, format: "A01, A02, B03"
    private double unitPrice;
    private double total;
    private String reservationId; // ID để giữ ghế tạm thời

    // Constructors
    public CartItem() {}

    public CartItem(String id, int movieId, String movieTitle, String posterUrl,
                    int showtimeId, String showtime, int roomId, String room,
                    String ticketType, int quantity, String seats, double unitPrice) {
        this.id = id;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.posterUrl = posterUrl;
        this.showtimeId = showtimeId;
        this.showtime = showtime;
        this.roomId = roomId;
        this.room = room;
        this.ticketType = ticketType;
        this.quantity = quantity;
        this.seats = seats;
        this.unitPrice = unitPrice;
        this.total = unitPrice * quantity;
    }

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
        this.total = unitPrice * this.quantity;
    }

    public double getTotal() { return total; }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    // Helper methods
    public String getSeatsDisplay() {
        if (seats == null || seats.isEmpty()) return "Chưa chọn ghế";
        return seats;
    }

    public String getTicketTypeDisplay() {
        switch(ticketType) {
            case "adult": return "Người lớn";
            case "student": return "Học sinh/Sinh viên";
            case "child": return "Trẻ em";
            case "u22": return "U22";
            default: return ticketType;
        }
    }

    public void updateQuantity(int newQuantity) {
        this.quantity = Math.max(1, newQuantity);
        this.total = this.unitPrice * this.quantity;
    }
}