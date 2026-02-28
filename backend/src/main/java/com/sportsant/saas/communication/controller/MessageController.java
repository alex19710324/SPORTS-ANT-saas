package com.sportsant.saas.communication.controller;

import com.sportsant.saas.communication.entity.Message;
import com.sportsant.saas.communication.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communication/messages")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    @PreAuthorize("isAuthenticated()")
    public Message sendMessage(@RequestBody Map<String, Object> payload) {
        // In real app, get senderId from SecurityContext
        Long senderId = 1L; // Mock current user
        Long receiverId = Long.valueOf(payload.get("receiverId").toString());
        String content = (String) payload.get("content");
        
        return messageService.sendMessage(senderId, receiverId, content);
    }

    @GetMapping("/inbox")
    @PreAuthorize("isAuthenticated()")
    public List<Message> getInbox() {
        Long userId = 1L; // Mock current user
        return messageService.getInbox(userId);
    }
    
    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public long getUnreadCount() {
        Long userId = 1L; // Mock current user
        return messageService.getUnreadCount(userId);
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public void markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
    }
}
