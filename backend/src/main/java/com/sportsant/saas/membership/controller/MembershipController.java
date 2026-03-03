package com.sportsant.saas.membership.controller;

import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.service.MembershipService;
import com.sportsant.saas.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.sportsant.saas.membership.service.MembershipAiLifecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/membership")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "会员中心", description = "会员信息、积分、等级、权益管理")
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private MembershipAiLifecycleService aiLifecycleService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get my membership", description = "获取当前用户的会员详情 (含积分/余额)")
    public Member getMyMembership(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membershipService.getMember(userDetails.getId());
    }

    @GetMapping("/{userId}/ai-insights")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get AI Insights", description = "获取会员 AI 洞察 (流失风险、推荐)")
    public Map<String, Object> getAiInsights(@PathVariable Long userId) {
        return aiLifecycleService.getMemberInsights(userId);
    }

    @PostMapping("/ai-analysis/run")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Run AI Analysis", description = "手动触发全量会员 AI 分析")
    public void runAiAnalysis() {
        aiLifecycleService.runDailyAiAnalysis();
    }

    @PostMapping("/checkin")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Daily check-in", description = "每日签到 (赚取成长值)")
    public Member dailyCheckIn(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membershipService.dailyCheckIn(userDetails.getId());
    }

    @PostMapping("/simulate-purchase")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Simulate purchase", description = "模拟消费 (测试积分和成长值)")
    public Member simulatePurchase(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody Map<String, Object> payload) {
        Integer amount = (Integer) payload.get("amount");
        return membershipService.simulatePurchase(userDetails.getId(), amount);
    }

    // Admin endpoint to simulate growth (for demo)
    @PostMapping("/growth/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add growth (Admin)", description = "手动增加成长值 (管理员)")
    public ResponseEntity<?> addGrowth(@RequestBody java.util.Map<String, Object> payload) {
        Long userId = Long.valueOf(payload.get("userId").toString());
        Integer amount = (Integer) payload.get("amount");
        String source = (String) payload.get("source");
        
        membershipService.addGrowth(userId, amount, source);
        return ResponseEntity.ok("Growth added successfully");
    }
}
