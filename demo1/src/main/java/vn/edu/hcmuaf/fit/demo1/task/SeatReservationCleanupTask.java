package vn.edu.hcmuaf.fit.demo1.task;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import vn.edu.hcmuaf.fit.demo1.dao.BookedSeatDao;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class SeatReservationCleanupTask implements ServletContextListener {

    private ScheduledExecutorService scheduler;
    private final BookedSeatDao bookedSeatDao = new BookedSeatDao();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== Seat Reservation Cleanup Task Initialized ===");

        // Tạo scheduler với 1 thread
        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Chạy mỗi 1 phút để kiểm tra và xóa ghế hết hạn
        scheduler.scheduleAtFixedRate(this::cleanupExpiredReservations,
                1, 1, TimeUnit.MINUTES);

        System.out.println("Cleanup task scheduled to run every 1 minute");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=== Seat Reservation Cleanup Task Shutting Down ===");

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                // Chờ tối đa 30 giây để các task đang chạy hoàn thành
                if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Cleanup task stopped");
    }

    /**
     * Dọn dẹp các ghế đã hết thời gian giữ
     */
    private void cleanupExpiredReservations() {
        try {
            int deletedCount = bookedSeatDao.cleanupExpiredReservations();

            if (deletedCount > 0) {
                System.out.println("[" + new java.util.Date() + "] Đã xóa " + deletedCount + " ghế hết hạn giữ");
            }

            // Log mỗi 10 lần chạy để debug
            if (System.currentTimeMillis() % 10 == 0) {
                System.out.println("[" + new java.util.Date() + "] Cleanup task is running...");
            }

        } catch (Exception e) {
            System.err.println("[" + new java.util.Date() + "] Lỗi khi dọn dẹp ghế hết hạn: " + e.getMessage());
            e.printStackTrace();
        }
    }
}