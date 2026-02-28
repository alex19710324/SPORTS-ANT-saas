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
    @GetMapping("/dashboard/overview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ_MANAGER')")
    public Map<String, Object> getGlobalOverview() {
        return hqService.getGlobalOverview();
    }

    @GetMapping("/map/stores")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ_MANAGER')")
    public Map<String, Object> getStoreMapData() {
        return Map.of("stores", hqService.getStoreMapData());
    }

    // --- P0: Franchise Management ---
    @PostMapping("/franchise/apply")
    public FranchiseApplication submitApplication(@RequestBody FranchiseApplication app) {
        return hqService.submitFranchiseApplication(app);
    }

    @PostMapping("/franchise/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public FranchiseApplication approveApplication(@RequestBody Map<String, Object> payload) {
        Long appId = Long.valueOf(payload.get("applicationId").toString());
        boolean approve = (boolean) payload.get("approve");
        String comments = (String) payload.get("comments");
        return hqService.approveFranchise(appId, approve, comments);
    }
}
