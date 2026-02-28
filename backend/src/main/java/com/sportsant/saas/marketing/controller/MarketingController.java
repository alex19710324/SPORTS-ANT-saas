package com.sportsant.saas.marketing.controller;

import com.sportsant.saas.marketing.entity.Campaign;
import com.sportsant.saas.marketing.service.MarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marketing/campaigns")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MarketingController {

    @Autowired
    private MarketingService marketingService;

    @GetMapping
    public List<Campaign> getAllCampaigns() {
        return marketingService.getAllCampaigns();
    }

    @PostMapping
    public Campaign createCampaign(@RequestBody Campaign campaign) {
        return marketingService.createCampaign(campaign);
    }

    @PostMapping("/{id}/ai-content")
    public Campaign generateAiContent(@PathVariable Long id) {
        return marketingService.generateAiContent(id);
    }
}
