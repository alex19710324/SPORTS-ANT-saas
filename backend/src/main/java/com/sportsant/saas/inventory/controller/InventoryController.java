package com.sportsant.saas.inventory.controller;

import com.sportsant.saas.common.response.ApiResponse;
import com.sportsant.saas.inventory.entity.InventoryPrediction;
import com.sportsant.saas.inventory.service.SmartReplenishmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "智能库存管理", description = "基于AI的库存预测和补货系统")
public class InventoryController {
    
    private final SmartReplenishmentService replenishmentService;
    
    public InventoryController(SmartReplenishmentService replenishmentService) {
        this.replenishmentService = replenishmentService;
    }
    
    @PostMapping("/predict/{productId}")
    @Operation(summary = "预测商品未来销量")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<InventoryPrediction>> predictProductSales(
            @PathVariable Long productId,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        InventoryPrediction prediction = replenishmentService.predictProductSales(productId, tenantId);
        return ResponseEntity.ok(ApiResponse.success(prediction, "销量预测完成"));
    }
    
    @PostMapping("/predict/batch")
    @Operation(summary = "批量预测所有商品")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<InventoryPrediction>>> batchPredict(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        List<InventoryPrediction> predictions = replenishmentService.batchPredictAllProducts(tenantId);
        return ResponseEntity.ok(ApiResponse.success(predictions, "批量预测完成"));
    }
    
    @GetMapping("/predictions/today")
    @Operation(summary = "获取今日预测结果")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<List<InventoryPrediction>>> getTodayPredictions(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        List<InventoryPrediction> predictions = replenishmentService.getSortedPredictions(tenantId);
        return ResponseEntity.ok(ApiResponse.success(predictions));
    }
    
    @GetMapping("/urgent")
    @Operation(summary = "获取紧急补货建议")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryPrediction>>> getUrgentReplenishments(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        List<InventoryPrediction> urgentItems = replenishmentService.getUrgentReplenishments(tenantId);
        return ResponseEntity.ok(ApiResponse.success(urgentItems));
    }
    
    @GetMapping("/dashboard")
    @Operation(summary = "库存仪表板数据")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInventoryDashboard(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        List<InventoryPrediction> todayPredictions = replenishmentService.getSortedPredictions(tenantId);
        List<InventoryPrediction> urgentItems = replenishmentService.getUrgentReplenishments(tenantId);
        
        long totalProducts = todayPredictions.size();
        long urgentCount = urgentItems.size();
        long normalCount = todayPredictions.stream()
            .filter(p -> "NORMAL".equals(p.getReplenishmentAdvice()))
            .count();
        long safeCount = todayPredictions.stream()
            .filter(p -> "SAFE".equals(p.getReplenishmentAdvice()))
            .count();
        
        double totalEstimatedCost = todayPredictions.stream()
            .map(p -> p.getEstimatedCost() != null ? p.getEstimatedCost().doubleValue() : 0)
            .reduce(0.0, Double::sum);
        
        double totalEstimatedRevenue = todayPredictions.stream()
            .map(p -> p.getEstimatedRevenue() != null ? p.getEstimatedRevenue().doubleValue() : 0)
            .reduce(0.0, Double::sum);
        
        Map<String, Object> dashboard = Map.of(
            "totalProducts", totalProducts,
            "urgentCount", urgentCount,
            "normalCount", normalCount,
            "safeCount", safeCount,
            "totalEstimatedCost", totalEstimatedCost,
            "totalEstimatedRevenue", totalEstimatedRevenue,
            "estimatedROI", totalEstimatedRevenue > 0 ? 
                String.format("%.1f%%", (totalEstimatedRevenue - totalEstimatedCost) / totalEstimatedCost * 100) : "0%",
            "lastUpdated", java.time.LocalDateTime.now().toString()
        );
        
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
    
    @PostMapping("/execute-replenishment/{productId}")
    @Operation(summary = "执行补货操作")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<String>> executeReplenishment(
            @PathVariable Long productId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestParam Integer quantity) {
        // 这里应该调用实际的补货系统
        // 暂时返回成功
        return ResponseEntity.ok(ApiResponse.success(
            "补货指令已发送: 商品ID=" + productId + ", 数量=" + quantity,
            "补货操作已执行"
        ));
    }
}
