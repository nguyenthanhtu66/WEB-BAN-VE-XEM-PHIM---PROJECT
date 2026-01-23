package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShoppingCart implements Serializable {
    private List<CartItem> items = new ArrayList<>();
    private double subtotal = 0;
    private double serviceFee = 0;
    private double discount = 0;
    private double grandTotal = 0;

    // Getters và Setters
    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        calculateTotals();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
        calculateTotals();
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    // Tính tổng số items
    public int getTotalItems() {
        int total = 0;
        for (CartItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }

    // Thêm item vào giỏ hàng
    public boolean addItem(CartItem item) {
        // Kiểm tra xem item đã tồn tại chưa
        for (CartItem existingItem : items) {
            if (existingItem.getMovieId() == item.getMovieId() &&
                    existingItem.getShowtimeId() == item.getShowtimeId() &&
                    existingItem.getRoomId() == item.getRoomId() &&
                    existingItem.getTicketType().equals(item.getTicketType()) &&
                    existingItem.getSeats().equals(item.getSeats())) {

                // Cập nhật số lượng
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                existingItem.setTotal(existingItem.getUnitPrice() * existingItem.getQuantity());
                calculateTotals();
                return true;
            }
        }

        // Thêm item mới
        items.add(item);
        calculateTotals();
        return true;
    }

    // Cập nhật số lượng
    public boolean updateQuantity(String itemId, int newQuantity) {
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

    // Xóa item
    public boolean removeItem(String itemId) {
        boolean removed = items.removeIf(item -> item.getId().equals(itemId));
        if (removed) {
            calculateTotals();
        }
        return removed;
    }

    // Xóa toàn bộ giỏ hàng
    public void clear() {
        items.clear();
        subtotal = 0;
        serviceFee = 0;
        discount = 0;
        grandTotal = 0;
    }

    // Tính toán tổng tiền
    private void calculateTotals() {
        subtotal = 0;
        for (CartItem item : items) {
            subtotal += item.getTotal();
        }

        // Phí dịch vụ 5%
        serviceFee = subtotal * 0.05;

        // Tổng cộng
        grandTotal = subtotal + serviceFee - discount;

        // Làm tròn 2 chữ số thập phân
        subtotal = Math.round(subtotal * 100.0) / 100.0;
        serviceFee = Math.round(serviceFee * 100.0) / 100.0;
        discount = Math.round(discount * 100.0) / 100.0;
        grandTotal = Math.round(grandTotal * 100.0) / 100.0;
    }

    // Tìm item theo ID
    public CartItem getItemById(String itemId) {
        for (CartItem item : items) {
            if (item.getId().equals(itemId)) {
                return item;
            }
        }
        return null;
    }

    // Kiểm tra giỏ hàng có trống không
    public boolean isEmpty() {
        return items.isEmpty();
    }
}