package com.sportsant.saas.communication.controller;

import com.sportsant.saas.communication.entity.Notification;
import com.sportsant.saas.communication.service.CommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communication")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommunicationController {

    @Autowired
    private CommunicationService communicationService;

    @GetMapping("/notifications")
    @PreAuthorize("hasRole('USER')")
    public List<Notification> getMyNotifications(@RequestParam Long userId) {
        return communicationService.getMyNotifications(userId);
    }

    @GetMapping("/notifications/unread-count")
    @PreAuthorize("hasRole('USER')")
    public Long getUnreadCount(@RequestParam Long userId) {
        return communicationService.getUnreadCount(userId);
    }

    @PutMapping("/notifications/{id}/read")
    @PreAuthorize("hasRole('USER')")
    public void markAsRead(@PathVariable Long id) {
        communicationService.markAsRead(id);
    }

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING')")
    public void broadcast(@RequestBody Map<String, String> payload) {
        communicationService.broadcast(payload.get("title"), payload.get("message"));
    }
}
