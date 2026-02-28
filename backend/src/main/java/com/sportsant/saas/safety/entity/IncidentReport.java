package com.sportsant.saas.safety.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "safety_incidents")
@Data
@NoArgsConstructor
public class IncidentReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // FIRE, MEDICAL, THEFT

    @Column(nullable = false)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String reporter;

    @Column(nullable = false)
    private String status; // REPORTED, RESPONDING, RESOLVED

    @Column(nullable = false)
    private LocalDateTime reportedAt;

    @PrePersist
    protected void onCreate() {
        reportedAt = LocalDateTime.now();
        if (status == null) status = "REPORTED";
    }
}
