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
        
        if (payload.containsKey("cartItems")) {
            // New Cart Logic
            java.util.List<Map<String, Object>> cartItems = (java.util.List<Map<String, Object>>) payload.get("cartItems");
            String method = (String) payload.get("paymentMethod");
            return frontDeskService.processCartSale(code, cartItems, method);
        } else {
            // Legacy Logic
            Object amountObj = payload.get("amount");
            if (code == null || amountObj == null) {
                throw new RuntimeException("Member Code and Amount are required");
            }
            Double amount = Double.valueOf(amountObj.toString());
            return frontDeskService.processSale(code, amount);
        }
    }
}
