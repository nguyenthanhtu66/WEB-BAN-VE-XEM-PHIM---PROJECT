package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.*;
import vn.edu.hcmuaf.fit.demo1.model.*;

import java.util.*;

public class BookingService {

    private final SeatDao seatDao = new SeatDao();
    private final BookedSeatDao bookedSeatDao = new BookedSeatDao();
    private final ShowtimeDao showtimeDao = new ShowtimeDao();
    private final RoomDao roomDao = new RoomDao();
    private final MovieDao movieDao = new MovieDao();

    // Lấy danh sách ghế còn trống
    public List<Map<String, Object>> getAvailableSeats(int showtimeId, int roomId) {
        List<Seat> allSeats = seatDao.getSeatsByRoom(roomId);
        List<Map<String, Object>> availableSeats = new ArrayList<>();

        for (Seat seat : allSeats) {
            boolean isAvailable = seatDao.isSeatAvailable(seat.getId(), showtimeId);

            Map<String, Object> seatInfo = new HashMap<>();
            seatInfo.put("seatId", seat.getId());
            seatInfo.put("seatCode", seat.getSeatCode());
            seatInfo.put("rowNumber", seat.getRowNumber());
            seatInfo.put("seatNumber", seat.getSeatNumber());
            seatInfo.put("seatType", seat.getSeatType());
            seatInfo.put("isAvailable", isAvailable);
            seatInfo.put("price", calculateSeatPrice(seat.getSeatType()));

            availableSeats.add(seatInfo);
        }

        return availableSeats;
    }

    // Lấy sơ đồ ghế
    public Map<String, Object> getSeatMap(int showtimeId, int roomId) {
        Map<String, Object> seatMap = new HashMap<>();

        Room room = roomDao.getRoomById(roomId);
        if (room == null) {
            return null;
        }

        // Lấy ghế với trạng thái
        List<Seat> seatsWithStatus = seatDao.getSeatsWithStatus(roomId, showtimeId);

        // Nhóm ghế theo hàng
        Map<String, List<Map<String, Object>>> rows = new TreeMap<>();

        for (Seat seat : seatsWithStatus) {
            String row = seat.getRowNumber();

            if (!rows.containsKey(row)) {
                rows.put(row, new ArrayList<>());
            }

            Map<String, Object> seatInfo = new HashMap<>();
            seatInfo.put("seatId", seat.getId());
            seatInfo.put("seatCode", seat.getSeatCode());
            seatInfo.put("rowNumber", seat.getRowNumber());
            seatInfo.put("seatNumber", seat.getSeatNumber());
            seatInfo.put("seatType", seat.getSeatType());
            seatInfo.put("status", seat.getSeatStatus());
            seatInfo.put("price", calculateSeatPrice(seat.getSeatType()));

            rows.get(row).add(seatInfo);
        }

        // Thông tin phòng
        Map<String, Object> roomInfo = new HashMap<>();
        roomInfo.put("roomId", room.getId());
        roomInfo.put("roomName", room.getRoomName());
        roomInfo.put("roomType", room.getRoomType());
        roomInfo.put("totalSeats", room.getTotalSeats());
        roomInfo.put("availableSeats", seatDao.countAvailableSeats(roomId, showtimeId));

        seatMap.put("room", roomInfo);
        seatMap.put("rows", rows);

        return seatMap;
    }

    // Kiểm tra ghế có sẵn không
    public boolean checkSeatAvailability(int showtimeId, int roomId, String seatCode) {
        Seat seat = seatDao.getSeatByCode(roomId, seatCode);
        if (seat == null) {
            return false;
        }

        return seatDao.isSeatAvailable(seat.getId(), showtimeId);
    }

    // Giữ ghế tạm thời (dùng cho modal)
    public boolean reserveSeatsTemporarily(int showtimeId, int roomId,
                                           String[] seatCodes, int userId) {
        return bookedSeatDao.reserveSeatsTemporarily(showtimeId, roomId, seatCodes, userId, 5);
    }

    // Hủy giữ ghế
    public boolean releaseSeats(int showtimeId, String[] seatCodes) {
        return bookedSeatDao.releaseSeats(showtimeId, seatCodes);
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
        details.put("availableSeats", seatDao.countAvailableSeats(room.getId(), showtimeId));

        return details;
    }

    // Kiểm tra suất chiếu có hợp lệ không
    public boolean isValidShowtime(int showtimeId) {
        Showtime showtime = showtimeDao.getShowtimeById(showtimeId);
        if (showtime == null || !showtime.isActive()) {
            return false;
        }

        // Có thể thêm logic kiểm tra thời gian
        return true;
    }

    // Tính giá ghế
    private double calculateSeatPrice(String seatType) {
        if ("vip".equalsIgnoreCase(seatType)) {
            return 120000; // Giá VIP
        }
        return 100000; // Giá thường
    }

    // Tính tổng tiền
    public double calculateTotalPrice(String ticketType, int quantity, String[] seatCodes) {
        double unitPrice = calculateTicketPrice(ticketType);
        double seatSurcharge = 0;

        // Tính phụ phí ghế VIP
        for (String seatCode : seatCodes) {
            // Giả sử có method để kiểm tra loại ghế
            if (isVipSeat(seatCode)) {
                seatSurcharge += 20000; // Phụ phí VIP
            }
        }

        return (unitPrice + seatSurcharge) * quantity;
    }

    private double calculateTicketPrice(String ticketType) {
        switch (ticketType.toLowerCase()) {
            case "adult":
                return 100000;
            case "student":
                return 80000;
            case "child":
                return 60000;
            default:
                return 100000;
        }
    }

    private boolean isVipSeat(String seatCode) {
        // Logic kiểm tra ghế VIP (cần implement)
        return seatCode.startsWith("V") || seatCode.contains("VIP");
    }
}