package com.sportsant.saas.membership.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "level_id", nullable = false)
    private MemberLevel currentLevel;

    @Column(nullable = false)
    private Integer growthValue; // 成长值

    @Column(nullable = false)
    private Integer points; // 积分

    private LocalDateTime expireDate; // 会员有效期

    // AI Labels
    private String tags; // "HIGH_VALUE,CHURN_RISK"

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        growthValue = 0;
        points = 0;
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
