package com.sportsant.saas.membership.service;

import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.entity.MemberLevel;
import com.sportsant.saas.membership.repository.MemberLevelRepository;
import com.sportsant.saas.membership.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class MembershipService implements AiAware {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberLevelRepository memberLevelRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public Member getMember(Long userId) {
        return memberRepository.findByUserId(userId)
                .orElseGet(() -> initializeMember(userId));
    }

    private Member initializeMember(Long userId) {
        Member member = new Member();
        member.setUserId(userId);
        // Default to level 1
        MemberLevel level1 = memberLevelRepository.findByLevelOrder(1)
                .orElseThrow(() -> new RuntimeException("Level 1 not configured"));
        member.setCurrentLevel(level1);
        return memberRepository.save(member);
    }

    @Transactional
    public void addGrowth(Long userId, Integer amount, String source) {
        Member member = getMember(userId);
        member.setGrowthValue(member.getGrowthValue() + amount);
        
        // Check for level up
        checkLevelUp(member);
        
        memberRepository.save(member);

        // Notify AI Brain (Perception)
        eventPublisher.publishEvent(createEvent("MEMBER_GROWTH", Map.of(
            "userId", userId,
            "amount", amount,
            "currentGrowth", member.getGrowthValue(),
            "source", source
        )));
    }

    private void checkLevelUp(Member member) {
        Optional<MemberLevel> nextLevelOpt = memberLevelRepository.findByLevelOrder(member.getCurrentLevel().getLevelOrder() + 1);
        if (nextLevelOpt.isPresent()) {
            MemberLevel nextLevel = nextLevelOpt.get();
            if (member.getGrowthValue() >= nextLevel.getRequiredGrowth()) {
                member.setCurrentLevel(nextLevel);
                // Trigger upgrade event
                eventPublisher.publishEvent(createEvent("MEMBER_LEVEL_UP", Map.of(
                    "userId", member.getUserId(),
                    "newLevel", nextLevel.getName()
                )));
            }
        }
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        // AI Suggests: "Prevent Churn" -> Send Coupon
        if ("CHURN_PREVENTION".equals(suggestionType)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) payload;
            Long userId = Long.valueOf(data.get("userId").toString());
            // Logic to send coupon...
            System.out.println("AI Action: Sending churn prevention coupon to user " + userId);
        }
    }
}
