package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;
import java.util.*;

public class Cart implements Serializable {
    private Map<String, CartItem> items;
    private double subtotal;
    private double serviceFee;
    private double discount;
    private double grandTotal;
    private String promoCode;
    private String promoMessage;

    public Cart() {
        this.items = new LinkedHashMap<>();
        this.subtotal = 0;
        this.serviceFee = 0;
        this.discount = 0;
        this.grandTotal = 0;
        this.promoCode = null;
        this.promoMessage = null;
    }

    // Thêm item vào giỏ hàng
    public void addItem(CartItem item) {
        // Tạo key duy nhất cho item
        String key = generateItemKey(item);

        if (items.containsKey(key)) {
            // Nếu đã có item tương tự, cập nhật số lượng
            CartItem existingItem = items.get(key);
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());

            // Cập nhật ghế nếu có
            if (item.getSeats() != null && !item.getSeats().isEmpty()) {
                String newSeats = existingItem.getSeats();
                if (newSeats == null || newSeats.isEmpty()) {
                    newSeats = item.getSeats();
                } else {
                    newSeats += ", " + item.getSeats();
                }
                existingItem.setSeats(newSeats);
            }
        } else {
            items.put(key, item);
        }

        calculateTotals();
    }

    // Xóa item khỏi giỏ hàng
    public void removeItem(String itemId) {
        items.values().removeIf(item -> item.getId().equals(itemId));
        calculateTotals();
    }

    // Cập nhật số lượng item
    public void updateQuantity(String itemId, int quantity) {
        for (CartItem item : items.values()) {
            if (item.getId().equals(itemId)) {
                item.setQuantity(Math.max(1, quantity));
                break;
            }
        }
        calculateTotals();
    }

    // Cập nhật ghế của item
    public void updateSeats(String itemId, String seats) {
        for (CartItem item : items.values()) {
            if (item.getId().equals(itemId)) {
                item.setSeats(seats);
                // Cập nhật quantity theo số ghế
                if (seats != null && !seats.isEmpty()) {
                    String[] seatArray = seats.split(", ");
                    item.setQuantity(seatArray.length);
                }
                break;
            }
        }
        calculateTotals();
    }

    // Lấy item theo ID
    public CartItem getItemById(String itemId) {
        return items.values().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    // Xóa toàn bộ giỏ hàng
    public void clear() {
        items.clear();
        subtotal = 0;
        serviceFee = 0;
        discount = 0;
        grandTotal = 0;
        promoCode = null;
        promoMessage = null;
    }

    // Tính toán tổng tiền
    public void calculateTotals() {
        subtotal = 0;
        for (CartItem item : items.values()) {
            subtotal += item.getTotal();
        }

        // Phí dịch vụ 5%
        serviceFee = subtotal * 0.05;

        // Giảm giá nếu có mã khuyến mãi
        discount = calculateDiscount();

        // Tổng cộng
        grandTotal = subtotal + serviceFee - discount;
    }

    // Tính discount
    private double calculateDiscount() {
        if (promoCode == null || promoCode.isEmpty()) {
            return 0;
        }

        // Logic tính discount dựa trên promoCode
        // Ở đây giả sử discount 10% cho mã "WELCOME10"
        if ("WELCOME10".equalsIgnoreCase(promoCode)) {
            return subtotal * 0.10;
        } else if ("SUMMER20".equalsIgnoreCase(promoCode)) {
            return subtotal * 0.20;
        }

        return 0;
    }

    // Áp dụng mã khuyến mãi
    public boolean applyPromoCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            promoMessage = "Vui lòng nhập mã khuyến mãi";
            return false;
        }

        code = code.trim().toUpperCase();

        // Kiểm tra mã hợp lệ
        if (!isValidPromoCode(code)) {
            promoMessage = "Mã khuyến mãi không hợp lệ";
            return false;
        }

        // Kiểm tra điều kiện áp dụng
        if (subtotal < 100000) {
            promoMessage = "Mã khuyến mãi chỉ áp dụng cho đơn hàng từ 100,000đ";
            return false;
        }

        promoCode = code;
        promoMessage = "Áp dụng mã khuyến mãi thành công";
        calculateTotals();
        return true;
    }

    private boolean isValidPromoCode(String code) {
        // Kiểm tra trong database hoặc danh sách mã cố định
        List<String> validCodes = Arrays.asList("WELCOME10", "SUMMER20", "MOVIE15");
        return validCodes.contains(code);
    }

    // Tạo key duy nhất cho item
    private String generateItemKey(CartItem item) {
        return String.format("%d_%d_%d_%s",
                item.getMovieId(),
                item.getShowtimeId(),
                item.getRoomId(),
                item.getTicketType());
    }

    // ==================== GETTERS & SETTERS ====================

    public List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public void setItems(List<CartItem> itemsList) {
        this.items.clear();
        for (CartItem item : itemsList) {
            this.items.put(generateItemKey(item), item);
        }
        calculateTotals();
    }

    public int getTotalItems() {
        return items.values().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public int getItemCount() {
        return items.size();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
        calculateTotals();
    }

    public double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
        calculateTotals();
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

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
        calculateTotals();
    }

    public String getPromoMessage() {
        return promoMessage;
    }

    public void setPromoMessage(String promoMessage) {
        this.promoMessage = promoMessage;
    }

    // ==================== HELPER METHODS ====================

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean containsMovie(int movieId) {
        return items.values().stream()
                .anyMatch(item -> item.getMovieId() == movieId);
    }

    public String getSummary() {
        return String.format("Giỏ hàng có %d vé, tổng tiền: %,.0fđ",
                getTotalItems(), grandTotal);
    }

    @Override
    public String toString() {
        return String.format("Cart{items=%d, total=%,.0fđ}",
                getTotalItems(), grandTotal);
    }
}