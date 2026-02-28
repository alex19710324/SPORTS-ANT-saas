package com.sportsant.saas.data.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "data_metrics_daily")
@Data
@NoArgsConstructor
public class DailyMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;
    private String metricName; // REVENUE, VISITORS, ACTIVE_USERS
    private String dimension; // STORE_ID:1, REGION:ASIA
    private Double value;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
