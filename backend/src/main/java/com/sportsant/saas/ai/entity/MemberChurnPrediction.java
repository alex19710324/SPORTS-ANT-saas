package com.sportsant.saas.ai.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_member_churn_predictions")
public class MemberChurnPrediction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Double churnProbability; // 流失概率 0-1

    @Column(nullable = false)
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(nullable = false)
    private LocalDateTime predictedAt;

    @Column(nullable = false)
    private LocalDateTime validUntil;

    // 特征数据
    @Column(columnDefinition = "TEXT") // H2 compatible, use jsonb for Postgres
    private String features; // JSON格式的特征数据

    // 预测依据
    @Column(columnDefinition = "TEXT")
    private String reasons; // 流失原因分析

    // 推荐行动
    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @PrePersist
    protected void onCreate() {
        predictedAt = LocalDateTime.now();
        validUntil = predictedAt.plusDays(7); // 预测有效期7天
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Double getChurnProbability() { return churnProbability; }
    public void setChurnProbability(Double churnProbability) { this.churnProbability = churnProbability; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public LocalDateTime getPredictedAt() { return predictedAt; }
    public void setPredictedAt(LocalDateTime predictedAt) { this.predictedAt = predictedAt; }
    public LocalDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }
    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }
    public String getReasons() { return reasons; }
    public void setReasons(String reasons) { this.reasons = reasons; }
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
}
