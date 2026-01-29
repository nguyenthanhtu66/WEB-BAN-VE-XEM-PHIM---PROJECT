package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class TicketType implements Serializable {
    private int id;
    private String typeName;
    private String description;
    private BigDecimal price;
    private boolean isActive;

    public TicketType() {}

    public TicketType(int id, String typeName, String description,
                      BigDecimal price, boolean isActive) {
        this.id = id;
        this.typeName = typeName;
        this.description = description;
        this.price = price;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Helper method để lấy giá theo base price
    public double getActualPrice(double basePrice) {
        return basePrice * price.doubleValue();
    }

    public String getFormattedPrice(double basePrice) {
        double actualPrice = getActualPrice(basePrice);
        return String.format("%,.0f đ", actualPrice);
    }

    @Override
    public String toString() {
        return "TicketType{" +
                "id=" + id +
                ", typeName='" + typeName + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", isActive=" + isActive +
                '}';
    }
}