package com.sportsant.saas.data.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.ai.event.SystemEvent;
import com.sportsant.saas.data.entity.EventLog;
import com.sportsant.saas.data.repository.EventLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class DataCollectionService {
    private static final Logger logger = LoggerFactory.getLogger(DataCollectionService.class);

    @Autowired
    private EventLogRepository eventLogRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Listens to ALL system events and persists them to the Data Lake (H2/MySQL for now).
     * This ensures the Data Middle Platform has a complete record of everything happening.
     */
    @Async // Don't block the main thread
    @EventListener
    public void collectEvent(SystemEvent event) {
        try {
            EventLog log = new EventLog();
            log.setSource(event.getSource());
            log.setEventType(event.getType());
            log.setTimestamp(event.getTimestamp());
            log.setPayloadJson(objectMapper.writeValueAsString(event.getPayload()));
            
            eventLogRepository.save(log);
            logger.debug("Data Collected: {} - {}", event.getType(), event.getSource());
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize event payload", e);
        }
    }
}
