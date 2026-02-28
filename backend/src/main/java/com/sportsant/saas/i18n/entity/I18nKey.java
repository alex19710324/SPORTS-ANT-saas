package com.sportsant.saas.i18n.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "i18n_keys")
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
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKeyName() { return keyName; }
    public void setKeyName(String keyName) { this.keyName = keyName; }
    public String getZhCn() { return zhCn; }
    public void setZhCn(String zhCn) { this.zhCn = zhCn; }
    public String getEnUs() { return enUs; }
    public void setEnUs(String enUs) { this.enUs = enUs; }
    public String getJaJp() { return jaJp; }
    public void setJaJp(String jaJp) { this.jaJp = jaJp; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
