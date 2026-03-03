package com.sportsant.saas.marketing.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "marketing_campaigns")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // DISCOUNT, COUPON, EVENT, AD_BOOST
    private String status; // DRAFT, ACTIVE, PAUSED, COMPLETED
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private BigDecimal budget;
    private BigDecimal spend;
    private String currency; // e.g., USD, CNY

    // Targeting
    private String targetAudience; // ALL, VIP, CHURN_RISK, NEW_LEADS
    private String targetRegion;   // e.g., zh-CN, en-US (Locale based)
    
    // Performance
    private Integer reachCount;
    private Integer conversionCount;
    private BigDecimal roi;

    @Column(columnDefinition = "TEXT")
    private String aiGeneratedContent;

    @PrePersist
    protected void onCreate() {
        if (spend == null) spend = BigDecimal.ZERO;
        if (reachCount == null) reachCount = 0;
        if (conversionCount == null) conversionCount = 0;
        if (roi == null) roi = BigDecimal.ZERO;
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
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public BigDecimal getSpend() { return spend; }
    public void setSpend(BigDecimal spend) { this.spend = spend; }
    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }
    public Integer getReachCount() { return reachCount; }
    public void setReachCount(Integer reachCount) { this.reachCount = reachCount; }
    public Integer getConversionCount() { return conversionCount; }
    public void setConversionCount(Integer conversionCount) { this.conversionCount = conversionCount; }
    public BigDecimal getRoi() { return roi; }
    public void setRoi(BigDecimal roi) { this.roi = roi; }
    public String getAiGeneratedContent() { return aiGeneratedContent; }
    public void setAiGeneratedContent(String aiGeneratedContent) { this.aiGeneratedContent = aiGeneratedContent; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getTargetRegion() { return targetRegion; }
    public void setTargetRegion(String targetRegion) { this.targetRegion = targetRegion; }
}
