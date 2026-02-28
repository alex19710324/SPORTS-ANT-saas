package com.sportsant.saas.communication.service;

import com.sportsant.saas.communication.entity.Notification;
import com.sportsant.saas.communication.repository.NotificationRepository;
import com.sportsant.saas.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void sendToUser(User user, String title, String message, String type, String link) {
        Notification n = new Notification();
        n.setRecipient(user);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setLink(link);
        n.setCreatedAt(LocalDateTime.now());
        n.setRead(false);
        notificationRepository.save(n);
    }

    public void sendToRole(String role, String title, String message, String type, String link) {
        Notification n = new Notification();
        n.setRoleTarget(role);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setLink(link);
        n.setCreatedAt(LocalDateTime.now());
        n.setRead(false);
        notificationRepository.save(n);
    }

    public List<Notification> getUserNotifications(User user) {
        // Simple aggregation: User specific + Role based + Global
        List<Notification> all = new ArrayList<>();
        all.addAll(notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId()));
        
        user.getRoles().forEach(role -> {
            all.addAll(notificationRepository.findByRoleTargetOrderByCreatedAtDesc(role.getName().name()));
        });
        
        // In real app, we need to track "read status" for role-based/global notifications per user
        // For MVP, we just return them
        
        return all;
    }
    
    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}
