package com.sportsant.saas.data.repository;

import com.sportsant.saas.data.entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventLogRepository extends JpaRepository<EventLog, Long> {
    List<EventLog> findByEventTypeAndTimestampBetween(String eventType, LocalDateTime start, LocalDateTime end);
    long countByEventTypeAndTimestampBetween(String eventType, LocalDateTime start, LocalDateTime end);
}
