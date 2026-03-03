package com.sportsant.saas.notification.repository;

import com.sportsant.saas.notification.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByCodeAndLocale(String code, String locale);
    
    // For admin UI to list all versions
    List<NotificationTemplate> findByCode(String code);
}
