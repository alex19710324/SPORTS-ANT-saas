package com.sportsant.saas.ai.controller;

import com.sportsant.saas.ai.entity.MemberChurnPrediction;
import com.sportsant.saas.ai.repository.MemberChurnPredictionRepository;
import com.sportsant.saas.ai.service.AdvancedChurnPredictionService;
import com.sportsant.saas.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/churn")
@Tag(name = "AI Churn Prediction", description = "AI会员流失预测接口")
public class MemberChurnPredictionController {

    private final AdvancedChurnPredictionService predictionService;
    private final MemberChurnPredictionRepository predictionRepository;

    public MemberChurnPredictionController(AdvancedChurnPredictionService predictionService,
                                           MemberChurnPredictionRepository predictionRepository) {
        this.predictionService = predictionService;
        this.predictionRepository = predictionRepository;
    }

    @PostMapping("/predict/{memberId}")
    @Operation(summary = "为指定会员生成流失预测")
    public Result<MemberChurnPrediction> predictMember(@PathVariable Long memberId) {
        return Result.success(predictionService.predictMemberChurn(memberId));
    }

    @GetMapping("/prediction/{memberId}")
    @Operation(summary = "获取会员最新流失预测")
    public Result<MemberChurnPrediction> getLatestPrediction(@PathVariable Long memberId) {
        return predictionRepository.findFirstByMemberIdOrderByPredictedAtDesc(memberId)
                .map(Result::success)
                .orElse(Result.error("No prediction found for this member"));
    }

    @PostMapping("/batch-predict")
    @Operation(summary = "手动触发批量预测")
    public Result<String> triggerBatchPrediction() {
        predictionService.batchPredictChurn();
        return Result.success("Batch prediction started");
    }

    @GetMapping("/stats")
    @Operation(summary = "获取流失风险统计概览")
    public Result<Map<String, Object>> getChurnStats() {
        List<Object[]> riskCounts = predictionRepository.countByRiskLevel();
        Map<String, Object> stats = new HashMap<>();
        
        long totalPredictions = 0;
        Map<String, Long> distribution = new HashMap<>();
        
        for (Object[] row : riskCounts) {
            String riskLevel = (String) row[0];
            Long count = (Long) row[1];
            distribution.put(riskLevel, count);
            totalPredictions += count;
        }
        
        stats.put("totalMembersMonitored", totalPredictions);
        stats.put("riskDistribution", distribution);
        stats.put("highRiskCount", distribution.getOrDefault("HIGH", 0L) + distribution.getOrDefault("CRITICAL", 0L));
        
        return Result.success(stats);
    }
    
    @GetMapping("/risk-list/{riskLevel}")
    @Operation(summary = "获取指定风险等级的会员预测列表")
    public Result<List<MemberChurnPrediction>> getRiskList(@PathVariable String riskLevel) {
        return Result.success(predictionRepository.findByRiskLevel(riskLevel));
    }
}
