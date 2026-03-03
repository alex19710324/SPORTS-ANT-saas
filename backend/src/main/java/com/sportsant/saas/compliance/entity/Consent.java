package com.sportsant.saas.compliance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consents")
public class Consent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String agreementType; // e.g. "terms_of_service", "privacy_policy", "marketing_sms"

    @Column(nullable = false)
    private String version; // e.g. "1.0", "2023-10-01"

    @Column(nullable = false)
    private boolean agreed; // true = agreed, false = withdrawn

    private String ipAddress;
    
    private String userAgent;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Consent() {}

    public Consent(Long userId, String agreementType, String version, boolean agreed, String ipAddress, String userAgent) {
        this.userId = userId;
        this.agreementType = agreementType;
        this.version = version;
        this.agreed = agreed;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getAgreementType() { return agreementType; }
    public void setAgreementType(String agreementType) { this.agreementType = agreementType; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public boolean isAgreed() { return agreed; }
    public void setAgreed(boolean agreed) { this.agreed = agreed; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
