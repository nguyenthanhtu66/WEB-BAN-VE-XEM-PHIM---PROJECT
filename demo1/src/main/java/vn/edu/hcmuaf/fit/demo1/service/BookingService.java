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

    // Map để lưu trữ reservation tạm thời (nếu không muốn lưu DB)
    private final Map<String, ReservationInfo> reservations = new ConcurrentHashMap<>();

    // Scheduler để cleanup reservation hết hạn
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public BookingService() {
        // Schedule cleanup mỗi 1 phút
        scheduler.scheduleAtFixedRate(this::cleanupExpiredReservations, 1, 1, TimeUnit.MINUTES);
    }

    // Class để lưu thông tin reservation
    private static class ReservationInfo {
        String reservationId;
        int showtimeId;
        List<Integer> seatIds;
        int userId;
        LocalDateTime expiryTime;
        String sessionId;

        ReservationInfo(String reservationId, int showtimeId, List<Integer> seatIds,
                        int userId, String sessionId) {
            this.reservationId = reservationId;
            this.showtimeId = showtimeId;
            this.seatIds = seatIds;
            this.userId = userId;
            this.sessionId = sessionId;
            this.expiryTime = LocalDateTime.now().plusMinutes(5); // 5 phút
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }
    }

    // Giữ ghế tạm thời
    public Map<String, Object> reserveSeats(int showtimeId, List<String> seatCodes,
                                            int roomId, int userId, String sessionId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Chuyển seatCodes thành seatIds
            List<Integer> seatIds = new ArrayList<>();
            List<String> invalidSeats = new ArrayList<>();

            for (String seatCode : seatCodes) {
                Seat seat = seatDao.getSeatByCode(roomId, seatCode.trim());
                if (seat != null) {
                    seatIds.add(seat.getId());
                } else {
                    invalidSeats.add(seatCode);
                }
            }

            if (!invalidSeats.isEmpty()) {
                result.put("success", false);
                result.put("message", "Ghế không hợp lệ: " + String.join(", ", invalidSeats));
                return result;
            }

            // 2. Kiểm tra ghế có sẵn không
            List<String> unavailableSeats = seatDao.getUnavailableSeatCodes(showtimeId, seatIds);
            if (!unavailableSeats.isEmpty()) {
                result.put("success", false);
                result.put("message", "Ghế đã được đặt: " + String.join(", ", unavailableSeats));
                return result;
            }

            // 3. Giữ ghế trong database
            boolean reserved = bookingDao.reserveMultipleSeats(showtimeId, seatIds, userId);
            if (!reserved) {
                result.put("success", false);
                result.put("message", "Không thể giữ ghế. Vui lòng thử lại.");
                return result;
            }

            // 4. Tạo reservation ID
            String reservationId = "RES-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);

            // 5. Lưu vào map
            ReservationInfo info = new ReservationInfo(reservationId, showtimeId, seatIds, userId, sessionId);
            reservations.put(reservationId, info);

            result.put("success", true);
            result.put("message", "Đã giữ ghế thành công. Bạn có 5 phút để hoàn tất.");
            result.put("reservationId", reservationId);
            result.put("expiryTime", info.expiryTime.toString());
            result.put("seatCodes", seatCodes);

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
            // Chuyển seatCodes thành seatIds
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

            // Xác nhận booking trong database
            boolean confirmed = bookingDao.confirmBooking(showtimeId, seatIds, orderId, userId);

            if (confirmed) {
                result.put("success", true);
                result.put("message", "Đặt vé thành công!");
                result.put("orderId", orderId);

                // Xóa reservation khỏi map
                reservations.values().removeIf(info ->
                        info.showtimeId == showtimeId &&
                                info.userId == userId &&
                                info.seatIds.containsAll(seatIds));
            } else {
                result.put("success", false);
                result.put("message", "Không thể xác nhận booking");
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
                // Release ghế trong database
                bookingDao.releaseMultipleSeats(info.showtimeId, info.seatIds, userId);

                // Xóa khỏi map
                reservations.remove(reservationId);

                result.put("success", true);
                result.put("message", "Đã hủy giữ ghế");
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
            // Lấy tất cả ghế của phòng
            List<Seat> allSeats = seatDao.getSeatsByRoom(roomId);

            // Lấy ghế đã được đặt/giữ
            List<BookedSeat> bookedSeats = bookingDao.getBookedSeatsForShowtime(showtimeId);

            // Tạo map seatId -> status
            Map<Integer, String> seatStatusMap = new HashMap<>();
            for (BookedSeat bookedSeat : bookedSeats) {
                seatStatusMap.put(bookedSeat.getSeatId(), bookedSeat.getStatus());
            }

            // Tạo response
            List<Map<String, Object>> seatsInfo = new ArrayList<>();
            for (Seat seat : allSeats) {
                Map<String, Object> seatInfo = new HashMap<>();
                seatInfo.put("seatCode", seat.getSeatCode());
                seatInfo.put("rowNumber", seat.getRowNumber());
                seatInfo.put("seatNumber", seat.getSeatNumber());
                seatInfo.put("seatType", seat.getSeatType());

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

            result.put("success", true);
            result.put("seats", seatsInfo);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống");
        }

        return result;
    }

    // Cleanup reservation hết hạn
    private void cleanupExpiredReservations() {
        try {
            // Cleanup trong map
            LocalDateTime now = LocalDateTime.now();
            reservations.entrySet().removeIf(entry -> {
                if (entry.getValue().isExpired()) {
                    // Release ghế trong database
                    bookingDao.releaseMultipleSeats(
                            entry.getValue().showtimeId,
                            entry.getValue().seatIds,
                            entry.getValue().userId
                    );
                    return true;
                }
                return false;
            });

            // Cleanup trong database
            bookingDao.cleanupExpiredReservations();

        } catch (Exception e) {
            e.printStackTrace();
        }
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