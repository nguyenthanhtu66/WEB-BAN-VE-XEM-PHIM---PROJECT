package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.SeatDao;
import vn.edu.hcmuaf.fit.demo1.model.Seat;

import java.util.List;

public class SeatService {

    private final SeatDao seatDao = new SeatDao();

    public List<Seat> getSeatsByRoom(int roomId) {
        return seatDao.getSeatsByRoom(roomId);
    }

    public Seat getSeatById(int seatId) {
        return seatDao.getSeatById(seatId);
    }

    public Seat getSeatByCode(int roomId, String seatCode) {
        return seatDao.getSeatByCode(roomId, seatCode);
    }

    public List<Seat> getAllSeats() {
        return seatDao.getAllSeats();
    }
}