package com.sportsant.saas.communication.service;

import com.sportsant.saas.communication.entity.Notification;
import com.sportsant.saas.communication.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Sends a notification (Mock).
     */
    public Notification send(Long userId, String title, String content, String channel) {
        logger.info("Sending {} to user {}: {} - {}", channel, userId, title, content);

        Notification notification = new Notification();
        notification.setRecipientUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setChannel(channel);
        notification.setStatus("SENT"); // Mock successful send
        
        // In real world: integrate with JavaMailSender, Twilio, WeChat SDK etc.
        
        return notificationRepository.save(notification);
    }

    public List<Notification> getUserHistory(Long userId) {
        return notificationRepository.findByRecipientUserId(userId);
    }
}
