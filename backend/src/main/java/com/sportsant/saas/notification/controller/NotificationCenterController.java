package com.sportsant.saas.notification.controller;

import com.sportsant.saas.notification.entity.NotificationLog;
import com.sportsant.saas.notification.entity.NotificationTemplate;
import com.sportsant.saas.notification.service.NotificationCenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.sportsant.saas.notification.entity.SystemNotification;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/notification")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "通知中心", description = "消息推送、模板管理、发送记录")
public class NotificationCenterController {

    @Autowired
    private NotificationCenterService notificationService;

    @GetMapping("/system/my")
    public List<SystemNotification> getMyNotifications() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return notificationService.getUnreadNotifications(username);
    }

    @PostMapping("/system/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }

    @GetMapping("/templates")
    public List<NotificationTemplate> getTemplates() {
        return notificationService.getAllTemplates();
    }

    @PostMapping("/templates")
    public NotificationTemplate createTemplate(@RequestBody NotificationTemplate template) {
        return notificationService.createTemplate(template);
    }

    @GetMapping("/logs")
    public List<NotificationLog> getLogs() {
        return notificationService.getAllLogs();
    }

    @PostMapping("/send")
    @Operation(summary = "Send notification", description = "发送系统通知 (支持多语言)")
    public void send(@RequestParam String templateCode, @RequestParam String recipient, @RequestBody(required = false) Map<String, String> params) {
        notificationService.sendNotification(templateCode, recipient, params);
    }
}
