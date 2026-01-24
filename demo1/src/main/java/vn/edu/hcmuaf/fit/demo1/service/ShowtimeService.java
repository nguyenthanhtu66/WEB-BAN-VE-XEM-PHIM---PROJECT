package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.ShowtimeDao;
import vn.edu.hcmuaf.fit.demo1.model.Showtime;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ShowtimeService {

    private final ShowtimeDao showtimeDao = new ShowtimeDao();

    // Tìm showtime
    public Showtime findShowtime(int movieId, int roomId, LocalDateTime dateTime) {
        return showtimeDao.findShowtime(movieId, roomId, dateTime);
    }

    // Tìm hoặc tạo showtime
    public Showtime findOrCreateShowtime(int movieId, int roomId, LocalDateTime dateTime) {
        return showtimeDao.findOrCreateShowtime(movieId, roomId, dateTime);
    }

    // Kiểm tra phòng có sẵn không
    public boolean isRoomAvailable(int roomId, LocalDate date, LocalTime time) {
        return showtimeDao.isRoomAvailable(roomId, date, time);
    }

    // Lấy showtime theo ID
    public Showtime getShowtimeById(int showtimeId) {
        return showtimeDao.getShowtimeById(showtimeId);
    }

    // Lấy tất cả showtime theo phim
    public List<Showtime> getShowtimesByMovie(int movieId) {
        return showtimeDao.getShowtimesByMovie(movieId);
    }

    // Lấy showtime theo phim và ngày
    public List<Showtime> getShowtimesByMovieAndDate(int movieId, LocalDate date) {
        return showtimeDao.getShowtimesByMovieAndDate(movieId, date);
    }

    // Tạo showtime mới
    public Integer createShowtime(int movieId, int roomId, LocalDate showDate, LocalTime showTime) {
        return showtimeDao.createShowtime(movieId, roomId, showDate, showTime);
    }

    // Kiểm tra thời gian hợp lệ
    public boolean isValidShowtime(LocalDateTime showtime) {
        // Không được trong quá khứ
        if (showtime.isBefore(LocalDateTime.now())) {
            return false;
        }

        // Không được quá 30 ngày trong tương lai
        if (showtime.isAfter(LocalDateTime.now().plusDays(30))) {
            return false;
        }

        return true;
    }

    // Format showtime cho display
    public String formatShowtime(Showtime showtime) {
        if (showtime == null) return "";

        LocalDate date = showtime.getShowDate();
        LocalTime time = showtime.getShowTime();

        if (date == null || time == null) return "";

        return String.format("%s %s",
                date.toString(),
                time.toString().substring(0, 5));
    }
}