package vn.edu.hcmuaf.fit.demo1.task;

import vn.edu.hcmuaf.fit.demo1.dao.BookingDao;
import java.util.Timer;
import java.util.TimerTask;

public class ReservationCleanupTask {

    private static final long CLEANUP_INTERVAL = 60000; // 1 phút
    private final BookingDao bookingDao;
    private Timer timer;

    public ReservationCleanupTask() {
        this.bookingDao = new BookingDao();
        this.timer = new Timer(true); // Daemon thread
    }

    public void start() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println("Running reservation cleanup task...");
                    int cleaned = cleanupExpiredReservations();
                    if (cleaned > 0) {
                        System.out.println("Cleaned up " + cleaned + " expired reservations");
                    }
                } catch (Exception e) {
                    System.err.println("Error in cleanup task: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        // Chạy mỗi 1 phút
        timer.scheduleAtFixedRate(task, 0, CLEANUP_INTERVAL);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private int cleanupExpiredReservations() {
        try {
            // Sử dụng phương thức cleanup trong BookingDao
            boolean success = bookingDao.cleanupExpiredReservations();
            return success ? 1 : 0;
        } catch (Exception e) {
            System.err.println("Error cleaning up expired reservations: " + e.getMessage());
            return 0;
        }
    }
}