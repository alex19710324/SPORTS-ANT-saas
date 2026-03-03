package com.sportsant.saas.marketing.service;

import com.sportsant.saas.common.context.UserContext;
import com.sportsant.saas.globalization.entity.InternationalizationProfile;
import com.sportsant.saas.globalization.repository.InternationalizationProfileRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.marketing.entity.Campaign;
import com.sportsant.saas.marketing.repository.CampaignRepository;
import com.sportsant.saas.crm.entity.Lead;
import com.sportsant.saas.crm.repository.LeadRepository;
// import com.sportsant.saas.membership.entity.Member;
// import com.sportsant.saas.membership.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class MarketingService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private LeadRepository leadRepository;
    
    @Autowired
    private InternationalizationProfileRepository profileRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    // @Autowired
    // private MemberRepository memberRepository;

    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    public Campaign createCampaign(Campaign campaign) {
        campaign.setStatus("DRAFT");
        
        // Auto-detect currency/region if not set
        if (campaign.getTargetRegion() == null) {
            campaign.setTargetRegion(UserContext.getLocale());
        }
        
        if (campaign.getCurrency() == null) {
            Optional<InternationalizationProfile> profileOpt = profileRepository.findByLocale(campaign.getTargetRegion());
            if (profileOpt.isPresent()) {
                try {
                    JsonNode currencyConfig = objectMapper.readTree(profileOpt.get().getCurrencyConfig());
                    campaign.setCurrency(currencyConfig.get("code").asText());
                } catch (Exception e) {
                    campaign.setCurrency("USD");
                }
            } else {
                campaign.setCurrency("USD");
            }
        }
        
        return campaignRepository.save(campaign);
    }

    @Transactional
    public void launchCampaign(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        if (!"DRAFT".equals(campaign.getStatus())) {
            throw new RuntimeException("Campaign is not in DRAFT status");
        }

        // 1. Identify Target Audience
        int targetCount = 0;
        if ("NEW_LEADS".equals(campaign.getTargetAudience())) {
            List<Lead> leads = leadRepository.findByStatus("NEW");
            targetCount = leads.size();
            // Simulate Sending Emails/SMS to leads
            System.out.println("Sending Campaign to " + targetCount + " Leads...");
        } else if ("VIP".equals(campaign.getTargetAudience())) {
            // Mock VIP logic
            targetCount = 50; 
            System.out.println("Sending Campaign to " + targetCount + " VIP Members...");
        } else {
            // All
            targetCount = 1000;
        }

        // 2. Update Status
        campaign.setStatus("ACTIVE");
        campaign.setStartDate(LocalDateTime.now());
        campaign.setReachCount(targetCount);
        
        campaignRepository.save(campaign);
    }

    // Simulate Performance Tracking (called by scheduler or event)
    @Transactional
    public void updateCampaignStats(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow();
        if ("ACTIVE".equals(campaign.getStatus())) {
            Random rand = new Random();
            int newConversions = rand.nextInt(10);
            campaign.setConversionCount(campaign.getConversionCount() + newConversions);
            
            // ROI Calculation: (Revenue - Cost) / Cost
            // Mock Revenue = Conversion * 100
            BigDecimal revenue = BigDecimal.valueOf(campaign.getConversionCount() * 100);
            if (campaign.getSpend() != null && campaign.getSpend().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal roi = revenue.subtract(campaign.getSpend())
                        .divide(campaign.getSpend(), 2, java.math.RoundingMode.HALF_UP);
                campaign.setRoi(roi);
            }
            
            campaignRepository.save(campaign);
        }
    }

    @Transactional
    public Campaign generateAiContent(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));
        
        // Mock AI Generation
        String content = String.format("""
            {
              "wechat_title": "🔥 Limited Time Offer: %s!",
              "sms_body": "Don't miss out on %s. Click here: bit.ly/sportant",
              "email_subject": "Exclusive for you: %s"
            }
            """, campaign.getName(), campaign.getName(), campaign.getName());
            
        campaign.setAiGeneratedContent(content);
        return campaignRepository.save(campaign);
    }
}
