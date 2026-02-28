package com.sportsant.saas.marketing.controller;

import com.sportsant.saas.marketing.entity.Campaign;
import com.sportsant.saas.marketing.entity.Coupon;
import com.sportsant.saas.marketing.service.MarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marketing")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MarketingController {

    @Autowired
    private MarketingService marketingService;

    @GetMapping("/campaigns")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING')")
    public List<Campaign> getAllCampaigns() {
        return marketingService.getAllCampaigns();
    }

    @PostMapping("/campaigns")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING')")
    public Campaign createCampaign(@RequestBody Campaign campaign) {
        return marketingService.createCampaign(campaign);
    }

    @PostMapping("/campaigns/{id}/launch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING')")
    public void launchCampaign(@PathVariable Long id) {
        marketingService.launchCampaign(id);
    }

    @PostMapping("/coupons")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING')")
    public Coupon createCoupon(@RequestBody Coupon coupon) {
        return marketingService.createCoupon(coupon);
    }

    @PostMapping("/coupons/validate")
    public Coupon validateCoupon(@RequestBody Map<String, String> payload) {
        return marketingService.validateCoupon(payload.get("code"));
    }

    @GetMapping("/loyalty/rewards")
    public List<com.sportsant.saas.marketing.entity.Reward> getRewards() {
        return marketingService.getRewards();
    }

    @PostMapping("/loyalty/redeem")
    public void redeemReward(@RequestBody Map<String, Long> payload) {
        Long memberId = Long.valueOf(payload.get("memberId").toString());
        Long rewardId = Long.valueOf(payload.get("rewardId").toString());
        marketingService.redeemReward(memberId, rewardId);
    }
}
