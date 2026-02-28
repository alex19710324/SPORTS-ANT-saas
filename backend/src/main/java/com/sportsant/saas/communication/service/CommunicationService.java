package com.sportsant.saas.communication.service;

import com.sportsant.saas.communication.entity.Notification;
import com.sportsant.saas.communication.repository.NotificationRepository;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommunicationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MemberRepository memberRepository;

    public List<Notification> getMyNotifications(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.findByRecipientIdAndIsReadFalse(userId).size();
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification n = notificationRepository.findById(notificationId).orElse(null);
        if (n != null) {
            n.setIsRead(true);
            notificationRepository.save(n);
        }
    }

    @Transactional
    public void sendNotification(Long recipientId, String title, String message, String type) {
        Notification n = new Notification();
        n.setRecipientId(recipientId);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        notificationRepository.save(n);
    }

    @Transactional
    public void broadcast(String title, String message) {
        List<Member> allMembers = memberRepository.findAll();
        for (Member member : allMembers) {
            // Assuming Member.id maps to User ID for notifications
            // In real system, Member entity would link to a User entity
            // For MVP, we use member.getId() as recipientId
            sendNotification(member.getId(), title, message, "MARKETING");
        }
    }
}
