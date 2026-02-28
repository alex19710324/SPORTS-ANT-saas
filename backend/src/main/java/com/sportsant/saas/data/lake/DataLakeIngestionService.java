package com.sportsant.saas.data.lake;

import com.sportsant.saas.ai.event.SystemEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Mock Data Lake Ingestion Service.
 * In production, this would be Kafka + Hudi/Iceberg.
 */
@Component
public class DataLakeIngestionService {

    @Async
    @EventListener
    public void onSystemEvent(SystemEvent event) {
        // Capture all system events into Data Lake
        String eventType = event.getType();
        Map<String, Object> payload = event.getPayload();
        
        // Mock writing to Parquet file
        System.out.println("[DataLake] Ingested Event: " + eventType + " Payload: " + payload);
        
        // Mock real-time computation trigger
        if ("ORDER_CREATED".equals(eventType)) {
            updateRealTimeRevenue(payload);
        }
    }

    private void updateRealTimeRevenue(Map<String, Object> payload) {
        // Mock Flink Job: Update Redis/ClickHouse
        System.out.println("[RealTime-Compute] Updating Revenue Metric...");
    }
}
