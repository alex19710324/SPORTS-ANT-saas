package com.sportsant.saas.membership.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_levels")
public class MemberLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // 工蚁先锋 -> 圣蚁尊者

    @Column(nullable = false)
    private Integer levelOrder; // 1, 2, 3, 4, 5

    @Column(nullable = false)
    private Integer requiredGrowth; // e.g., 0, 1000, 5000...

    @Column(columnDefinition = "TEXT")
    private String benefitsJson; // JSON: {"discount": 0.95, "priority": true}

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getLevelOrder() { return levelOrder; }
    public void setLevelOrder(Integer levelOrder) { this.levelOrder = levelOrder; }
    public Integer getRequiredGrowth() { return requiredGrowth; }
    public void setRequiredGrowth(Integer requiredGrowth) { this.requiredGrowth = requiredGrowth; }
    public String getBenefitsJson() { return benefitsJson; }
    public void setBenefitsJson(String benefitsJson) { this.benefitsJson = benefitsJson; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
