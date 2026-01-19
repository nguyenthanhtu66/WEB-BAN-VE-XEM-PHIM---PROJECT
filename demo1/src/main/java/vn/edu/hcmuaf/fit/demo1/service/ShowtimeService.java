package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.*;
import vn.edu.hcmuaf.fit.demo1.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ShowtimeService {

    private final ShowtimeDao showtimeDao = new ShowtimeDao();
    private final MovieDao movieDao = new MovieDao();
    private final RoomDao roomDao = new RoomDao();

    // Lấy suất chiếu theo ID
    public Showtime getShowtimeById(int showtimeId) {
        return showtimeDao.getShowtimeById(showtimeId);
    }

    // Lấy suất chiếu theo phim
    public List<Showtime> getShowtimesByMovie(int movieId) {
        return showtimeDao.getShowtimesByMovie(movieId);
    }

    // Lấy suất chiếu theo ngày
    public List<Showtime> getShowtimesByDate(LocalDate date) {
        return showtimeDao.getShowtimesByDate(date);
    }

    // Lấy suất chiếu theo phim và ngày
    public List<Showtime> getShowtimesByMovieAndDate(int movieId, LocalDate date) {
        return showtimeDao.getShowtimesByMovieAndDate(movieId, date);
    }

    // Lấy thông tin chi tiết suất chiếu
    public Map<String, Object> getShowtimeDetails(int showtimeId) {
        Showtime showtime = showtimeDao.getShowtimeById(showtimeId);
        if (showtime == null) {
            return null;
        }

        Movie movie = movieDao.getMovieById(showtime.getMovieId());
        Room room = roomDao.getRoomById(showtime.getRoomId());

        Map<String, Object> details = new HashMap<>();
        details.put("showtime", showtime);
        details.put("movie", movie);
        details.put("room", room);

        // Tính toán thời gian còn lại
        if (showtime.getShowDate() != null && showtime.getShowTime() != null) {
            LocalDate showDate = showtime.getShowDate();
            LocalTime showTime = showtime.getShowTime();
            // ... tính toán thời gian còn lại ...
        }

        return details;
    }

    // Lấy tên phòng
    public String getRoomName(int roomId) {
        Room room = roomDao.getRoomById(roomId);
        return room != null ? room.getRoomName() : "Unknown";
    }

    // Lấy thông tin phòng
    public Room getRoomInfo(int roomId) {
        return roomDao.getRoomById(roomId);
    }

    // Kiểm tra suất chiếu có hợp lệ không
    public boolean isValidShowtime(int showtimeId) {
        Showtime showtime = showtimeDao.getShowtimeById(showtimeId);
        if (showtime == null || !showtime.isActive()) {
            return false;
        }

        // Kiểm tra thời gian (không cho đặt vé quá muộn)
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (showtime.getShowDate().isBefore(today)) {
            return false;
        }

        if (showtime.getShowDate().equals(today)) {
            // Không cho đặt vé trước giờ chiếu 30 phút
            if (showtime.getShowTime().minusMinutes(30).isBefore(now)) {
                return false;
            }
        }

        return true;
    }

    // Lấy suất chiếu sắp tới
    public List<Map<String, Object>> getUpcomingShowtimes(int limit) {
        List<Showtime> showtimes = showtimeDao.getUpcomingShowtimes(limit);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Showtime showtime : showtimes) {
            Map<String, Object> info = new HashMap<>();
            info.put("showtime", showtime);

            Movie movie = movieDao.getMovieById(showtime.getMovieId());
            info.put("movie", movie);

            Room room = roomDao.getRoomById(showtime.getRoomId());
            info.put("room", room);

            result.add(info);
        }

        return result;
    }

    // Lấy suất chiếu theo rạp
    public List<Showtime> getShowtimesByCinema(String cinemaCode) {
        // Giả sử room có thuộc tính cinema
        List<Room> rooms = roomDao.getRoomsByCinema(cinemaCode);
        List<Showtime> allShowtimes = new ArrayList<>();

        for (Room room : rooms) {
            List<Showtime> roomShowtimes = showtimeDao.getShowtimesByRoom(room.getId());
            allShowtimes.addAll(roomShowtimes);
        }

        // Sắp xếp theo thời gian
        allShowtimes.sort((s1, s2) -> {
            int dateCompare = s1.getShowDate().compareTo(s2.getShowDate());
            if (dateCompare != 0) return dateCompare;
            return s1.getShowTime().compareTo(s2.getShowTime());
        });

        return allShowtimes;
    }

    // Kiểm tra xem có suất chiếu nào trùng không
    public boolean hasTimeConflict(int roomId, LocalDate date, LocalTime time, int duration) {
        return showtimeDao.hasTimeConflict(roomId, date, time, duration);
    }
}