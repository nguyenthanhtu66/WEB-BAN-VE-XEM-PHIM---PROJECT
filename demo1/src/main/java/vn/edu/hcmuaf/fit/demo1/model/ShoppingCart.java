package vn.edu.hcmuaf.fit.demo1.model;

import java.util.*;

public class ShoppingCart {
    private List<CartItem> items = new ArrayList<>();
    private double subtotal = 0;
    private double serviceFee = 0;
    private double discount = 0;
    private double grandTotal = 0;
    private int totalItems = 0;

    // Getters and Setters
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getServiceFee() { return serviceFee; }
    public void setServiceFee(double serviceFee) { this.serviceFee = serviceFee; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }

    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }

    // Business methods
    public void addItem(CartItem item) {
        // Kiểm tra xem item đã tồn tại chưa (cùng phim, suất chiếu, loại vé)
        for (CartItem existingItem : items) {
            if (existingItem.equals(item)) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                calculateTotals();
                return;
            }
        }

        items.add(item);
        calculateTotals();
    }

    public boolean removeItem(String itemId) {
        boolean removed = items.removeIf(item -> item.getId().equals(itemId));
        if (removed) {
            calculateTotals();
        }
        return removed;
    }

    public boolean updateItemQuantity(String itemId, int newQuantity) {
        if (newQuantity <= 0) {
            return removeItem(itemId);
        }

        for (CartItem item : items) {
            if (item.getId().equals(itemId)) {
                item.setQuantity(newQuantity);
                item.setTotal(item.getUnitPrice() * newQuantity);
                calculateTotals();
                return true;
            }
        }
        return false;
    }

    public void clear() {
        items.clear();
        calculateTotals();
    }

    public void calculateTotals() {
        subtotal = 0;
        totalItems = 0;

        for (CartItem item : items) {
            subtotal += item.getTotal();
            totalItems += item.getQuantity();
        }

        // Phí dịch vụ 5%
        serviceFee = subtotal * 0.05;

        // Tính tổng
        grandTotal = subtotal + serviceFee - discount;
    }

    public double getItemTotal(String itemId) {
        for (CartItem item : items) {
            if (item.getId().equals(itemId)) {
                return item.getTotal();
            }
        }
        return 0;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}