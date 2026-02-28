package com.sportsant.saas.communication.controller;

import com.sportsant.saas.communication.entity.Notification;
import com.sportsant.saas.communication.service.NotificationService;
import com.sportsant.saas.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communication")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommunicationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/notify")
    @PreAuthorize("hasRole('ADMIN')")
    public Notification sendNotification(@RequestBody Map<String, Object> payload) {
        Long userId = Long.valueOf(payload.get("userId").toString());
        String title = (String) payload.get("title");
        String content = (String) payload.get("content");
        String channel = (String) payload.get("channel"); // EMAIL, SMS

        return notificationService.send(userId, title, content, channel);
    }

    @GetMapping("/notifications")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Notification> getMyNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return notificationService.getUserHistory(userDetails.getId());
    }
}
