package vn.edu.hcmuaf.fit.demo1.model;

public enum SeatStatus {
    AVAILABLE("Ghế trống"),
    RESERVED("Đang giữ tạm"),
    BOOKED("Đã đặt"),
    RELEASED("Đã hủy giữ");

    private final String description;

    SeatStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}