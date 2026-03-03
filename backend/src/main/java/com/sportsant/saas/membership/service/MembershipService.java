package com.sportsant.saas.membership.service;

import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.common.context.UserContext;
import com.sportsant.saas.globalization.entity.InternationalizationProfile;
import com.sportsant.saas.globalization.repository.InternationalizationProfileRepository;
import com.sportsant.saas.globalization.service.ComplianceValidator;
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

    @Autowired
    private ComplianceValidator complianceValidator;

    @Autowired
    private InternationalizationProfileRepository profileRepository;

    public Member getMember(Long userId) {
        return memberRepository.findByUserId(userId)
                .orElseGet(() -> initializeMember(userId));
    }

    private Member initializeMember(Long userId) {
        Member member = new Member();
        member.setUserId(userId);
        
        // Auto-detect context for new member initialization
        String currentLocale = UserContext.getLocale();
        member.setLocale(currentLocale);
        
        // Try to get timezone from profile
        if (currentLocale != null) {
            profileRepository.findByLocale(currentLocale).ifPresent(p -> {
                if (p.getTimezone() != null) member.setTimezone(p.getTimezone());
            });
        }
        if (member.getTimezone() == null) {
            member.setTimezone("UTC");
        }
        
        // Default to level 1
        MemberLevel level1 = memberLevelRepository.findByLevelOrder(1)
                .orElseThrow(() -> new RuntimeException("Level 1 not configured"));
        member.setCurrentLevel(level1);
        return memberRepository.save(member);
    }

    public Member findMemberByCodeOrPhone(String code) {
        return memberRepository.findByMemberCode(code)
                .or(() -> memberRepository.findByPhoneNumber(code))
                .orElseThrow(() -> new RuntimeException("Member not found with code/phone: " + code));
    }

    @Transactional
    public Member createMember(Long userId, String name, String phoneNumber, String idCard, String locale) {
        // 1. Compliance Validation (Forced Check)
        if (locale == null) locale = "en-US";
        
        if (phoneNumber != null) {
            ComplianceValidator.ValidationResult phoneRes = complianceValidator.validatePhone(locale, phoneNumber);
            if (!phoneRes.valid) {
                throw new RuntimeException("Compliance Error: Invalid phone number for " + locale + ": " + phoneRes.message);
            }
        }
        if (idCard != null) {
            ComplianceValidator.ValidationResult idRes = complianceValidator.validateIdCard(locale, idCard);
            if (!idRes.valid) {
                throw new RuntimeException("Compliance Error: Invalid ID Card for " + locale + ": " + idRes.message);
            }
        }

        if (memberRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new RuntimeException("Phone number already registered as member: " + phoneNumber);
        }

        Member member = new Member();
        member.setUserId(userId);
        member.setName(name);
        member.setPhoneNumber(phoneNumber);
        member.setIdCard(idCard);
        member.setLocale(locale);
        
        // Auto-set Timezone from Profile
        String finalLocale = locale;
        InternationalizationProfile profile = profileRepository.findByLocale(finalLocale)
                .orElse(null);
        if (profile != null && profile.getTimezone() != null) {
            member.setTimezone(profile.getTimezone());
        } else {
            member.setTimezone("UTC"); // Fallback
        }
        
        // Generate unique member code (simple random for now)
        String code = "M" + System.currentTimeMillis() % 1000000;
        member.setMemberCode(code);

        // Default to level 1
        MemberLevel level1 = memberLevelRepository.findByLevelOrder(1)
                .orElseThrow(() -> new RuntimeException("Level 1 not configured"));
        member.setCurrentLevel(level1);
        
        return memberRepository.save(member);
    }

    @Transactional
    public Member dailyCheckIn(Long userId) {
        // Daily Check-in gives 10 Growth
        addGrowth(userId, 10, "Daily Check-in");
        
        Member member = getMember(userId);
        member.setLastVisitDate(java.time.LocalDateTime.now());
        memberRepository.save(member);
        
        return member;
    }

    @Transactional
    public void topUpBalance(Long memberId, java.math.BigDecimal amount) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        
        if (amount == null || amount.compareTo(java.math.BigDecimal.ZERO) <= 0) return;
        
        member.setBalance(member.getBalance() + amount.doubleValue());
        
        // Add Top-Up Growth
        addGrowth(member.getUserId(), amount.intValue(), "Top-Up Reward");
        
        memberRepository.save(member);
    }

    @Transactional
    public void topUp(Long userId, Double amount) {
        Member member = getMember(userId);
        if (amount == null || amount <= 0) return;
        
        member.setBalance(member.getBalance() + amount);
        
        // Add Top-Up Growth (Ratio could be different, e.g. 1:1)
        addGrowth(userId, amount.intValue(), "Top-Up Reward");
        
        memberRepository.save(member);
    }

    @Transactional
    public void deductBalance(Long userId, Double amount) {
        Member member = getMember(userId);
        if (amount == null || amount <= 0) return;
        
        if (member.getBalance() < amount) {
            throw new RuntimeException("Insufficient Balance");
        }
        
        member.setBalance(member.getBalance() - amount);
        memberRepository.save(member);
    }

    @Transactional
    public Member simulatePurchase(Long userId, Integer amount) {
        // Purchase gives growth = amount * 1.5 (Mock ratio)
        if (amount == null || amount <= 0) amount = 100;
        int growth = (int) (amount * 1.5);
        addGrowth(userId, growth, "Purchase Reward");
        
        // Also give points = amount
        Member member = getMember(userId);
        member.setPoints(member.getPoints() + amount);
        memberRepository.save(member);
        
        return getMember(userId);
    }

    @Transactional
    public void redeemPoints(Long userId, Integer points) {
        Member member = getMember(userId);
        if (member.getPoints() < points) {
            throw new RuntimeException("Insufficient points");
        }
        member.setPoints(member.getPoints() - points);
        memberRepository.save(member);
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
