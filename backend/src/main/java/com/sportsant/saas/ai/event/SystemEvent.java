package com.sportsant.saas.ai.event;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@NoArgsConstructor
public class SystemEvent {
    private String source; // e.g., "USER_SERVICE", "ORDER_SERVICE"
    private String type; // e.g., "USER_REGISTERED", "LARGE_TRANSACTION"
    private Map<String, Object> payload;
    private LocalDateTime timestamp = LocalDateTime.now();

    public SystemEvent(String source, String type, Map<String, Object> payload) {
        this.source = source;
        this.type = type;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
