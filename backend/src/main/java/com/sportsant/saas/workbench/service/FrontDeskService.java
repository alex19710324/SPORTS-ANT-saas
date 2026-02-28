package com.sportsant.saas.workbench.service;

import com.sportsant.saas.entity.ERole;
import com.sportsant.saas.entity.Role;
import com.sportsant.saas.entity.User;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.service.MembershipService;
import com.sportsant.saas.repository.RoleRepository;
import com.sportsant.saas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class FrontDeskService {

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

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
        Member member = membershipService.findMemberByCodeOrPhone(memberCode);
        return membershipService.dailyCheckIn(member.getUserId());
    }

    @Transactional
    public Member registerMember(String name, String phoneNumber) {
        // 1. Check if user exists by phone (username)
        if (userRepository.existsByUsername(phoneNumber)) {
            throw new RuntimeException("User with this phone number already exists.");
        }

        // 2. Create User
        String email = phoneNumber + "@member.local";
        if (userRepository.existsByEmail(email)) {
            // Should be rare if username is unique, but check anyway
            email = phoneNumber + "_" + System.currentTimeMillis() + "@member.local";
        }

        User user = new User(phoneNumber, email, encoder.encode("123456"));
        
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));
        roles.add(userRole);
        user.setRoles(roles);
        
        user = userRepository.save(user);

        // 3. Create Member
        return membershipService.createMember(user.getId(), name, phoneNumber);
    }

    public Member processSale(String memberCode, Double amount) {
        Member member = membershipService.findMemberByCodeOrPhone(memberCode);
        return membershipService.simulatePurchase(member.getUserId(), amount.intValue());
    }
}
