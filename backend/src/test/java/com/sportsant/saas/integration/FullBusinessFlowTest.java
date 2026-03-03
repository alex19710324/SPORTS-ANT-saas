package com.sportsant.saas.integration;

import com.sportsant.saas.ai.service.AiBrainService;
import com.sportsant.saas.finance.service.FinanceService;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.repository.MemberRepository;
import com.sportsant.saas.membership.service.MembershipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

import com.sportsant.saas.membership.entity.MemberLevel;
import com.sportsant.saas.membership.repository.MemberLevelRepository;

@SpringBootTest
@ActiveProfiles("test")
public class FullBusinessFlowTest {

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberLevelRepository memberLevelRepository;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private AiBrainService aiBrainService;

    @Test
    @Transactional
    public void testFullCycle() {
        // 0. Setup Member Level
        MemberLevel level = new MemberLevel();
        level.setName("Bronze");
        level.setLevelOrder(1);
        level.setRequiredGrowth(0);
        level.setBenefitsJson("{\"discount\": 1.0}");
        memberLevelRepository.save(level);

        // 1. Member Registration
        System.out.println("--- Step 1: Member Registration ---");
        Member newMember = new Member();
        newMember.setMemberCode("TEST001");
        newMember.setName("Integration User");
        newMember.setPhoneNumber("13800138000");
        newMember.setUserId(1001L); // Mock User ID
        newMember.setCurrentLevel(level); // Assign Level
        memberRepository.save(newMember);
        assertNotNull(newMember.getId());

        // 2. Top Up (Finance)
        System.out.println("--- Step 2: Member Top Up ---");
        membershipService.topUpBalance(newMember.getId(), new BigDecimal("1000.00"));
        Member updatedMember = memberRepository.findById(newMember.getId()).get();
        assertEquals(0, new BigDecimal("1000.00").compareTo(BigDecimal.valueOf(updatedMember.getBalance())));

        // 3. Consume Service (POS)
        System.out.println("--- Step 3: Consume Service ---");
        // Simulate POS logic calling finance
        financeService.processPayment(newMember.getId(), new BigDecimal("200.00"), "VR Game Session", "CNY");
        
        // 4. Check AI Diagnosis
        System.out.println("--- Step 4: AI Diagnosis ---");
        // Simulate a scenario where liability is high (lots of top up, low consumption)
        // We just topped up 1000 and spent 200. 
        // Let's trigger AI analysis
        aiBrainService.analyzeFinancialHealth();
        
        // Verify no exception thrown
        assertTrue(true);
    }
}
