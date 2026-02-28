package com.sportsant.saas.marketing.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // GROUP_BUY, FLASH_SALE, COUPON
    private String status; // DRAFT, ACTIVE, COMPLETED
    
    @Column(columnDefinition = "TEXT")
    private String rulesJson; // Store specific rules like discount, min_participants

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(columnDefinition = "TEXT")
    private String aiGeneratedContent; // JSON storing title, body, poster_url

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "DRAFT";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRulesJson() { return rulesJson; }
    public void setRulesJson(String rulesJson) { this.rulesJson = rulesJson; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getAiGeneratedContent() { return aiGeneratedContent; }
    public void setAiGeneratedContent(String aiGeneratedContent) { this.aiGeneratedContent = aiGeneratedContent; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
