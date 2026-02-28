package com.sportsant.saas.membership.service;

import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.entity.MemberLevel;
import com.sportsant.saas.membership.repository.MemberLevelRepository;
import com.sportsant.saas.membership.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MembershipServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberLevelRepository memberLevelRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MembershipService membershipService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindMemberByCodeOrPhone_Success_ByCode() {
        Member member = new Member();
        member.setMemberCode("12345");
        when(memberRepository.findByMemberCode("12345")).thenReturn(Optional.of(member));

        Member found = membershipService.findMemberByCodeOrPhone("12345");
        assertNotNull(found);
        assertEquals("12345", found.getMemberCode());
    }

    @Test
    public void testFindMemberByCodeOrPhone_Success_ByPhone() {
        Member member = new Member();
        member.setPhoneNumber("13800138000");
        when(memberRepository.findByMemberCode("13800138000")).thenReturn(Optional.empty());
        when(memberRepository.findByPhoneNumber("13800138000")).thenReturn(Optional.of(member));

        Member found = membershipService.findMemberByCodeOrPhone("13800138000");
        assertNotNull(found);
        assertEquals("13800138000", found.getPhoneNumber());
    }

    @Test
    public void testFindMemberByCodeOrPhone_NotFound() {
        when(memberRepository.findByMemberCode(any())).thenReturn(Optional.empty());
        when(memberRepository.findByPhoneNumber(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> membershipService.findMemberByCodeOrPhone("unknown"));
    }

    @Test
    public void testDailyCheckIn_AddsGrowth() {
        Member member = new Member();
        member.setUserId(1L);
        member.setGrowthValue(0);
        
        MemberLevel level1 = new MemberLevel();
        level1.setLevelOrder(1);
        level1.setRequiredGrowth(0);
        member.setCurrentLevel(level1);

        when(memberRepository.findByUserId(1L)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenAnswer(i -> i.getArguments()[0]);

        Member updatedMember = membershipService.dailyCheckIn(1L);
        
        assertEquals(10, updatedMember.getGrowthValue());
        // Verify that publishEvent was called with any object
        verify(eventPublisher, times(1)).publishEvent(any(Object.class));
    }
}
