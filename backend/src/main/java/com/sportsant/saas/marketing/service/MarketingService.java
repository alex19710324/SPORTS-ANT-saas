package com.sportsant.saas.marketing.service;

import com.sportsant.saas.communication.service.CommunicationService;
import com.sportsant.saas.marketing.entity.Campaign;
import com.sportsant.saas.marketing.entity.Coupon;
import com.sportsant.saas.marketing.repository.CampaignRepository;
import com.sportsant.saas.marketing.repository.CouponRepository;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MarketingService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommunicationService communicationService;

    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    @Transactional
    public Campaign createCampaign(Campaign campaign) {
        campaign.setStatus("DRAFT");
        return campaignRepository.save(campaign);
    }

    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Transactional
    public void launchCampaign(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        if (!"DRAFT".equals(campaign.getStatus())) {
            throw new RuntimeException("Campaign is not in DRAFT status");
        }

        List<Member> targets;
        if ("ACTIVE_MEMBERS".equals(campaign.getTargetSegment())) {
            targets = memberRepository.findByStatus("ACTIVE");
        } else {
            // Simplified: Default to all for other segments in MVP
            targets = memberRepository.findAll();
        }

        // Send Notifications
        for (Member member : targets) {
            communicationService.sendNotification(
                member.getId(),
                "Special Offer: " + campaign.getName(),
                campaign.getDescription(),
                "MARKETING"
            );
        }

        campaign.setStatus("ACTIVE");
        campaign.setSentCount(targets.size());
        campaign.setStartDate(LocalDate.now());
        campaignRepository.save(campaign);
    }

    @Transactional
    public Coupon validateCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid Coupon Code"));

        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Coupon Expired");
        }

        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new RuntimeException("Coupon Usage Limit Reached");
        }

        return coupon;
    }

    @Transactional
    public void recordConversion(String code) {
         Coupon coupon = couponRepository.findByCode(code).orElse(null);
         if (coupon != null) {
             coupon.setUsedCount(coupon.getUsedCount() + 1);
             couponRepository.save(coupon);
             
             if (coupon.getCampaign() != null) {
                 Campaign c = coupon.getCampaign();
                 c.setConvertedCount(c.getConvertedCount() + 1);
                 campaignRepository.save(c);
             }
         }
    }
}
