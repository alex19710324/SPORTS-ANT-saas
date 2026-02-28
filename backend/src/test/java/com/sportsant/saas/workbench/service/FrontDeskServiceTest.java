package com.sportsant.saas.workbench.service;

import com.sportsant.saas.entity.ERole;
import com.sportsant.saas.entity.Role;
import com.sportsant.saas.entity.User;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.service.MembershipService;
import com.sportsant.saas.repository.RoleRepository;
import com.sportsant.saas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FrontDeskServiceTest {

    @Mock
    private MembershipService membershipService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private FrontDeskService frontDeskService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterMember_Success() {
        String name = "Test User";
        String phone = "13800000000";

        when(userRepository.existsByUsername(phone)).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(encoder.encode(any())).thenReturn("encodedPass");
        
        Role role = new Role();
        role.setName(ERole.ROLE_USER);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(role));

        User savedUser = new User();
        savedUser.setId(100L);
        savedUser.setUsername(phone);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Member member = new Member();
        member.setId(1L);
        member.setName(name);
        member.setPhoneNumber(phone);
        when(membershipService.createMember(eq(100L), eq(name), eq(phone))).thenReturn(member);

        Member result = frontDeskService.registerMember(name, phone);

        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(phone, result.getPhoneNumber());
        
        verify(userRepository, times(1)).save(any(User.class));
        verify(membershipService, times(1)).createMember(eq(100L), eq(name), eq(phone));
    }

    @Test
    public void testRegisterMember_UserExists() {
        String phone = "13800000000";
        when(userRepository.existsByUsername(phone)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> frontDeskService.registerMember("Test", phone));
        
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testProcessSale_Success() {
        String code = "M12345";
        Double amount = 100.0;
        
        Member member = new Member();
        member.setId(1L);
        member.setUserId(100L);
        member.setMemberCode(code);
        
        when(membershipService.findMemberByCodeOrPhone(code)).thenReturn(member);
        when(membershipService.simulatePurchase(100L, 100)).thenReturn(member);
        
        Member result = frontDeskService.processSale(code, amount);
        
        assertNotNull(result);
        verify(membershipService, times(1)).simulatePurchase(100L, 100);
    }
}
