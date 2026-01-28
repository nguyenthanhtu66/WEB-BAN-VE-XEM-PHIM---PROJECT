package vn.edu.hcmuaf.fit.demo1.task;

import vn.edu.hcmuaf.fit.demo1.dao.BookedSeatDao;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SeatCleanupTask {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final BookedSeatDao bookedSeatDao = new BookedSeatDao();

    public static void start() {
        // Chạy mỗi 1 phút để kiểm tra và xóa seat hết hạn
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("🧹 Running seat cleanup task...");
                int cleaned = bookedSeatDao.releaseAllExpiredReservations();
                if (cleaned > 0) {
                    System.out.println("✅ Cleaned " + cleaned + " expired seat reservations");
                }
            } catch (Exception e) {
                System.err.println("❌ Error in seat cleanup task: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    public static void stop() {
        scheduler.shutdown();
    }
}