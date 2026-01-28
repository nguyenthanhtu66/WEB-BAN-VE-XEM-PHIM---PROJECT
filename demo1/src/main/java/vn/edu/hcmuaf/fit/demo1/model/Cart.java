package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Cart implements Serializable {
    private List<CartItem> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    // Thêm item vào giỏ hàng
    public void addItem(CartItem item) {
        // Kiểm tra xem item đã tồn tại chưa
        for (CartItem existingItem : items) {
            if (existingItem.getShowtimeId() == item.getShowtimeId() &&
                    existingItem.getSeatId() == item.getSeatId()) {
                // Nếu đã tồn tại thì không thêm nữa
                System.out.println("⚠ Item already exists in cart: showtimeId=" +
                        item.getShowtimeId() + ", seatId=" + item.getSeatId());
                return;
            }
        }
        items.add(item);
        System.out.println("✅ Item added to cart: " + item);
    }

    // Xóa item khỏi giỏ hàng
    public void removeItem(int showtimeId, int seatId) {
        boolean removed = items.removeIf(item ->
                item.getShowtimeId() == showtimeId && item.getSeatId() == seatId
        );
        System.out.println("✅ Item removed from cart: showtimeId=" + showtimeId +
                ", seatId=" + seatId + ", removed=" + removed);
    }

    // Xóa tất cả items của một showtime
    public void removeItemsByShowtime(int showtimeId) {
        int count = (int) items.stream()
                .filter(item -> item.getShowtimeId() == showtimeId)
                .count();
        items.removeIf(item -> item.getShowtimeId() == showtimeId);
        System.out.println("✅ Removed " + count + " items for showtimeId=" + showtimeId);
    }

    // Lấy tổng số vé
    public int getTotalItems() {
        return items.size();
    }

    // Lấy tổng tiền
    public double getTotalAmount() {
        return items.stream()
                .mapToDouble(CartItem::getPrice)
                .sum();
    }

    // Xóa toàn bộ giỏ hàng
    public void clear() {
        System.out.println("✅ Clearing cart with " + items.size() + " items");
        items.clear();
    }

    // Lấy danh sách seat IDs cho một showtime
    public List<Integer> getSeatIdsForShowtime(int showtimeId) {
        return items.stream()
                .filter(item -> item.getShowtimeId() == showtimeId)
                .map(CartItem::getSeatId)
                .collect(Collectors.toList());
    }

    // Kiểm tra xem một seat có trong giỏ hàng không
    public boolean containsSeat(int showtimeId, int seatId) {
        return items.stream()
                .anyMatch(item -> item.getShowtimeId() == showtimeId &&
                        item.getSeatId() == seatId);
    }

    // Getters and Setters
    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    // Helper method
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public String toString() {
        return "Cart{" +
                "items=" + items.size() +
                ", totalAmount=" + getTotalAmount() +
                '}';
    }
}