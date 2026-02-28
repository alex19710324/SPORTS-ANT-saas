package com.sportsant.saas.communication.controller;

import com.sportsant.saas.communication.entity.Notification;
import com.sportsant.saas.communication.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communication")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommunicationController {

    @Autowired
    private NotificationService notificationService;

    // --- P0: Send Notification ---
    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING')")
    public Notification sendNotification(@RequestBody Notification notification) {
        return notificationService.sendNotification(notification);
    }

    // --- P1: History ---
    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING')")
    public List<Notification> getHistory() {
        return notificationService.getAllNotifications();
    }
}
