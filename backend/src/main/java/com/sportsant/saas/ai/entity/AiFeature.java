package com.sportsant.saas.ai.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_features")
@Data
@NoArgsConstructor
public class AiFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "user_total_purchase_30d"

    @Column(nullable = false)
    private String type; // e.g., "NUMERIC", "CATEGORICAL"

    @Column(columnDefinition = "TEXT")
    private String description;

    private String source; // e.g., "mysql", "kafka", "redis"

    private boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        isActive = true;
    }
}
