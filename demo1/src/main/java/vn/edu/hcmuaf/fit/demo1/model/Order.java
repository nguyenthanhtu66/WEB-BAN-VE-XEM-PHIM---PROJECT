package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Order implements Serializable {
    private int id;
    private String orderCode;
    private int userId;
    private int showtimeId;
    private int totalQuantity;
    private double totalAmount;
    private Integer promotionId;
    private double finalAmount;
    private String status; // pending, paid, cancelled, processing
    private Integer paymentMethodId;
    private LocalDateTime bookingDate;
    private LocalDateTime paymentDate;
    private String notes;

    // Constructors
    public Order() {}

    public Order(int id, String orderCode, int userId, int showtimeId,
                 int totalQuantity, double totalAmount, Integer promotionId,
                 double finalAmount, String status, Integer paymentMethodId,
                 LocalDateTime bookingDate, LocalDateTime paymentDate, String notes) {
        this.id = id;
        this.orderCode = orderCode;
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.promotionId = promotionId;
        this.finalAmount = finalAmount;
        this.status = status;
        this.paymentMethodId = paymentMethodId;
        this.bookingDate = bookingDate;
        this.paymentDate = paymentDate;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(int showtimeId) {
        this.showtimeId = showtimeId;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Integer paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderCode='" + orderCode + '\'' +
                ", userId=" + userId +
                ", showtimeId=" + showtimeId +
                ", totalQuantity=" + totalQuantity +
                ", totalAmount=" + totalAmount +
                ", promotionId=" + promotionId +
                ", finalAmount=" + finalAmount +
                ", status='" + status + '\'' +
                ", paymentMethodId=" + paymentMethodId +
                ", bookingDate=" + bookingDate +
                ", paymentDate=" + paymentDate +
                ", notes='" + notes + '\'' +
                '}';
    }
}