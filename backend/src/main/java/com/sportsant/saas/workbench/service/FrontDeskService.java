package com.sportsant.saas.workbench.service;

import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FrontDeskService {

    @Autowired
    private MemberRepository memberRepository;

    public Map<String, Object> getFrontDeskOverview() {
        // Mock data
        Map<String, Object> data = new HashMap<>();
        data.put("todayTarget", 5000.00);
        data.put("currentSales", 1250.00);
        data.put("pendingCheckins", 3);
        data.put("pendingVerifications", 2);
        return data;
    }

    public Member quickCheckIn(String memberCode) {
        // Mock implementation
        // In real app: find member by code (QR/Phone), create check-in record
        return memberRepository.findAll().stream().findFirst().orElseThrow(() -> new RuntimeException("Member not found"));
    }
}
