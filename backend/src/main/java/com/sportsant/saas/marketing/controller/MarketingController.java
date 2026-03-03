package com.sportsant.saas.marketing.controller;

import com.sportsant.saas.common.response.ApiResponse;
import com.sportsant.saas.marketing.entity.Campaign;
import com.sportsant.saas.marketing.service.AutoRetentionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marketing")
@Tag(name = "自动化营销", description = "会员挽回自动化营销系统")
public class MarketingController {
    
    private final AutoRetentionService retentionService;

    public MarketingController(AutoRetentionService retentionService) {
        this.retentionService = retentionService;
    }
    
    @PostMapping("/retention/execute/{memberId}")
    @Operation(summary = "执行会员挽回活动")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Campaign>> executeRetention(
            @PathVariable String memberId,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        Campaign campaign = retentionService.executeRetentionCampaign(memberId, tenantId);
        return ResponseEntity.ok(ApiResponse.success(campaign, "挽回活动已启动"));
    }
    
    @PostMapping("/retention/batch-execute")
    @Operation(summary = "批量执行挽回活动")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Campaign>>> batchExecuteRetention(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestParam(defaultValue = "10") int limit) {
        List<Campaign> campaigns = retentionService.batchExecuteRetention(tenantId, limit);
        return ResponseEntity.ok(ApiResponse.success(campaigns, "批量挽回活动已启动"));
    }
    
    // @GetMapping("/retention/stats") 
    // AutoRetentionService currently doesn't implement getCampaignStats properly due to missing repository methods
    // So we comment this out for now or implement a stub
    
    @GetMapping("/retention/campaigns")
    @Operation(summary = "获取营销活动列表")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<Campaign>>> getCampaigns(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestParam(defaultValue = "PENDING") String status) {
        // 这里需要实现根据状态查询
        return ResponseEntity.ok(ApiResponse.success(List.of()));
    }
}
