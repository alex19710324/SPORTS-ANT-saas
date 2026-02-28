package com.sportsant.saas.marketing.controller;

import com.sportsant.saas.marketing.entity.Campaign;
import com.sportsant.saas.marketing.service.MarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marketing/campaigns")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MarketingController {

    @Autowired
    private MarketingService marketingService;

    @GetMapping
    @PreAuthorize("hasRole('STORE_MANAGER') or hasRole('MARKETING') or hasRole('ADMIN')")
    public List<Campaign> getAllCampaigns() {
        return marketingService.getAllCampaigns();
    }

    @PostMapping
    @PreAuthorize("hasRole('STORE_MANAGER') or hasRole('MARKETING') or hasRole('ADMIN')")
    public Campaign createCampaign(@RequestBody Campaign campaign) {
        return marketingService.createCampaign(campaign);
    }

    @PostMapping("/{id}/generate-content")
    @PreAuthorize("hasRole('STORE_MANAGER') or hasRole('MARKETING') or hasRole('ADMIN')")
    public Campaign generateContent(@PathVariable Long id) {
        return marketingService.generateAiContent(id);
    }
}
