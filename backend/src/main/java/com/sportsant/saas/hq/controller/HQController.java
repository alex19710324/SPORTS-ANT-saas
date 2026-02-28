package com.sportsant.saas.hq.controller;

import com.sportsant.saas.franchise.entity.FranchiseApplication;
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

    // --- P0: Global Overview ---
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ_MANAGER')")
    public Map<String, Object> getGlobalOverview() {
        return hqService.getGlobalOverview();
    }

    @GetMapping("/stores")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ_MANAGER')")
    public Map<String, Object> getStoreMapData() {
        return Map.of("stores", hqService.getStoreMapData());
    }

    // --- P0: Franchise Management ---
    @GetMapping("/applications")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ_MANAGER')")
    public Map<String, Object> getFranchiseApplications() {
        return Map.of("applications", hqService.getFranchiseApplications());
    }

    @PostMapping("/franchise/apply")
    public FranchiseApplication submitApplication(@RequestBody FranchiseApplication app) {
        return hqService.submitFranchiseApplication(app);
    }

    @PostMapping("/applications/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public FranchiseApplication approveApplication(@PathVariable Long id) {
        return hqService.approveFranchise(id, true, "Approved via Dashboard");
    }
}
