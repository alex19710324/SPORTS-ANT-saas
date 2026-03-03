package com.sportsant.saas.points.service;

import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.repository.MemberRepository;
import com.sportsant.saas.points.entity.PointsRule;
import com.sportsant.saas.points.entity.PointsTransaction;
import com.sportsant.saas.points.repository.PointsRuleRepository;
import com.sportsant.saas.points.repository.PointsTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PointsService {

    @Autowired
    private PointsRuleRepository ruleRepository;

    @Autowired
    private PointsTransactionRepository transactionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public void earnPoints(String userId, String ruleCode, BigDecimal amount, String refId) {
        // 1. Get Rule
        PointsRule rule = ruleRepository.findByCode(ruleCode).orElse(null);
        if (rule == null || !rule.isEnabled()) {
            System.out.println("Points rule not found or disabled: " + ruleCode);
            return; // Or throw exception
        }

        // 2. Calculate Points
        int points = 0;
        if (rule.getFixedPoints() != null && rule.getFixedPoints() > 0) {
            points = rule.getFixedPoints();
        } else if (rule.getRatio() != null && amount != null) {
            points = (int) (amount.doubleValue() * rule.getRatio());
        }

        if (points <= 0) return;

        // 3. Check Daily Limit
        if (rule.getDailyLimit() != null && rule.getDailyLimit() > 0) {
            LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            List<PointsTransaction> dailyTxns = transactionRepository.findByUserIdAndRuleCodeAndCreatedAtAfter(userId, ruleCode, today);
            int todayPoints = dailyTxns.stream().mapToInt(PointsTransaction::getPoints).sum();
            
            if (todayPoints >= rule.getDailyLimit()) {
                System.out.println("Daily points limit reached for user: " + userId);
                return;
            }
            
            // Adjust if adding this exceeds limit
            if (todayPoints + points > rule.getDailyLimit()) {
                points = rule.getDailyLimit() - todayPoints;
            }
        }

        // 4. Update Member Balance
        Member member = memberRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new RuntimeException("Member not found"));
        member.setPoints(member.getPoints() + points);
        memberRepository.save(member);

        // 5. Record Transaction
        PointsTransaction txn = new PointsTransaction();
        txn.setUserId(userId);
        txn.setType("EARN");
        txn.setRuleCode(ruleCode);
        txn.setPoints(points);
        txn.setAmount(amount);
        txn.setReferenceId(refId);
        transactionRepository.save(txn);
    }

    @Transactional
    public void burnPoints(String userId, String ruleCode, int points, String refId) {
        Member member = memberRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new RuntimeException("Member not found"));
        
        if (member.getPoints() < points) {
            throw new RuntimeException("Insufficient points");
        }

        member.setPoints(member.getPoints() - points);
        memberRepository.save(member);

        PointsTransaction txn = new PointsTransaction();
        txn.setUserId(userId);
        txn.setType("BURN");
        txn.setRuleCode(ruleCode);
        txn.setPoints(-points); // Negative for burn
        txn.setReferenceId(refId);
        transactionRepository.save(txn);
    }

    public List<PointsTransaction> getHistory(String userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public PointsRule createRule(PointsRule rule) {
        return ruleRepository.save(rule);
    }

    public List<PointsRule> getAllRules() {
        return ruleRepository.findAll();
    }
}
