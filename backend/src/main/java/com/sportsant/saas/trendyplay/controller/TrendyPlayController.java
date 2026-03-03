package com.sportsant.saas.trendyplay.controller;

import com.sportsant.saas.common.response.ApiResponse;
import com.sportsant.saas.trendyplay.entity.BlindBoxSeries;
import com.sportsant.saas.trendyplay.entity.UserCollectible;
import com.sportsant.saas.trendyplay.service.TrendyPlayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trendy-play")
@Tag(name = "Global Trendy Play", description = "跨国数字潮玩生态")
public class TrendyPlayController {

    private final TrendyPlayService service;

    public TrendyPlayController(TrendyPlayService service) {
        this.service = service;
    }

    @GetMapping("/series/active")
    @Operation(summary = "Get Active Blind Box Series")
    public ResponseEntity<ApiResponse<List<BlindBoxSeries>>> getActiveSeries(
            @RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String tenantId) {
        return ResponseEntity.ok(ApiResponse.success(service.getActiveSeries(tenantId)));
    }

    @GetMapping("/series/global")
    @Operation(summary = "Get Global Limited Editions")
    public ResponseEntity<ApiResponse<List<BlindBoxSeries>>> getGlobalLimited() {
        return ResponseEntity.ok(ApiResponse.success(service.getGlobalLimitedSeries()));
    }

    @PostMapping("/purchase/{seriesId}")
    @Operation(summary = "Buy and Open Blind Box")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserCollectible>> purchaseBlindBox(
            @PathVariable String seriesId,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = "default") String tenantId,
            @RequestParam String userId) {
        try {
            UserCollectible result = service.purchaseBlindBox(userId, tenantId, seriesId);
            return ResponseEntity.ok(ApiResponse.success(result, "Congratulations! You got " + result.getRarity()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/collection/{userId}")
    @Operation(summary = "Get User Collection")
    public ResponseEntity<ApiResponse<List<UserCollectible>>> getUserCollection(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success(service.getUserCollection(userId)));
    }
}
