package com.sportsant.saas.marketing.service;

import com.sportsant.saas.marketing.entity.Campaign;
import com.sportsant.saas.marketing.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MarketingServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private MarketingService marketingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateCampaign() {
        Campaign campaign = new Campaign();
        campaign.setName("Summer Sale");
        campaign.setType("GROUP_BUY");
        
        when(campaignRepository.save(any(Campaign.class))).thenAnswer(i -> {
            Campaign c = i.getArgument(0);
            c.setId(1L);
            return c;
        });
        
        Campaign created = marketingService.createCampaign(campaign);
        
        assertNotNull(created.getId());
        assertEquals("DRAFT", created.getStatus());
        assertEquals("Summer Sale", created.getName());
    }

    @Test
    public void testGenerateAiContent() {
        Long campaignId = 1L;
        Campaign campaign = new Campaign();
        campaign.setId(campaignId);
        campaign.setName("Flash Sale Event");
        campaign.setType("FLASH_SALE");
        
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any(Campaign.class))).thenAnswer(i -> i.getArgument(0));
        
        Campaign updated = marketingService.generateAiContent(campaignId);
        
        assertNotNull(updated.getAiGeneratedContent());
        assertTrue(updated.getAiGeneratedContent().contains("wechat_title"));
        assertTrue(updated.getAiGeneratedContent().contains("Flash Sale Event"));
    }
}
