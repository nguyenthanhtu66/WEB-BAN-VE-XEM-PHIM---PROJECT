package vn.edu.hcmuaf.fit.demo1.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import vn.edu.hcmuaf.fit.demo1.task.SeatCleanupTask;

@WebListener
public class AppInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("🚀 Starting seat cleanup task...");
        SeatCleanupTask.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("🛑 Stopping seat cleanup task...");
        SeatCleanupTask.stop();
    }
}