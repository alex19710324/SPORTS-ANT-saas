package com.sportsant.saas.hq.controller;

import com.sportsant.saas.hq.service.HQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/hq")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HQController {

    @Autowired
    private HQService hqService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ')")
    public Map<String, Object> getDashboard() {
        return hqService.getExecutiveDashboard();
    }

    @GetMapping("/global-overview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ')")
    public Map<String, Object> getGlobalOverview() {
        return hqService.getGlobalOverview();
    }

    @GetMapping("/store-map")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ')")
    public Map<String, Object> getStoreMapData() {
        return Map.of("stores", hqService.getStoreMapData());
    }

    @GetMapping("/franchise/applications")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ')")
    public Map<String, Object> getFranchiseApplications() {
        return Map.of("applications", hqService.getFranchiseApplications());
    }

    @PostMapping("/franchise/applications/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ')")
    public com.sportsant.saas.franchise.entity.FranchiseApplication approveApplication(@PathVariable Long id) {
        return hqService.approveFranchise(id, true, "Approved via Controller");
    }
}
