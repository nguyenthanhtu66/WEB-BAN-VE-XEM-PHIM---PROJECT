package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;

public class OrderDetail implements Serializable {
    private int id;
    private int orderId;
    private int seatId;
    private int ticketTypeId;
    private double price;

    // Constructors
    public OrderDetail() {}

    public OrderDetail(int id, int orderId, int seatId, int ticketTypeId, double price) {
        this.id = id;
        this.orderId = orderId;
        this.seatId = seatId;
        this.ticketTypeId = ticketTypeId;
        this.price = price;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(int ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", seatId=" + seatId +
                ", ticketTypeId=" + ticketTypeId +
                ", price=" + price +
                '}';
    }
}