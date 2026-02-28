package com.sportsant.saas.workbench.controller;

import com.sportsant.saas.ai.entity.AiSuggestion;
import com.sportsant.saas.ai.service.AiBrainService;
import com.sportsant.saas.workbench.service.StoreManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workbench/manager")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StoreManagerController {

    @Autowired
    private StoreManagerService storeManagerService;

    @Autowired
    private AiBrainService aiBrainService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('STORE_MANAGER') or hasRole('ADMIN')")
    public Map<String, Object> getOverview() {
        // In a real app, we would get the storeId from the authenticated user's context
        Long mockStoreId = 1L; 
        return storeManagerService.getStoreOverview(mockStoreId);
    }

    @PutMapping("/approvals/{id}/approve")
    @PreAuthorize("hasRole('STORE_MANAGER') or hasRole('ADMIN')")
    public String approveRequest(@PathVariable Long id) {
        Long mockManagerId = 10L;
        storeManagerService.approveRequest(id, mockManagerId);
        return "Request approved successfully";
    }

    @GetMapping("/suggestions")
    @PreAuthorize("hasRole('STORE_MANAGER') or hasRole('ADMIN')")
    public List<AiSuggestion> getAiSuggestions() {
        return aiBrainService.getSuggestions();
    }
}
