package com.sportsant.saas.inventory.controller;

import com.sportsant.saas.common.response.Result;
import com.sportsant.saas.inventory.entity.InventoryPrediction;
import com.sportsant.saas.inventory.service.SmartReplenishmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/smart")
@Tag(name = "智能库存补货", description = "AI预测库存需求与自动补货")
public class SmartInventoryController {

    private final SmartReplenishmentService replenishmentService;

    public SmartInventoryController(SmartReplenishmentService replenishmentService) {
        this.replenishmentService = replenishmentService;
    }

    @PostMapping("/predict-all")
    @Operation(summary = "手动触发全量库存预测")
    public Result<List<InventoryPrediction>> predictAll(
            @RequestHeader(value = "X-Tenant-ID", defaultValue = "DEFAULT") String tenantId) {
        return Result.success(replenishmentService.batchPredictAllProducts(tenantId));
    }

    @PostMapping("/predict/{productId}")
    @Operation(summary = "预测单个商品库存")
    public Result<InventoryPrediction> predictProduct(
            @PathVariable Long productId,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = "DEFAULT") String tenantId) {
        return Result.success(replenishmentService.predictProductSales(productId, tenantId));
    }

    @GetMapping("/predictions/today")
    @Operation(summary = "获取今日所有预测结果")
    public Result<List<InventoryPrediction>> getTodayPredictions(
            @RequestHeader(value = "X-Tenant-ID", defaultValue = "DEFAULT") String tenantId) {
        return Result.success(replenishmentService.getTodayPredictions(tenantId));
    }

    @GetMapping("/advice/urgent")
    @Operation(summary = "获取紧急补货建议")
    public Result<List<InventoryPrediction>> getUrgentAdvice(
            @RequestHeader(value = "X-Tenant-ID", defaultValue = "DEFAULT") String tenantId) {
        return Result.success(replenishmentService.getUrgentReplenishments(tenantId));
    }
}
