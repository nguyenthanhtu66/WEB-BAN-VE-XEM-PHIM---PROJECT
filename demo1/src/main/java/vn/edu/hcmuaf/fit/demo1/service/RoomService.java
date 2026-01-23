package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.RoomDao;
import vn.edu.hcmuaf.fit.demo1.model.Room;

import java.util.List;

public class RoomService {

    private final RoomDao roomDao = new RoomDao();

    public List<Room> getAllRooms() {
        return roomDao.getAllRooms();
    }

    public Room getRoomById(int id) {
        return roomDao.getRoomById(id);
    }

    public Room getRoomByName(String name) {
        return roomDao.getRoomByName(name);
    }

    public List<Room> getActiveRooms() {
        return roomDao.getActiveRooms();
    }
}