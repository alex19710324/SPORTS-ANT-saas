package com.sportsant.saas.ai.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_suggestions")
public class AiSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // REVENUE_SPIKE, SAFETY_ALERT, STAFFING_SHORTAGE, MARKETING_OPPORTUNITY
    
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content; // The suggestion text

    private String priority; // HIGH, MEDIUM, LOW

    private String actionableApi; // Endpoint to call if user accepts

    private String status; // NEW, ACCEPTED, REJECTED
    
    @Column(columnDefinition = "TEXT")
    private String feedback;
    
    private LocalDateTime createdAt;
    
    private Double confidence; // 0.0 - 1.0

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "NEW";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getActionableApi() { return actionableApi; }
    public void setActionableApi(String actionableApi) { this.actionableApi = actionableApi; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
}
