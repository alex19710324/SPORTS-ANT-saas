package com.sportsant.saas.scheduler.job;

import org.springframework.stereotype.Component;

@Component("pointsExpiryJob")
public class PointsExpiryJob implements Runnable {

    @Override
    public void run() {
        System.out.println("Running Points Expiry Job...");
        // 1. Find points transactions expired today
        // 2. Deduct member points
        // 3. Send notification
        
        try {
            Thread.sleep(2000); // Simulate work
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("Points Expiry Job Completed.");
    }
}
