package com.sportsant.saas.i18n.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "i18n_keys")
@Data
@NoArgsConstructor
public class I18nKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String keyName; // e.g., "common.welcome"

    @Column(columnDefinition = "TEXT")
    private String zhCn; // Default source

    @Column(columnDefinition = "TEXT")
    private String enUs;

    @Column(columnDefinition = "TEXT")
    private String jaJp;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
