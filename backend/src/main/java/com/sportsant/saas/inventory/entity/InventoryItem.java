package com.sportsant.saas.inventory.entity;

import com.sportsant.saas.store.entity.Store;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    private String name;
    
    @Column(unique = true)
    private String sku;
    
    private String category; // RETAIL (Drinks, Merch), SPARE_PART (Wires, Pads), ASSET (VR Headset)
    
    private Integer quantity;
    
    private Integer threshold; // Low stock alert level
    
    private Double unitPrice;
    
    private LocalDateTime lastUpdated;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getThreshold() { return threshold; }
    public void setThreshold(Integer threshold) { this.threshold = threshold; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
