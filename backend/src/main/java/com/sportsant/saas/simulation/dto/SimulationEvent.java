package com.sportsant.saas.simulation.dto;

import java.time.LocalDateTime;

public class SimulationEvent {
    private String type; // "MEMBER", "PURCHASE", "DEVICE", "SYSTEM"
    private String title;
    private String description;
    private String level; // "INFO", "WARNING", "ERROR", "SUCCESS"
    private LocalDateTime timestamp;
    private String relatedId; // memberId, productId, deviceId
    
    public SimulationEvent(String type, String title, String description, String level, String relatedId) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.level = level;
        this.relatedId = relatedId;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLevel() { return level; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getRelatedId() { return relatedId; }
}
