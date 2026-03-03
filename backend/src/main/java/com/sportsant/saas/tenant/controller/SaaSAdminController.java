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

    @PutMapping("/tenants/{id}/config")
    @PreAuthorize("hasRole('ADMIN')")
    public Tenant updateConfig(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Double rate = payload.get("rate") != null ? Double.valueOf(payload.get("rate").toString()) : null;
        Integer cycle = payload.get("cycle") != null ? Integer.valueOf(payload.get("cycle").toString()) : null;
        String permissions = payload.get("permissions") != null ? payload.get("permissions").toString() : null;
        
        // Handle array permissions if frontend sends array
        Object permissionsObj = payload.get("permissions");
        if (permissionsObj instanceof List<?>) {
            List<?> list = (List<?>) permissionsObj;
            if (!list.isEmpty() && list.get(0) instanceof String) {
                @SuppressWarnings("unchecked")
                List<String> strList = (List<String>) list;
                permissions = String.join(",", strList);
            }
        }

        return tenantService.updateConfig(id, rate, cycle, permissions);
    }

    @PostMapping("/tenants/{id}/appkey")
    @PreAuthorize("hasRole('ADMIN')")
    public Tenant generateAppKey(@PathVariable Long id) {
        return tenantService.generateAppKey(id);
    }
}
