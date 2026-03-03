package com.sportsant.saas.inventory.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    private String name;
    
    private String category; // Gift, Merchandise, Consumable

    private Integer quantity; // Current Stock

    private Integer safetyStock; // Warning Threshold

    private BigDecimal costPrice;
    
    private BigDecimal sellPrice; // Cash Price (optional)
    
    private Integer redeemPoints; // Points Price (for gifts)

    @Column(nullable = false)
    private String unit; // pcs, box, kg

    private String location; // Shelf A1

    private LocalDateTime lastRestockDate;

    @PrePersist
    protected void onCreate() {
        if (quantity == null) quantity = 0;
        if (safetyStock == null) safetyStock = 10;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getSafetyStock() { return safetyStock; }
    public void setSafetyStock(Integer safetyStock) { this.safetyStock = safetyStock; }
    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
    public BigDecimal getSellPrice() { return sellPrice; }
    public void setSellPrice(BigDecimal sellPrice) { this.sellPrice = sellPrice; }
    public Integer getRedeemPoints() { return redeemPoints; }
    public void setRedeemPoints(Integer redeemPoints) { this.redeemPoints = redeemPoints; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDateTime getLastRestockDate() { return lastRestockDate; }
    public void setLastRestockDate(LocalDateTime lastRestockDate) { this.lastRestockDate = lastRestockDate; }
}
