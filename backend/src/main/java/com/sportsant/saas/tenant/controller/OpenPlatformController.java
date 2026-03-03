package com.sportsant.saas.tenant.controller;

import com.sportsant.saas.common.response.Result;
import com.sportsant.saas.finance.service.FinanceService;
import com.sportsant.saas.tenant.entity.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/open")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OpenPlatformController {

    @Autowired
    private FinanceService financeService;

    @GetMapping("/test")
    public Result<String> testConnection() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Tenant) {
            Tenant tenant = (Tenant) auth.getPrincipal();
            return Result.success("Connected successfully as: " + tenant.getName());
        }
        return Result.error(401, "Invalid AppKey");
    }

    @PostMapping("/points/sync")
    public Result<Map<String, Object>> syncPoints(@RequestBody Map<String, Object> payload) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Tenant)) {
            return Result.error(401, "Invalid AppKey");
        }
        
        Tenant tenant = (Tenant) auth.getPrincipal();
        // Check permission MP03 (Points Interop)
        if (tenant.getPermissions() == null || !tenant.getPermissions().contains("MP03")) {
            return Result.error(403, "Permission denied: MP03 required");
        }

        // Mock logic for points sync: 
        // Assume points have a monetary value. e.g. 100 points = 1 RMB.
        // If merchant issues points (user earns), Merchant pays SaaS? Or SaaS owes Merchant?
        // Let's assume SaaS is the "Central Bank" of points. 
        // If user earns points at Merchant, SaaS issues points to user, Merchant pays SaaS for these points.
        // Amount = Points / 100 * Rate.
        
        Integer points = (Integer) payload.get("points");
        Double rate = tenant.getRate() != null ? tenant.getRate() : 1.0; // e.g. 1.0 = 100% cost, 0.6 = 60% cost
        Double amount = (points / 100.0) * rate;

        // Record financial transaction (Closed Loop: API -> Finance)
        financeService.processMerchantTransaction(
            tenant.getId(), 
            tenant.getAppKey(), 
            amount, 
            "INCOME", 
            "Points Sync Fee for " + points + " points"
        );

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("syncedPoints", points);
        response.put("userId", payload.get("userId"));
        response.put("rate", rate); 
        response.put("fee", amount);

        return Result.success(response);
    }
}
