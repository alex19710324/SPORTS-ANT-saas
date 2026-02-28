package com.sportsant.saas.ai.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
