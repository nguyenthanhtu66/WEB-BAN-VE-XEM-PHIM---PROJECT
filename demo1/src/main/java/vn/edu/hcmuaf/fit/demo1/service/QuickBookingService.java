package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.*;
import vn.edu.hcmuaf.fit.demo1.model.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class QuickBookingService {

    private final MovieDao movieDao = new MovieDao();
    private final RoomDao roomDao = new RoomDao();
    private final ShowtimeDao showtimeDao = new ShowtimeDao();
    private final ShowtimeService showtimeService = new ShowtimeService();

    public Map<String, Object> processQuickBooking(int movieId, int roomId, String showtimeStr) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Kiểm tra phim có đang chiếu không
            Movie movie = movieDao.getMovieById(movieId);
            if (movie == null || !"showing".equals(movie.getStatus())) {
                result.put("success", false);
                result.put("message", "Phim không tồn tại hoặc không đang chiếu");
                return result;
            }

            // 2. Kiểm tra phòng
            Room room = roomDao.getRoomById(roomId);
            if (room == null || !room.getIsActive()) {
                result.put("success", false);
                result.put("message", "Phòng chiếu không tồn tại hoặc không hoạt động");
                return result;
            }

            // 3. Parse thời gian
            String[] parts = showtimeStr.split("T");
            if (parts.length != 2) {
                result.put("success", false);
                result.put("message", "Định dạng thời gian không hợp lệ");
                return result;
            }

            LocalDate showDate = LocalDate.parse(parts[0]);
            LocalTime showTime = LocalTime.parse(parts[1]);
            LocalDateTime showDateTime = LocalDateTime.of(showDate, showTime);

            // 4. Kiểm tra thời gian không ở quá khứ
            if (showDateTime.isBefore(LocalDateTime.now())) {
                result.put("success", false);
                result.put("message", "Thời gian chiếu không hợp lệ");
                return result;
            }

            // 5. Kiểm tra phòng có trùng lịch không
            if (!showtimeDao.isRoomAvailable(roomId, showDate, showTime)) {
                result.put("success", false);
                result.put("message", "Phòng đã có suất chiếu vào thời gian này");
                return result;
            }

            // 6. Tạo hoặc lấy showtime
            Integer showtimeId = showtimeDao.createShowtime(movieId, roomId, showDate, showTime);

            if (showtimeId == null || showtimeId <= 0) {
                result.put("success", false);
                result.put("message", "Không thể tạo suất chiếu");
                return result;
            }

            // 7. Lấy thông tin showtime
            Showtime showtime = showtimeDao.getShowtimeById(showtimeId);

            result.put("success", true);
            result.put("showtimeId", showtimeId);
            result.put("showtime", showtime);
            result.put("movie", movie);
            result.put("room", room);
            result.put("message", "Sẵn sàng đặt vé");

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống: " + e.getMessage());
        }

        return result;
    }

    public Map<String, Object> getAvailableRoomsForMovie(int movieId) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Room> allRooms = roomDao.getAllActiveRooms();
            List<Room> availableRooms = new ArrayList<>();

            for (Room room : allRooms) {
                // Kiểm tra room có thể chiếu phim này không
                // (Có thể thêm logic kiểm tra phòng phù hợp với định dạng phim)
                availableRooms.add(room);
            }

            result.put("success", true);
            result.put("rooms", availableRooms);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống");
        }

        return result;
    }

    public Map<String, Object> getSuggestedShowtimes(int movieId, int roomId) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Map<String, Object>> suggestedTimes = new ArrayList<>();
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);

            // Tạo các suất chiếu gợi ý: 10:00, 13:00, 16:00, 19:00, 22:00
            LocalTime[] suggestedHours = {
                    LocalTime.of(10, 0),
                    LocalTime.of(13, 0),
                    LocalTime.of(16, 0),
                    LocalTime.of(19, 0),
                    LocalTime.of(22, 0)
            };

            for (LocalTime time : suggestedHours) {
                LocalDateTime dateTime = LocalDateTime.of(tomorrow, time);

                // Kiểm tra phòng có trống không
                if (showtimeDao.isRoomAvailable(roomId, tomorrow, time)) {
                    Map<String, Object> timeInfo = new HashMap<>();
                    timeInfo.put("date", tomorrow.toString());
                    timeInfo.put("time", time.toString());
                    timeInfo.put("datetime", dateTime.toString());
                    timeInfo.put("formatted", tomorrow.format(DateTimeFormatter.ofPattern("dd/MM")) +
                            " - " + time.format(DateTimeFormatter.ofPattern("HH:mm")));
                    suggestedTimes.add(timeInfo);
                }
            }

            result.put("success", true);
            result.put("suggestedTimes", suggestedTimes);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống");
        }

        return result;
    }
}