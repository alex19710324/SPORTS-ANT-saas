package com.sportsant.saas.marketing.service;

import com.sportsant.saas.ai.entity.MemberChurnPrediction;
import com.sportsant.saas.ai.service.AdvancedChurnPredictionService;
import com.sportsant.saas.marketing.entity.Campaign;
import com.sportsant.saas.marketing.repository.CampaignRepository;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.repository.MemberRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AutoRetentionService {
    
    private final CampaignRepository campaignRepository;
    private final MemberRepository memberRepository;
    private final AdvancedChurnPredictionService churnPredictionService;
    private final CouponService couponService;
    private final SmsService smsService;
    
    public AutoRetentionService(CampaignRepository campaignRepository,
                               MemberRepository memberRepository,
                               AdvancedChurnPredictionService churnPredictionService,
                               CouponService couponService,
                               SmsService smsService) {
        this.campaignRepository = campaignRepository;
        this.memberRepository = memberRepository;
        this.churnPredictionService = churnPredictionService;
        this.couponService = couponService;
        this.smsService = smsService;
    }
    
    /**
     * 一键执行会员挽回
     */
    @Transactional
    public Campaign executeRetentionCampaign(String memberId, String tenantId) {
        System.out.println("开始执行会员挽回活动: memberId=" + memberId + ", tenantId=" + tenantId);
        
        // 1. 获取会员信息
        Member member = memberRepository.findById(Long.parseLong(memberId))
            .orElseThrow(() -> new RuntimeException("会员不存在: " + memberId));
        
        // 2. 获取流失预测数据
        MemberChurnPrediction prediction = churnPredictionService.predictMemberChurn(Long.parseLong(memberId));
        
        // 3. 创建营销活动记录
        Campaign campaign = new Campaign();
        campaign.setType("CHURN_RETENTION");
        campaign.setName("自动挽回 - " + member.getName());
        // campaign.setDescription("针对高风险流失会员的自动挽回活动"); // Description not available in Campaign entity
        campaign.setAiGeneratedContent("针对高风险流失会员的自动挽回活动"); // Use this field instead or ignore
        campaign.setTargetAudience("CHURN_RISK");
        
        // 4. 生成优惠券
        String couponCode = generateCouponCode();
        BigDecimal amount = calculateCouponAmount(prediction.getChurnProbability());
        campaign.setBudget(amount); // Use budget as coupon amount for simplicity
        campaign.setCurrency("CNY");
        campaign.setStartDate(LocalDateTime.now());
        campaign.setEndDate(LocalDateTime.now().plusDays(30));
        
        // 5. 准备短信内容
        // Store SMS content in campaign description or a separate field if available
        String smsContent = buildSmsContent(member.getName(), couponCode, amount);
        
        campaign.setStatus("EXECUTING");
        campaignRepository.save(campaign);
        
        // 6. 异步执行营销活动
        executeCampaignAsync(campaign, member, couponCode, amount, smsContent);
        
        return campaign;
    }
    
    /**
     * 批量执行高风险会员挽回
     */
    @Transactional
    public List<Campaign> batchExecuteRetention(String tenantId, int limit) {
        System.out.println("批量执行会员挽回: tenantId=" + tenantId + ", limit=" + limit);
        
        // 获取高风险会员列表
        List<MemberChurnPrediction> highRiskMembers = 
            churnPredictionService.predictHighRiskMembers(limit);
        
        return highRiskMembers.stream()
            .map(prediction -> executeRetentionCampaign(String.valueOf(prediction.getMemberId()), tenantId))
            .collect(Collectors.toList());
    }
    
    /**
     * 异步执行营销活动（优惠券+短信）
     */
    @Async
    public void executeCampaignAsync(Campaign campaign, Member member, String couponCode, BigDecimal amount, String smsContent) {
        try {
            System.out.println("开始异步执行营销活动: campaignId=" + campaign.getId());
            
            // 1. 发放优惠券
            boolean couponIssued = couponService.issueCoupon(
                String.valueOf(member.getId()),
                couponCode,
                amount,
                "CNY",
                LocalDateTime.now().plusDays(30),
                "流失挽回礼包"
            );
            
            if (couponIssued) {
                campaign.setConversionCount(0); // 初始化为0，等待使用
            }
            
            // 2. 发送召回短信
            boolean smsSent = smsService.sendRetentionSms(
                member.getPhoneNumber(),
                smsContent,
                "CHURN_RETENTION_01"
            );
            
            if (smsSent) {
                // campaign.setSmsDelivered(1); // If field exists
            }
            
            // 3. 更新活动状态
            campaign.setStatus("ACTIVE");
            
        } catch (Exception e) {
            System.out.println("营销活动执行失败: campaignId=" + campaign.getId() + ", error=" + e.getMessage());
            campaign.setStatus("FAILED");
        } finally {
            campaignRepository.save(campaign);
            System.out.println("营销活动执行完成: campaignId=" + campaign.getId() + ", status=" + campaign.getStatus());
        }
    }
    
    // 辅助方法
    private String generateCouponCode() {
        return "SAVE" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private BigDecimal calculateCouponAmount(Double churnProbability) {
        // 根据流失概率计算优惠券金额
        if (churnProbability > 0.8) {
            return BigDecimal.valueOf(100); // 80%以上流失风险，给100元优惠券
        } else if (churnProbability > 0.6) {
            return BigDecimal.valueOf(50);  // 60-80%风险，给50元
        } else {
            return BigDecimal.valueOf(30);  // 其他，给30元
        }
    }
    
    private String buildSmsContent(String memberName, String couponCode, BigDecimal amount) {
        return String.format(
            "【运动蚂蚁】亲爱的%s，我们为您准备了一份%s元回归礼包！优惠码：%s，有效期30天。期待您的再次光临！退订回T",
            memberName, amount, couponCode
        );
    }
}
