package com.sportsant.saas.trendyplay.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_collectibles")
public class UserCollectible {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String userId;
    private String tenantId;
    
    private String blindBoxSeriesId;
    
    @Column(nullable = false)
    private String collectibleName;
    
    private String rarity; // COMMON, RARE, LEGENDARY, SECRET
    private String imageUrl;
    
    private BigDecimal marketValue; // Estimated market value
    private String currency;
    
    private Boolean isDigitalTwin; // NFT linked?
    private String nftTokenId;
    
    private LocalDateTime obtainedAt;
    
    @PrePersist
    protected void onCreate() {
        obtainedAt = LocalDateTime.now();
        if (isDigitalTwin == null) isDigitalTwin = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getBlindBoxSeriesId() { return blindBoxSeriesId; }
    public void setBlindBoxSeriesId(String blindBoxSeriesId) { this.blindBoxSeriesId = blindBoxSeriesId; }
    public String getCollectibleName() { return collectibleName; }
    public void setCollectibleName(String collectibleName) { this.collectibleName = collectibleName; }
    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public BigDecimal getMarketValue() { return marketValue; }
    public void setMarketValue(BigDecimal marketValue) { this.marketValue = marketValue; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Boolean getIsDigitalTwin() { return isDigitalTwin; }
    public void setIsDigitalTwin(Boolean isDigitalTwin) { this.isDigitalTwin = isDigitalTwin; }
    public String getNftTokenId() { return nftTokenId; }
    public void setNftTokenId(String nftTokenId) { this.nftTokenId = nftTokenId; }
    public LocalDateTime getObtainedAt() { return obtainedAt; }
    public void setObtainedAt(LocalDateTime obtainedAt) { this.obtainedAt = obtainedAt; }
}
