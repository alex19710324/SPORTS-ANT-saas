package com.sportsant.saas.notification.repository;

import com.sportsant.saas.notification.entity.SystemNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemNotificationRepository extends JpaRepository<SystemNotification, Long> {
    List<SystemNotification> findByRecipientAndIsReadFalse(String recipient);
    List<SystemNotification> findByRecipient(String recipient);
}
