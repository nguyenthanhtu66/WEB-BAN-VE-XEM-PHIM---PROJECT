package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;

public class TicketType implements Serializable {
    private int id;
    private String typeName; // Người lớn, Học sinh/Sinh viên, Trẻ em, U22
    private String description;
    private double price; // 1.00 = giá gốc, 0.80 = giảm 20%, 0.55 = giảm 45%
    private boolean isActive;

    public TicketType() {}

    public TicketType(int id, String typeName, String description, double price, boolean isActive) {
        this.id = id;
        this.typeName = typeName;
        this.description = description;
        this.price = price;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }

    // Helper methods
    public double getActualPrice(double basePrice) {
        return basePrice * price;
    }

    public String getDisplayName() {
        return typeName + " - " + String.format("%,.0f", price * 100) + "% giá gốc";
    }
}