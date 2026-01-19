package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.*;
import vn.edu.hcmuaf.fit.demo1.model.*;

import java.util.*;

public class SeatService {

    private final SeatDao seatDao = new SeatDao();
    private final BookedSeatDao bookedSeatDao = new BookedSeatDao();
    private final RoomDao roomDao = new RoomDao();

    // Kiểm tra ghế có sẵn không
    public boolean isSeatAvailable(int showtimeId, int roomId, String seatCode) {
        Seat seat = seatDao.getSeatByCode(roomId, seatCode);
        if (seat == null) {
            return false;
        }

        return seatDao.isSeatAvailable(seat.getId(), showtimeId);
    }

    // Lấy danh sách ghế theo phòng
    public List<Seat> getSeatsByRoom(int roomId) {
        return seatDao.getSeatsByRoom(roomId);
    }

    // Lấy thông tin ghế
    public Seat getSeatInfo(int seatId) {
        return seatDao.getSeatById(seatId);
    }

    // Lấy sơ đồ ghế cho suất chiếu
    public Map<String, Object> getSeatMap(int showtimeId, int roomId) {
        Map<String, Object> seatMap = new HashMap<>();

        // Lấy thông tin phòng
        Room room = roomDao.getRoomById(roomId);
        if (room == null) {
            return null;
        }

        // Lấy tất cả ghế
        List<Seat> allSeats = seatDao.getSeatsByRoom(roomId);

        // Nhóm ghế theo hàng
        Map<String, List<Map<String, Object>>> rows = new TreeMap<>();

        for (Seat seat : allSeats) {
            String row = seat.getRowNumber();

            if (!rows.containsKey(row)) {
                rows.put(row, new ArrayList<>());
            }

            Map<String, Object> seatInfo = new HashMap<>();
            seatInfo.put("seatCode", seat.getSeatCode());
            seatInfo.put("seatNumber", seat.getSeatNumber());
            seatInfo.put("seatType", seat.getSeatType());
            seatInfo.put("isAvailable", isSeatAvailable(showtimeId, roomId, seat.getSeatCode()));
            seatInfo.put("seatId", seat.getId());

            // Kiểm tra trạng thái ghế
            String status = bookedSeatDao.getSeatStatus(showtimeId, seat.getSeatCode());
            seatInfo.put("status", status);

            rows.get(row).add(seatInfo);
        }

        seatMap.put("room", room);
        seatMap.put("rows", rows);
        seatMap.put("totalSeats", room.getTotalSeats());
        seatMap.put("availableSeats", countAvailableSeats(showtimeId, roomId));

        return seatMap;
    }

    // Đếm ghế còn trống
    public int countAvailableSeats(int showtimeId, int roomId) {
        List<Seat> allSeats = seatDao.getSeatsByRoom(roomId);
        int availableCount = 0;

        for (Seat seat : allSeats) {
            if (isSeatAvailable(showtimeId, roomId, seat.getSeatCode())) {
                availableCount++;
            }
        }

        return availableCount;
    }

    // Lấy ghế VIP
    public List<Seat> getVipSeats(int roomId) {
        List<Seat> allSeats = seatDao.getSeatsByRoom(roomId);
        List<Seat> vipSeats = new ArrayList<>();

        for (Seat seat : allSeats) {
            if ("vip".equalsIgnoreCase(seat.getSeatType())) {
                vipSeats.add(seat);
            }
        }

        return vipSeats;
    }

    // Kiểm tra xem ghế có phải VIP không
    public boolean isVipSeat(int seatId) {
        Seat seat = seatDao.getSeatById(seatId);
        return seat != null && "vip".equalsIgnoreCase(seat.getSeatType());
    }

    // Lấy giá ghế theo loại
    public double getSeatPrice(int seatId, String ticketType) {
        Seat seat = seatDao.getSeatById(seatId);
        if (seat == null) {
            return 0;
        }

        double basePrice = getBasePrice(ticketType);

        // Nếu là ghế VIP, tăng giá 50%
        if ("vip".equalsIgnoreCase(seat.getSeatType())) {
            return basePrice * 1.5;
        }

        return basePrice;
    }

    private double getBasePrice(String ticketType) {
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

    // Lấy thông tin trạng thái ghế
    public Map<String, List<String>> getSeatStatusByRow(int showtimeId, int roomId) {
        Map<String, List<String>> statusMap = new HashMap<>();

        List<Seat> seats = seatDao.getSeatsByRoom(roomId);
        for (Seat seat : seats) {
            String row = seat.getRowNumber();
            String status = bookedSeatDao.getSeatStatus(showtimeId, seat.getSeatCode());

            if (!statusMap.containsKey(row)) {
                statusMap.put(row, new ArrayList<>());
            }

            statusMap.get(row).add(seat.getSeatCode() + ":" + status);
        }

        return statusMap;
    }
}