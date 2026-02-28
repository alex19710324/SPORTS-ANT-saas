package com.sportsant.saas.marketing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.marketing.entity.Campaign;
import com.sportsant.saas.marketing.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MarketingService {

    @Autowired
    private CampaignRepository campaignRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Campaign createCampaign(Campaign campaign) {
        if (campaign.getStatus() == null) {
            campaign.setStatus("DRAFT");
        }
        return campaignRepository.save(campaign);
    }

    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    public Campaign generateAiContent(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        // Simulate AI Content Generation based on Campaign Type and Name
        Map<String, String> content = new HashMap<>();
        String type = campaign.getType();
        String name = campaign.getName();

        if ("GROUP_BUY".equals(type)) {
            content.put("wechat_title", "🔥 Limited Time Group Buy: " + name + "!");
            content.put("wechat_body", "Gather your friends and get amazing discounts! Only for " + name + ". Join now!");
            content.put("poster_url", "https://via.placeholder.com/300x400?text=Group+Buy");
        } else if ("FLASH_SALE".equals(type)) {
            content.put("wechat_title", "⚡ Flash Sale Alert: " + name);
            content.put("wechat_body", "Don't miss out! 50% OFF for the next 24 hours on " + name + ".");
            content.put("poster_url", "https://via.placeholder.com/300x400?text=Flash+Sale");
        } else {
            content.put("wechat_title", "Special Offer: " + name);
            content.put("wechat_body", "Check out our latest promotion for " + name + ".");
            content.put("poster_url", "https://via.placeholder.com/300x400?text=Special+Offer");
        }

        try {
            campaign.setAiGeneratedContent(objectMapper.writeValueAsString(content));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return campaignRepository.save(campaign);
    }
}
