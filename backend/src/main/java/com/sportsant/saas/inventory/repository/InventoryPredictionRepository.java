package com.sportsant.saas.inventory.repository;

import com.sportsant.saas.inventory.entity.InventoryPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryPredictionRepository extends JpaRepository<InventoryPrediction, String> {
    
    List<InventoryPrediction> findByTenantIdAndPredictionDate(String tenantId, LocalDate predictionDate);
    
    List<InventoryPrediction> findByTenantIdAndReplenishmentAdviceAndPredictionDate(
        String tenantId, String replenishmentAdvice, LocalDate predictionDate);
    
    List<InventoryPrediction> findByProductIdAndPredictionDateBetween(
        Long productId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT ip FROM InventoryPrediction ip WHERE ip.tenantId = :tenantId " +
           "AND ip.predictionDate = :date " +
           "ORDER BY " +
           "CASE ip.replenishmentAdvice " +
           "  WHEN 'URGENT' THEN 1 " +
           "  WHEN 'NORMAL' THEN 2 " +
           "  WHEN 'SAFE' THEN 3 " +
           "  ELSE 4 END, ip.daysOfSupply ASC")
    List<InventoryPrediction> findSortedPredictions(
        @Param("tenantId") String tenantId,
        @Param("date") LocalDate date);
}
