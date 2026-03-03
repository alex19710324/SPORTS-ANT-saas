package com.sportsant.saas.notification.service;

import com.sportsant.saas.common.context.UserContext;
import com.sportsant.saas.notification.channel.ChannelStrategy;
import com.sportsant.saas.notification.entity.NotificationLog;
import com.sportsant.saas.notification.entity.NotificationTemplate;
import com.sportsant.saas.notification.repository.NotificationLogRepository;
import com.sportsant.saas.notification.repository.NotificationTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.sportsant.saas.notification.entity.SystemNotification;
import com.sportsant.saas.notification.repository.SystemNotificationRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationCenterService {

    @Autowired
    private NotificationTemplateRepository templateRepository;

    @Autowired
    private NotificationLogRepository logRepository;

    @Autowired
    private SystemNotificationRepository systemNotificationRepository;

    private Map<String, ChannelStrategy> channelMap;

    public NotificationCenterService(List<ChannelStrategy> channels) {
        this.channelMap = new HashMap<>();
        for (ChannelStrategy channel : channels) {
            this.channelMap.put(channel.getChannelName(), channel);
        }
    }

    public void sendNotification(String templateCode, String recipient, Map<String, String> params) {
        // Auto-detect locale
        String locale = UserContext.getLocale();
        if (locale == null) locale = "en-US";
        
        sendNotification(templateCode, recipient, params, locale);
    }

    public void sendNotification(String templateCode, String recipient, Map<String, String> params, String locale) {
        // 1. Get Template (Try requested locale -> Fallback to en-US -> Fallback to any)
        NotificationTemplate template = templateRepository.findByCodeAndLocale(templateCode, locale)
                .or(() -> templateRepository.findByCodeAndLocale(templateCode, "en-US"))
                .or(() -> templateRepository.findByCode(templateCode).stream().findFirst())
                .orElse(null);
        
        if (template == null) {
            System.out.println("Template not found: " + templateCode);
            return;
        }

        if (!template.isEnabled()) {
            System.out.println("Template disabled: " + templateCode);
            return;
        }

        // 2. Format Content
        String content = template.getContent();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                content = content.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        // 3. Select Channel
        ChannelStrategy strategy = channelMap.get(template.getChannel());
        if (strategy == null) {
            // Fallback or Log
            logResult(templateCode, recipient, template.getChannel(), content, "FAILED", "Channel not supported: " + template.getChannel());
            return;
        }

        // 4. Send
        try {
            boolean success = strategy.send(recipient, content);
            if (success) {
                logResult(templateCode, recipient, template.getChannel(), content, "SENT", null);
            } else {
                logResult(templateCode, recipient, template.getChannel(), content, "FAILED", "Provider returned false");
            }
        } catch (Exception e) {
            logResult(templateCode, recipient, template.getChannel(), content, "FAILED", e.getMessage());
        }
    }

    private void logResult(String templateCode, String recipient, String channel, String content, String status, String error) {
        NotificationLog log = new NotificationLog();
        log.setTemplateCode(templateCode);
        log.setRecipient(recipient);
        log.setChannel(channel);
        log.setContent(content);
        log.setStatus(status);
        log.setErrorMsg(error);
        logRepository.save(log);
    }

    public NotificationTemplate createTemplate(NotificationTemplate template) {
        return templateRepository.save(template);
    }

    public List<NotificationTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }

    public List<NotificationLog> getAllLogs() {
        return logRepository.findAll();
    }

    @Transactional
    public void sendSystemNotification(String recipient, String title, String content, String type) {
        SystemNotification notification = new SystemNotification();
        notification.setRecipient(recipient);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        systemNotificationRepository.save(notification);
    }

    public List<SystemNotification> getUnreadNotifications(String recipient) {
        return systemNotificationRepository.findByRecipientAndIsReadFalse(recipient);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        SystemNotification notification = systemNotificationRepository.findById(notificationId).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            systemNotificationRepository.save(notification);
        }
    }
}
