package com.sportsant.saas.communication.service;

import com.sportsant.saas.communication.entity.Notification;
import com.sportsant.saas.communication.repository.NotificationRepository;
import com.sportsant.saas.entity.User;
import com.sportsant.saas.globalization.service.CommunicationGateway;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSource;
import java.util.Locale;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private CommunicationGateway communicationGateway;

    @Autowired
    private MessageSource messageSource;

    public void sendToUser(User user, String title, String message, String type, String link) {
        // Legacy method support
        doSend(user, title, message, type, link);
    }

    public void sendLocalizedToUser(User user, String titleKey, String messageKey, Object[] messageArgs, String type, String link) {
        // Resolve Locale
        Locale locale = Locale.US; // Default
        Member member = null;
        try {
            member = membershipService.getMember(user.getId());
            if (member != null && member.getLocale() != null) {
                locale = Locale.forLanguageTag(member.getLocale().replace("-", "_")); // Java Locale uses underscore usually, but forLanguageTag handles BCP 47
            }
        } catch (Exception e) {
            // Ignore if member not found, use default locale
        }

        String title = messageSource.getMessage(titleKey, null, titleKey, locale);
        String message = messageSource.getMessage(messageKey, messageArgs, messageKey, locale);

        doSend(user, title, message, type, link);
    }

    private void doSend(User user, String title, String message, String type, String link) {
        Notification n = new Notification();
        n.setRecipient(user);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setLink(link);
        n.setCreatedAt(LocalDateTime.now());
        n.setRead(false);
        notificationRepository.save(n);
        
        // Force Route through Communication Gateway for ALL notifications
        try {
            Member member = membershipService.getMember(user.getId());
            if (member != null && member.getPhoneNumber() != null) {
                String locale = member.getLocale() != null ? member.getLocale() : "en-US";
                communicationGateway.sendSms(locale, member.getPhoneNumber(), "[" + title + "] " + message);
            }
        } catch (Exception e) {
            System.err.println("Failed to route to Communication Gateway: " + e.getMessage());
        }
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
