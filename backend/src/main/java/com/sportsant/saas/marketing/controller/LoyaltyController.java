package com.sportsant.saas.marketing.controller;

import com.sportsant.saas.marketing.entity.Reward;
import com.sportsant.saas.marketing.service.LoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marketing/loyalty")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LoyaltyController {

    @Autowired
    private LoyaltyService loyaltyService;

    @GetMapping("/rewards")
    public List<Reward> getRewards() {
        return loyaltyService.getAvailableRewards();
    }

    @PostMapping("/redeem")
    @PreAuthorize("hasRole('USER')") // Any logged-in member
    public Map<String, String> redeemReward(@RequestBody Map<String, Object> payload) {
        Long memberId = Long.valueOf(payload.get("memberId").toString());
        Long rewardId = Long.valueOf(payload.get("rewardId").toString());
        
        loyaltyService.redeemReward(memberId, rewardId);
        return Map.of("status", "SUCCESS", "message", "Redemption successful!");
    }

    @PostMapping("/rewards")
    @PreAuthorize("hasRole('MARKETING') or hasRole('ADMIN')")
    public Reward createReward(@RequestBody Reward reward) {
        return loyaltyService.createReward(reward.getName(), reward.getDescription(), reward.getPointsCost());
    }
}
