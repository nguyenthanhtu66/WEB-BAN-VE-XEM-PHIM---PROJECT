package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.BookingDao;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SeatReservationService {

    private final BookingDao bookingDao = new BookingDao();
    private final Map<String, ReservationInfo> reservations = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public SeatReservationService() {
        // Cleanup mỗi 1 phút
        scheduler.scheduleAtFixedRate(this::cleanupExpiredReservations, 1, 1, TimeUnit.MINUTES);
    }

    // Class lưu thông tin reservation
    public static class ReservationInfo {
        private String reservationId;
        private int showtimeId;
        private List<Integer> seatIds;
        private int userId;
        private LocalDateTime expiryTime;
        private String sessionId;

        public ReservationInfo(String reservationId, int showtimeId, List<Integer> seatIds,
                               int userId, String sessionId) {
            this.reservationId = reservationId;
            this.showtimeId = showtimeId;
            this.seatIds = seatIds;
            this.userId = userId;
            this.sessionId = sessionId;
            this.expiryTime = LocalDateTime.now().plusMinutes(5);
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }

        // Getters
        public String getReservationId() { return reservationId; }
        public int getShowtimeId() { return showtimeId; }
        public List<Integer> getSeatIds() { return seatIds; }
        public int getUserId() { return userId; }
        public LocalDateTime getExpiryTime() { return expiryTime; }
        public String getSessionId() { return sessionId; }
    }

    // Tạo reservation mới
    public String createReservation(int showtimeId, List<Integer> seatIds, int userId, String sessionId) {
        // Kiểm tra ghế có sẵn không
        for (int seatId : seatIds) {
            if (!isSeatAvailable(showtimeId, seatId)) {
                return null;
            }
        }

        // Giữ ghế trong database
        boolean reserved = bookingDao.reserveMultipleSeats(showtimeId, seatIds, userId);
        if (!reserved) {
            return null;
        }

        // Tạo reservation ID
        String reservationId = "RES-" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 8);

        // Lưu vào map
        ReservationInfo info = new ReservationInfo(reservationId, showtimeId, seatIds, userId, sessionId);
        reservations.put(reservationId, info);

        return reservationId;
    }

    // Kiểm tra ghế có sẵn không
    private boolean isSeatAvailable(int showtimeId, int seatId) {
        // Có thể kiểm tra trong database
        return true; // Tạm thời luôn trả về true
    }

    // Hủy reservation
    public boolean cancelReservation(String reservationId) {
        ReservationInfo info = reservations.get(reservationId);
        if (info != null) {
            // Release ghế trong database
            bookingDao.releaseMultipleSeats(info.getShowtimeId(), info.getSeatIds(), info.getUserId());

            // Xóa khỏi map
            reservations.remove(reservationId);
            return true;
        }
        return false;
    }

    // Cleanup reservation hết hạn
    private void cleanupExpiredReservations() {
        // Cleanup trong map
        LocalDateTime now = LocalDateTime.now();
        reservations.entrySet().removeIf(entry -> {
            if (entry.getValue().isExpired()) {
                // Release ghế trong database
                bookingDao.releaseMultipleSeats(
                        entry.getValue().getShowtimeId(),
                        entry.getValue().getSeatIds(),
                        entry.getValue().getUserId()
                );
                return true;
            }
            return false;
        });

        // Cleanup trong database
        bookingDao.cleanupExpiredReservations();
    }

    // Kiểm tra reservation còn hiệu lực không
    public boolean isReservationValid(String reservationId) {
        ReservationInfo info = reservations.get(reservationId);
        return info != null && !info.isExpired();
    }

    // Lấy thông tin reservation
    public ReservationInfo getReservationInfo(String reservationId) {
        return reservations.get(reservationId);
    }

    // Shutdown scheduler
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}