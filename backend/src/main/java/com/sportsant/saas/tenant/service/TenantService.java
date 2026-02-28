package com.sportsant.saas.tenant.service;

import com.sportsant.saas.tenant.entity.Tenant;
import com.sportsant.saas.tenant.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    public Optional<Tenant> getTenantById(Long id) {
        return tenantRepository.findById(id);
    }

    @Transactional
    public Tenant createTenant(String name, String domain, String plan) {
        if (tenantRepository.findByDomain(domain).isPresent()) {
            throw new RuntimeException("Domain already exists");
        }

        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setDomain(domain);
        tenant.setSubscriptionPlan(plan);
        tenant.setStatus("ACTIVE");
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setUpdatedAt(LocalDateTime.now());
        
        // Simple logic for end date
        if ("FREE".equals(plan)) {
            tenant.setSubscriptionEndDate(LocalDateTime.now().plusMonths(1));
        } else {
            tenant.setSubscriptionEndDate(LocalDateTime.now().plusYears(1));
        }

        return tenantRepository.save(tenant);
    }

    @Transactional
    public Tenant updateStatus(Long id, String status) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        tenant.setStatus(status);
        tenant.setUpdatedAt(LocalDateTime.now());
        return tenantRepository.save(tenant);
    }
}
