package com.sportsant.saas.ai.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_suggestions")
@Data
@NoArgsConstructor
public class AiSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // e.g., "SECURITY", "PERFORMANCE", "MARKETING", "FINANCE"

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String priority; // "LOW", "MEDIUM", "HIGH", "CRITICAL"

    @Column(nullable = false)
    private String status; // "PENDING", "ACCEPTED", "REJECTED", "AUTO_EXECUTED"

    private String actionableApi; // API endpoint to execute the suggestion (if applicable)

    @Column(columnDefinition = "TEXT")
    private String feedback; // User feedback after accepting/rejecting

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
