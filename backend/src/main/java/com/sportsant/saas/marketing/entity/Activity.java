package com.sportsant.saas.marketing.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "marketing_activities")
@Data
@NoArgsConstructor
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // GROUP_BUY, FLASH_SALE, CHECK_IN
    private String status; // DRAFT, ACTIVE, PAUSED, ENDED

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(columnDefinition = "TEXT")
    private String rulesJson; // {"groupSize": 3, "discount": 0.8}

    @Column(columnDefinition = "TEXT")
    private String rewardsJson; // {"type": "coupon", "value": 10}

    private BigDecimal budget;
    private BigDecimal usedBudget;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = "DRAFT";
        usedBudget = BigDecimal.ZERO;
    }
}
