package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.*;
import vn.edu.hcmuaf.fit.demo1.model.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BookingService {

    private final BookingDao bookingDao = new BookingDao();
    private final ShowtimeDao showtimeDao = new ShowtimeDao();
    private final SeatDao seatDao = new SeatDao();
    private final RoomDao roomDao = new RoomDao();
    private final MovieDao movieDao = new MovieDao();

    // Map để lưu trữ reservation tạm thời (5 phút)
    private final Map<String, ReservationInfo> reservations = new ConcurrentHashMap<>();

    // Scheduler để cleanup reservation hết hạn
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public BookingService() {
        // Schedule cleanup mỗi 1 phút
        scheduler.scheduleAtFixedRate(this::cleanupExpiredReservations, 1, 1, TimeUnit.MINUTES);

        // Schedule notification cho reservation sắp hết hạn
        scheduler.scheduleAtFixedRate(this::notifyExpiringReservations, 30, 30, TimeUnit.SECONDS);
    }

    // Class để lưu thông tin reservation
    private static class ReservationInfo {
        String reservationId;
        int showtimeId;
        List<Integer> seatIds;
        List<String> seatCodes;
        int userId;
        LocalDateTime expiryTime;
        String sessionId;
        LocalDateTime createdAt;

        ReservationInfo(String reservationId, int showtimeId, List<Integer> seatIds,
                        List<String> seatCodes, int userId, String sessionId) {
            this.reservationId = reservationId;
            this.showtimeId = showtimeId;
            this.seatIds = seatIds;
            this.seatCodes = seatCodes;
            this.userId = userId;
            this.sessionId = sessionId;
            this.expiryTime = LocalDateTime.now().plusMinutes(5); // 5 phút
            this.createdAt = LocalDateTime.now();
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }

        long getRemainingSeconds() {
            return java.time.Duration.between(LocalDateTime.now(), expiryTime).getSeconds();
        }
    }

    // Giữ ghế tạm thời (5 phút)
    public Map<String, Object> reserveSeats(int showtimeId, List<String> seatCodes,
                                            int roomId, int userId, String sessionId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Kiểm tra thông tin showtime
            Showtime showtime = showtimeDao.getShowtimeById(showtimeId);
            if (showtime == null || !showtime.getIsActive()) {
                result.put("success", false);
                result.put("message", "Suất chiếu không tồn tại hoặc không hoạt động");
                return result;
            }

            // 2. Kiểm tra thời gian showtime
            LocalDateTime showDateTime = LocalDateTime.of(showtime.getShowDate(), showtime.getShowTime());
            if (showDateTime.isBefore(LocalDateTime.now())) {
                result.put("success", false);
                result.put("message", "Suất chiếu đã bắt đầu hoặc đã kết thúc");
                return result;
            }

            // 3. Chuyển seatCodes thành seatIds
            List<Integer> seatIds = new ArrayList<>();
            List<String> invalidSeats = new ArrayList<>();
            Map<String, Integer> seatCodeToIdMap = new HashMap<>();

            for (String seatCode : seatCodes) {
                Seat seat = seatDao.getSeatByCode(roomId, seatCode.trim());
                if (seat != null && seat.getIsActive()) {
                    seatIds.add(seat.getId());
                    seatCodeToIdMap.put(seatCode.trim(), seat.getId());
                } else {
                    invalidSeats.add(seatCode);
                }
            }

            if (!invalidSeats.isEmpty()) {
                result.put("success", false);
                result.put("message", "Ghế không hợp lệ: " + String.join(", ", invalidSeats));
                return result;
            }

            if (seatIds.isEmpty()) {
                result.put("success", false);
                result.put("message", "Không có ghế nào được chọn");
                return result;
            }

            // 4. Kiểm tra ghế có sẵn không
            List<String> unavailableSeats = seatDao.getUnavailableSeatCodes(showtimeId, seatIds);
            if (!unavailableSeats.isEmpty()) {
                result.put("success", false);
                result.put("message", "Ghế đã được đặt: " + String.join(", ", unavailableSeats));
                return result;
            }

            // 5. Giữ ghế trong database
            boolean reserved = bookingDao.reserveMultipleSeats(showtimeId, seatIds, userId);
            if (!reserved) {
                result.put("success", false);
                result.put("message", "Không thể giữ ghế. Vui lòng thử lại.");
                return result;
            }

            // 6. Tạo reservation ID
            String reservationId = bookingDao.createReservationId(showtimeId, userId);

            // 7. Lưu vào map
            ReservationInfo info = new ReservationInfo(reservationId, showtimeId, seatIds,
                    new ArrayList<>(seatCodes), userId, sessionId);
            reservations.put(reservationId, info);

            // 8. Lấy thông tin ghế
            List<Seat> seats = seatDao.getSeatsByRoom(roomId);
            Map<Integer, String> seatIdToCodeMap = new HashMap<>();
            for (Seat seat : seats) {
                seatIdToCodeMap.put(seat.getId(), seat.getSeatCode());
            }

            result.put("success", true);
            result.put("message", "Đã giữ ghế thành công. Bạn có 5 phút để hoàn tất.");
            result.put("reservationId", reservationId);
            result.put("expiryTime", info.expiryTime.toString());
            result.put("seatCodes", seatCodes);
            result.put("remainingSeconds", info.getRemainingSeconds());
            result.put("showtimeId", showtimeId);
            result.put("userId", userId);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống: " + e.getMessage());
        }

        return result;
    }

    // Xác nhận booking
    public Map<String, Object> confirmBooking(int showtimeId, List<String> seatCodes,
                                              int roomId, int userId, int orderId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Kiểm tra thông tin showtime
            Showtime showtime = showtimeDao.getShowtimeById(showtimeId);
            if (showtime == null) {
                result.put("success", false);
                result.put("message", "Suất chiếu không tồn tại");
                return result;
            }

            // 2. Chuyển seatCodes thành seatIds
            List<Integer> seatIds = new ArrayList<>();
            for (String seatCode : seatCodes) {
                Seat seat = seatDao.getSeatByCode(roomId, seatCode.trim());
                if (seat != null) {
                    seatIds.add(seat.getId());
                }
            }

            if (seatIds.isEmpty()) {
                result.put("success", false);
                result.put("message", "Không có ghế nào được chọn");
                return result;
            }

            // 3. Xác nhận booking trong database
            boolean confirmed = bookingDao.confirmBooking(showtimeId, seatIds, orderId, userId);

            if (confirmed) {
                result.put("success", true);
                result.put("message", "Đặt vé thành công!");
                result.put("orderId", orderId);
                result.put("seatCodes", seatCodes);
                result.put("showtimeId", showtimeId);

                // 4. Xóa reservation khỏi map
                String reservationIdToRemove = null;
                for (Map.Entry<String, ReservationInfo> entry : reservations.entrySet()) {
                    ReservationInfo info = entry.getValue();
                    if (info.showtimeId == showtimeId &&
                            info.userId == userId &&
                            info.seatCodes.containsAll(seatCodes)) {
                        reservationIdToRemove = entry.getKey();
                        break;
                    }
                }

                if (reservationIdToRemove != null) {
                    reservations.remove(reservationIdToRemove);
                }

                // 5. Gửi thông báo xác nhận (có thể gửi email/SMS)
                sendBookingConfirmation(userId, orderId, showtimeId, seatCodes);

            } else {
                result.put("success", false);
                result.put("message", "Không thể xác nhận booking. Vui lòng thử lại.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống: " + e.getMessage());
        }

        return result;
    }

    // Hủy reservation
    public Map<String, Object> cancelReservation(String reservationId, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            ReservationInfo info = reservations.get(reservationId);
            if (info != null && info.userId == userId) {
                // 1. Release ghế trong database
                bookingDao.releaseMultipleSeats(info.showtimeId, info.seatIds, userId);

                // 2. Xóa khỏi map
                reservations.remove(reservationId);

                result.put("success", true);
                result.put("message", "Đã hủy giữ ghế");
                result.put("reservationId", reservationId);
            } else {
                result.put("success", false);
                result.put("message", "Reservation không tồn tại hoặc không thuộc về bạn");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống");
        }

        return result;
    }

    // Kiểm tra trạng thái ghế
    public Map<String, Object> checkSeatStatus(int showtimeId, int roomId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Lấy tất cả ghế của phòng
            List<Seat> allSeats = seatDao.getSeatsByRoom(roomId);

            // 2. Lấy ghế đã được đặt/giữ
            List<BookedSeat> bookedSeats = bookingDao.getBookedSeatsForShowtime(showtimeId);

            // 3. Tạo map seatId -> status
            Map<Integer, String> seatStatusMap = new HashMap<>();
            for (BookedSeat bookedSeat : bookedSeats) {
                seatStatusMap.put(bookedSeat.getSeatId(), bookedSeat.getStatus());
            }

            // 4. Tạo response
            List<Map<String, Object>> seatsInfo = new ArrayList<>();
            for (Seat seat : allSeats) {
                Map<String, Object> seatInfo = new HashMap<>();
                seatInfo.put("seatId", seat.getId());
                seatInfo.put("seatCode", seat.getSeatCode());
                seatInfo.put("rowNumber", seat.getRowNumber());
                seatInfo.put("seatNumber", seat.getSeatNumber());
                seatInfo.put("seatType", seat.getSeatType());
                seatInfo.put("isActive", seat.getIsActive());

                String status = seatStatusMap.get(seat.getId());
                if (status == null) {
                    seatInfo.put("status", "available");
                } else if ("reserved".equals(status)) {
                    seatInfo.put("status", "reserved");
                } else {
                    seatInfo.put("status", "booked");
                }

                seatsInfo.add(seatInfo);
            }

            // 5. Lấy thông tin showtime
            Showtime showtime = showtimeDao.getShowtimeById(showtimeId);
            if (showtime != null) {
                result.put("showtime", showtime);
            }

            result.put("success", true);
            result.put("seats", seatsInfo);
            result.put("totalSeats", allSeats.size());
            result.put("availableSeats", allSeats.size() - bookedSeats.size());

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống: " + e.getMessage());
        }

        return result;
    }

    // Lấy thông tin reservation
    public Map<String, Object> getReservationInfo(String reservationId) {
        Map<String, Object> result = new HashMap<>();

        try {
            ReservationInfo info = reservations.get(reservationId);
            if (info != null) {
                result.put("success", true);
                result.put("reservationId", info.reservationId);
                result.put("showtimeId", info.showtimeId);
                result.put("seatCodes", info.seatCodes);
                result.put("seatIds", info.seatIds);
                result.put("userId", info.userId);
                result.put("expiryTime", info.expiryTime.toString());
                result.put("remainingSeconds", info.getRemainingSeconds());
                result.put("isExpired", info.isExpired());
                result.put("createdAt", info.createdAt.toString());
            } else {
                result.put("success", false);
                result.put("message", "Reservation không tồn tại");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống");
        }

        return result;
    }

    // Kiểm tra reservation còn hiệu lực không
    public boolean isReservationValid(String reservationId, int userId) {
        try {
            ReservationInfo info = reservations.get(reservationId);
            return info != null && info.userId == userId && !info.isExpired();
        } catch (Exception e) {
            return false;
        }
    }

    // Lấy tất cả reservation của user
    public List<ReservationInfo> getUserReservations(int userId) {
        List<ReservationInfo> userReservations = new ArrayList<>();
        for (ReservationInfo info : reservations.values()) {
            if (info.userId == userId && !info.isExpired()) {
                userReservations.add(info);
            }
        }
        return userReservations;
    }

    // Cleanup reservation hết hạn
    private void cleanupExpiredReservations() {
        try {
            // 1. Cleanup trong map
            List<String> expiredReservations = new ArrayList<>();

            for (Map.Entry<String, ReservationInfo> entry : reservations.entrySet()) {
                ReservationInfo info = entry.getValue();
                if (info.isExpired()) {
                    // Release ghế trong database
                    bookingDao.releaseMultipleSeats(info.showtimeId, info.seatIds, info.userId);
                    expiredReservations.add(entry.getKey());
                }
            }

            // Xóa khỏi map
            for (String reservationId : expiredReservations) {
                reservations.remove(reservationId);
            }

            // 2. Cleanup trong database
            bookingDao.cleanupExpiredReservations();

            // Log số lượng reservation đã cleanup
            if (!expiredReservations.isEmpty()) {
                System.out.println("Đã cleanup " + expiredReservations.size() + " reservation hết hạn");
            }

        } catch (Exception e) {
            System.err.println("Error in cleanupExpiredReservations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Thông báo reservation sắp hết hạn
    private void notifyExpiringReservations() {
        try {
            List<ReservationInfo> expiringReservations = new ArrayList<>();

            for (ReservationInfo info : reservations.values()) {
                long remainingSeconds = info.getRemainingSeconds();
                // Thông báo khi còn 1 phút
                if (remainingSeconds > 0 && remainingSeconds <= 60) {
                    expiringReservations.add(info);
                }
            }

            // Gửi thông báo (có thể gửi qua WebSocket hoặc lưu vào database)
            for (ReservationInfo info : expiringReservations) {
                sendExpirationWarning(info);
            }

        } catch (Exception e) {
            System.err.println("Error in notifyExpiringReservations: " + e.getMessage());
        }
    }

    // Phương thức helper
    private void sendBookingConfirmation(int userId, int orderId, int showtimeId, List<String> seatCodes) {
        // Implement gửi email/SMS thông báo
        System.out.println("Gửi thông báo xác nhận booking: Order #" + orderId +
                ", User: " + userId + ", Ghế: " + seatCodes);
    }

    private void sendExpirationWarning(ReservationInfo info) {
        // Implement gửi cảnh báo
        System.out.println("Cảnh báo: Reservation " + info.reservationId +
                " sẽ hết hạn trong " + info.getRemainingSeconds() + " giây");
    }

    // Lấy thông tin phòng và lịch chiếu
    public Map<String, Object> getRoomAndShowtimeInfo(int movieId, int roomId, String showtimeStr) {
        Map<String, Object> result = new HashMap<>();

        try {
            Movie movie = movieDao.getMovieById(movieId);
            Room room = roomDao.getRoomById(roomId);

            if (movie == null || room == null) {
                result.put("success", false);
                result.put("message", "Thông tin không hợp lệ");
                return result;
            }

            // Parse showtime
            String[] parts = showtimeStr.split("T");
            if (parts.length != 2) {
                result.put("success", false);
                result.put("message", "Định dạng thời gian không hợp lệ");
                return result;
            }

            java.time.LocalDate showDate = java.time.LocalDate.parse(parts[0]);
            java.time.LocalTime showTime = java.time.LocalTime.parse(parts[1]);

            // Tìm hoặc tạo showtime
            Integer showtimeId = showtimeDao.createShowtime(movieId, roomId, showDate, showTime);

            if (showtimeId == null || showtimeId <= 0) {
                result.put("success", false);
                result.put("message", "Không thể tạo suất chiếu");
                return result;
            }

            Showtime showtime = showtimeDao.getShowtimeById(showtimeId);

            result.put("success", true);
            result.put("movie", movie);
            result.put("room", room);
            result.put("showtime", showtime);
            result.put("showtimeId", showtimeId);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống: " + e.getMessage());
        }

        return result;
    }

    // Release seat theo reservation ID
    public boolean releaseSeatsByReservationId(String reservationId) {
        try {
            ReservationInfo info = reservations.get(reservationId);
            if (info != null) {
                bookingDao.releaseMultipleSeats(info.showtimeId, info.seatIds, info.userId);
                reservations.remove(reservationId);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Shutdown scheduler
    public void shutdown() {
        try {
            scheduler.shutdown();
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // Lấy số lượng reservation đang active
    public int getActiveReservationCount() {
        return reservations.size();
    }

    // Lấy tất cả reservation đang active (cho admin)
    public Map<String, ReservationInfo> getAllActiveReservations() {
        return new HashMap<>(reservations);
    }
}