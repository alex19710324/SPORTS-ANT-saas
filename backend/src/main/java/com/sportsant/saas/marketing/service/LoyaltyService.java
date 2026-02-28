package com.sportsant.saas.marketing.service;

import com.sportsant.saas.marketing.entity.Reward;
import com.sportsant.saas.marketing.repository.RewardRepository;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LoyaltyService {

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private MemberRepository memberRepository;

    public List<Reward> getAvailableRewards() {
        return rewardRepository.findByActiveTrue();
    }

    @Transactional
    public void redeemReward(Long memberId, Long rewardId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        if (member.getPoints() < reward.getPointsCost()) {
            throw new RuntimeException("Insufficient points");
        }

        member.setPoints(member.getPoints() - reward.getPointsCost());
        memberRepository.save(member);

        // Here we could also log a RedemptionRecord entity for history
        System.out.println("Member " + member.getName() + " redeemed " + reward.getName());
    }

    @Transactional
    public Reward createReward(String name, String desc, Integer cost) {
        Reward reward = new Reward();
        reward.setName(name);
        reward.setDescription(desc);
        reward.setPointsCost(cost);
        reward.setActive(true);
        return rewardRepository.save(reward);
    }
}
