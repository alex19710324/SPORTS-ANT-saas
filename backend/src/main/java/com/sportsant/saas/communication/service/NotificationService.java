package com.sportsant.saas.communication.service;

import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.communication.entity.Notification;
import com.sportsant.saas.communication.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class NotificationService implements AiAware {

    @Autowired
    private NotificationRepository notificationRepository;

    private final Random random = new Random();

    public Notification sendNotification(Notification notification) {
        // Save first as PENDING
        notificationRepository.save(notification);

        // Mock Sending Process
        boolean success = simulateSending(notification.getChannel());
        
        if (success) {
            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
        } else {
            notification.setStatus("FAILED");
            notification.setErrorMessage("Gateway timeout or invalid recipient");
        }
        
        return notificationRepository.save(notification);
    }

    private boolean simulateSending(String channel) {
        // Simulate network delay
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        
        System.out.println("Sending via " + channel + "...");
        
        // 90% success rate
        return random.nextInt(10) > 0;
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        // AI Suggestion: "Resend failed notifications"
    }
}
