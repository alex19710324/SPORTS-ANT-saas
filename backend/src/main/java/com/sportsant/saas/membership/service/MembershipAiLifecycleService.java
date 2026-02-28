package com.sportsant.saas.membership.service;

import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.ai.service.AiInferenceService;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MembershipAiLifecycleService implements AiAware {
    private static final Logger logger = LoggerFactory.getLogger(MembershipAiLifecycleService.class);

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AiInferenceService aiInferenceService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * Daily job to analyze all members for churn risk and segmentation.
     * This embodies the "AI-First" principle: proactive analysis rather than reactive.
     */
    @Scheduled(cron = "0 0 2 * * ?") // 2 AM Daily
    public void runDailyAiAnalysis() {
        logger.info("Starting Daily AI Membership Analysis...");
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            // 1. Churn Prediction
            predictChurn(member);
            
            // 2. Segmentation (Mock for now)
            // updateSegmentation(member);
        }
        logger.info("Daily AI Membership Analysis Completed.");
    }

    private void predictChurn(Member member) {
        try {
            // Call AI Base for inference
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) aiInferenceService.predict("churn-prediction-v1", Map.of(
                "userId", member.getUserId(),
                "growthValue", member.getGrowthValue(),
                "lastActive", "2023-10-01" // Mock data, in real life fetch from Feature Store
            ));

            String riskLevel = (String) result.get("prediction");
            if ("HIGH_RISK".equals(riskLevel)) {
                // If high risk, notify AI Brain to take action
                eventPublisher.publishEvent(createEvent("MEMBER_CHURN_RISK", Map.of(
                    "userId", member.getUserId(),
                    "riskLevel", riskLevel,
                    "confidence", result.get("confidence")
                )));
                
                // Update local tags
                updateMemberTags(member, "CHURN_RISK");
            }
        } catch (Exception e) {
            logger.error("Failed to predict churn for user " + member.getUserId(), e);
        }
    }

    private void updateMemberTags(Member member, String tag) {
        String currentTags = member.getTags() == null ? "" : member.getTags();
        if (!currentTags.contains(tag)) {
            member.setTags(currentTags.isEmpty() ? tag : currentTags + "," + tag);
            memberRepository.save(member);
        }
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        // Handle suggestions specific to lifecycle (e.g. "Run Analysis Now")
    }
}
