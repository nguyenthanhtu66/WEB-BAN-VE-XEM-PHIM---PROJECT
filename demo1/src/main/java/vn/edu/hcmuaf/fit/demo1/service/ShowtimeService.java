package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.*;
import vn.edu.hcmuaf.fit.demo1.model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ShowtimeService {

    private final ShowtimeDao showtimeDao = new ShowtimeDao();
    private final MovieDao movieDao = new MovieDao();
    private final RoomDao roomDao = new RoomDao();

    public Map<String, Object> getAvailableShowtimes(int movieId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Lấy thông tin phim
            Movie movie = movieDao.getMovieById(movieId);
            if (movie == null) {
                result.put("success", false);
                result.put("message", "Phim không tồn tại");
                return result;
            }

            // Lấy tất cả showtimes cho phim này
            List<Showtime> showtimes = showtimeDao.getShowtimesByMovie(movieId);

            // Nhóm theo ngày
            Map<LocalDate, List<Map<String, Object>>> showtimesByDate = new TreeMap<>();

            for (Showtime showtime : showtimes) {
                if (!showtime.getIsActive()) continue;

                // Kiểm tra showtime không ở quá khứ
                LocalDateTime showDateTime = LocalDateTime.of(
                        showtime.getShowDate(),
                        showtime.getShowTime()
                );

                if (showDateTime.isBefore(LocalDateTime.now())) {
                    continue;
                }

                // Lấy thông tin phòng
                Room room = roomDao.getRoomById(showtime.getRoomId());

                Map<String, Object> showtimeInfo = new HashMap<>();
                showtimeInfo.put("id", showtime.getId());
                showtimeInfo.put("showDate", showtime.getShowDate());
                showtimeInfo.put("showTime", showtime.getShowTime());
                showtimeInfo.put("roomId", showtime.getRoomId());
                showtimeInfo.put("roomName", room != null ? room.getRoomName() : "");
                showtimeInfo.put("roomType", room != null ? room.getRoomType() : "");
                showtimeInfo.put("formattedTime", formatTime(showtime.getShowTime()));

                // Nhóm theo ngày
                LocalDate showDate = showtime.getShowDate();
                showtimesByDate.computeIfAbsent(showDate, k -> new ArrayList<>()).add(showtimeInfo);
            }

            // Chuyển thành danh sách theo thứ tự ngày
            List<Map<String, Object>> groupedShowtimes = new ArrayList<>();
            for (Map.Entry<LocalDate, List<Map<String, Object>>> entry : showtimesByDate.entrySet()) {
                Map<String, Object> dayGroup = new HashMap<>();
                dayGroup.put("date", entry.getKey());
                dayGroup.put("dateFormatted", formatDate(entry.getKey()));
                dayGroup.put("showtimes", entry.getValue());
                groupedShowtimes.add(dayGroup);
            }

            result.put("success", true);
            result.put("movie", movie);
            result.put("showtimes", groupedShowtimes);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống: " + e.getMessage());
        }

        return result;
    }

    public Map<String, Object> createOrGetShowtime(int movieId, int roomId, String dateTimeStr) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Parse datetime string
            String[] parts = dateTimeStr.split("T");
            LocalDate showDate = LocalDate.parse(parts[0]);
            LocalTime showTime = LocalTime.parse(parts[1]);
            LocalDateTime dateTime = LocalDateTime.of(showDate, showTime);

            // Kiểm tra thời gian không ở quá khứ
            if (dateTime.isBefore(LocalDateTime.now())) {
                result.put("success", false);
                result.put("message", "Thời gian chiếu không hợp lệ");
                return result;
            }

            // Tìm showtime đã tồn tại
            Showtime existing = showtimeDao.findShowtime(movieId, roomId, dateTime);

            if (existing != null) {
                // Showtime đã tồn tại
                result.put("success", true);
                result.put("showtimeId", existing.getId());
                result.put("isNew", false);
                return result;
            }

            // Kiểm tra phòng có trùng lịch không
            if (!showtimeDao.isRoomAvailable(roomId, showDate, showTime)) {
                result.put("success", false);
                result.put("message", "Phòng đã có suất chiếu vào thời gian này");
                return result;
            }

            // Tạo showtime mới
            Integer newShowtimeId = showtimeDao.createShowtime(movieId, roomId, showDate, showTime);

            if (newShowtimeId != null && newShowtimeId > 0) {
                result.put("success", true);
                result.put("showtimeId", newShowtimeId);
                result.put("isNew", true);
            } else {
                result.put("success", false);
                result.put("message", "Không thể tạo suất chiếu");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi hệ thống: " + e.getMessage());
        }

        return result;
    }

    private String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}