package com.sportsant.saas.device.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "work_orders")
@Data
@NoArgsConstructor
public class WorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long deviceId;

    @Column(nullable = false)
    private String type; // REPAIR, MAINTENANCE

    @Column(nullable = false)
    private String priority; // HIGH, MEDIUM, LOW

    private String status; // PENDING, IN_PROGRESS, COMPLETED

    @Column(columnDefinition = "TEXT")
    private String description;

    private Long reportedBy; // User ID
    private Long assignedTo; // Technician User ID

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = "PENDING";
    }
}
