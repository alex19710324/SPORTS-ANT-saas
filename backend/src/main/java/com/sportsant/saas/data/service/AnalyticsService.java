package com.sportsant.saas.data.service;

import com.sportsant.saas.data.repository.EventLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired
    private EventLogRepository eventLogRepository;

    /**
     * Mocks a BI report generation. In real life, this would query ClickHouse/Spark.
     */
    public Map<String, Object> generateDailyReport(LocalDateTime date) {
        LocalDateTime start = date.toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        long newUsers = eventLogRepository.countByEventTypeAndTimestampBetween("USER_REGISTERED", start, end);
        long orders = eventLogRepository.countByEventTypeAndTimestampBetween("ORDER_CREATED", start, end);
        long errors = eventLogRepository.countByEventTypeAndTimestampBetween("SYSTEM_ERROR", start, end);

        Map<String, Object> report = new HashMap<>();
        report.put("date", date.toLocalDate().toString());
        report.put("new_users", newUsers);
        report.put("orders", orders);
        report.put("errors", errors);
        report.put("status", "GENERATED");
        
        return report;
    }
}
