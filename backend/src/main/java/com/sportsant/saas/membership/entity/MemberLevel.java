package com.sportsant.saas.membership.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_levels")
@Data
@NoArgsConstructor
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
}
