package com.sportsant.saas.membership.controller;

import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.service.MembershipService;
import com.sportsant.saas.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/membership")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Member getMyMembership(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membershipService.getMember(userDetails.getId());
    }

    // Admin endpoint to simulate growth (for demo)
    @PostMapping("/growth/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addGrowth(@RequestBody java.util.Map<String, Object> payload) {
        Long userId = Long.valueOf(payload.get("userId").toString());
        Integer amount = (Integer) payload.get("amount");
        String source = (String) payload.get("source");
        
        membershipService.addGrowth(userId, amount, source);
        return ResponseEntity.ok("Growth added successfully");
    }
}
