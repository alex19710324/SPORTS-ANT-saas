package com.sportsant.saas.inventory.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_predictions")
public class InventoryPrediction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String tenantId;
    
    @Column(nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private String productName;
    
    @Column(nullable = false)
    private LocalDate predictionDate;
    
    // 历史数据
    private Integer currentStock;
    private Integer dailySalesAvg;
    private Integer weeklySalesAvg;
    private Double salesTrend;
    
    // 预测结果
    private Integer predictedSales;
    private Double confidenceLevel;
    
    // 补货建议
    private String replenishmentAdvice;
    private Integer suggestedQuantity;
    private Integer daysOfSupply;
    private BigDecimal estimatedCost;
    private BigDecimal estimatedRevenue;
    
    // 算法参数
    private String algorithmUsed;
    private String algorithmParams;
    
    @Column(nullable = false)
    private LocalDateTime predictedAt;
    
    @PrePersist
    protected void onCreate() {
        predictedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public LocalDate getPredictionDate() { return predictionDate; }
    public void setPredictionDate(LocalDate predictionDate) { this.predictionDate = predictionDate; }
    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
    public Integer getDailySalesAvg() { return dailySalesAvg; }
    public void setDailySalesAvg(Integer dailySalesAvg) { this.dailySalesAvg = dailySalesAvg; }
    public Integer getWeeklySalesAvg() { return weeklySalesAvg; }
    public void setWeeklySalesAvg(Integer weeklySalesAvg) { this.weeklySalesAvg = weeklySalesAvg; }
    public Double getSalesTrend() { return salesTrend; }
    public void setSalesTrend(Double salesTrend) { this.salesTrend = salesTrend; }
    public Integer getPredictedSales() { return predictedSales; }
    public void setPredictedSales(Integer predictedSales) { this.predictedSales = predictedSales; }
    public Double getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(Double confidenceLevel) { this.confidenceLevel = confidenceLevel; }
    public String getReplenishmentAdvice() { return replenishmentAdvice; }
    public void setReplenishmentAdvice(String replenishmentAdvice) { this.replenishmentAdvice = replenishmentAdvice; }
    public Integer getSuggestedQuantity() { return suggestedQuantity; }
    public void setSuggestedQuantity(Integer suggestedQuantity) { this.suggestedQuantity = suggestedQuantity; }
    public Integer getDaysOfSupply() { return daysOfSupply; }
    public void setDaysOfSupply(Integer daysOfSupply) { this.daysOfSupply = daysOfSupply; }
    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }
    public BigDecimal getEstimatedRevenue() { return estimatedRevenue; }
    public void setEstimatedRevenue(BigDecimal estimatedRevenue) { this.estimatedRevenue = estimatedRevenue; }
    public String getAlgorithmUsed() { return algorithmUsed; }
    public void setAlgorithmUsed(String algorithmUsed) { this.algorithmUsed = algorithmUsed; }
    public String getAlgorithmParams() { return algorithmParams; }
    public void setAlgorithmParams(String algorithmParams) { this.algorithmParams = algorithmParams; }
    public LocalDateTime getPredictedAt() { return predictedAt; }
    public void setPredictedAt(LocalDateTime predictedAt) { this.predictedAt = predictedAt; }
}
