package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.ShowtimeDao;
import vn.edu.hcmuaf.fit.demo1.model.Showtime;

import java.time.LocalDateTime;
import java.util.List;

public class ShowtimeService {

    private final ShowtimeDao showtimeDao = new ShowtimeDao();

    public Showtime getShowtimeById(int id) {
        return showtimeDao.getShowtimeById(id);
    }

    public List<Showtime> getShowtimesByMovie(int movieId) {
        return showtimeDao.getShowtimesByMovie(movieId);
    }

    public Showtime findOrCreateShowtime(int movieId, int roomId, String showtimeStr) {
        // Phân tích chuỗi thời gian
        LocalDateTime showDateTime = parseShowtimeString(showtimeStr);

        // Tìm suất chiếu hiện có
        Showtime existing = showtimeDao.findShowtime(movieId, roomId, showDateTime);
        if (existing != null) {
            return existing;
        }

        // Tạo suất chiếu mới
        Showtime newShowtime = new Showtime();
        newShowtime.setMovieId(movieId);
        newShowtime.setRoomId(roomId);
        newShowtime.setShowDate(showDateTime.toLocalDate());
        newShowtime.setShowTime(showDateTime.toLocalTime());
        newShowtime.setActive(true);

        if (showtimeDao.createShowtime(newShowtime)) {
            return showtimeDao.findShowtime(movieId, roomId, showDateTime);
        }

        return null;
    }

    private LocalDateTime parseShowtimeString(String showtimeStr) {
        // Có thể cần cài đặt parsing cụ thể
        // Tạm thời trả về thời gian hiện tại + 2 giờ
        return LocalDateTime.now().plusHours(2);
    }
}