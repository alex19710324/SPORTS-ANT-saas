package com.sportsant.saas.tenant.controller;

import com.sportsant.saas.tenant.entity.Tenant;
import com.sportsant.saas.tenant.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/saas/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SaaSAdminController {

    @Autowired
    private TenantService tenantService;

    @GetMapping("/tenants")
    @PreAuthorize("hasRole('ADMIN')") // In real SaaS, this would be SUPER_ADMIN
    public List<Tenant> getAllTenants() {
        return tenantService.getAllTenants();
    }

    @PostMapping("/tenants")
    @PreAuthorize("hasRole('ADMIN')")
    public Tenant createTenant(@RequestBody Map<String, String> payload) {
        return tenantService.createTenant(
            payload.get("name"),
            payload.get("domain"),
            payload.get("plan")
        );
    }

    @PutMapping("/tenants/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Tenant updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return tenantService.updateStatus(id, payload.get("status"));
    }
}
