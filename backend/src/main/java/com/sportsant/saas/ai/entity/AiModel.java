package com.sportsant.saas.ai.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_models")
@Data
@NoArgsConstructor
public class AiModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "customer-churn-prediction-v1"

    @Column(nullable = false)
    private String type; // e.g., "CLASSIFICATION", "GENERATION", "RECOMMENDATION"

    @Column(columnDefinition = "TEXT")
    private String description;

    private String endpointUrl; // URL to the actual inference service (e.g., Python flask app)

    private String version;

    private boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        isActive = true;
    }
}
