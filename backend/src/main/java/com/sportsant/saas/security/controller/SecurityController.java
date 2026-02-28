package com.sportsant.saas.security.controller;

import com.sportsant.saas.security.blockchain.AuditLedgerService;
import com.sportsant.saas.security.policy.ZeroTrustPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SecurityController {

    @Autowired
    private AuditLedgerService auditLedgerService;

    @Autowired
    private ZeroTrustPolicyService zeroTrustPolicyService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getSecurityDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Mock Real-time Threat Map Data
        dashboard.put("active_threats", 0);
        dashboard.put("blocked_ips", 5); // Mock
        dashboard.put("failed_logins_24h", 12);
        
        // Blockchain Status
        boolean ledgerIntegrity = auditLedgerService.verifyIntegrity();
        dashboard.put("ledger_integrity", ledgerIntegrity ? "SECURE" : "COMPROMISED");
        
        // WAF Status
        dashboard.put("waf_status", "ACTIVE");
        dashboard.put("waf_blocks_24h", 3);

        return dashboard;
    }

    // Emergency Panic Button
    @PostMapping("/lockdown")
    @PreAuthorize("hasRole('ADMIN')")
    public void activateLockdown(@RequestBody Map<String, String> payload) {
        String reason = payload.getOrDefault("reason", "Manual Admin Trigger");
        zeroTrustPolicyService.activateGlobalLockdown(reason);
    }

    @PostMapping("/lockdown/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivateLockdown() {
        zeroTrustPolicyService.deactivateGlobalLockdown();
    }
}
