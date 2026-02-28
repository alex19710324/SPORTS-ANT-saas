package com.sportsant.saas.workbench.controller;

import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.workbench.service.FrontDeskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workbench/frontdesk")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FrontDeskController {

    @Autowired
    private FrontDeskService frontDeskService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    public Map<String, Object> getOverview() {
        return frontDeskService.getFrontDeskOverview();
    }

    @PostMapping("/checkin")
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    public Member checkIn(@RequestBody Map<String, String> payload) {
        String code = payload.get("memberCode");
        return frontDeskService.quickCheckIn(code);
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    public Member register(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String phone = payload.get("phone");
        if (name == null || phone == null) {
            throw new RuntimeException("Name and Phone are required");
        }
        return frontDeskService.registerMember(name, phone);
    }

    @PostMapping("/sale")
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    public Member processSale(@RequestBody Map<String, Object> payload) {
        String code = (String) payload.get("memberCode");
        Double amount = Double.valueOf(payload.get("amount").toString());
        if (code == null || amount == null) {
            throw new RuntimeException("Member Code and Amount are required");
        }
        return frontDeskService.processSale(code, amount);
    }
}
