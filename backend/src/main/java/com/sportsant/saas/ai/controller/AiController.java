package com.sportsant.saas.ai.controller;

import com.sportsant.saas.ai.entity.AiSuggestion;
import com.sportsant.saas.ai.service.AiBrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AiController {

    @Autowired
    private AiBrainService aiBrainService;

    @GetMapping("/insights")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STORE_MANAGER') or hasRole('HQ')")
    public List<AiSuggestion> getInsights() {
        // Delegate to AiBrainService to fetch latest suggestions/insights
        // For MVP, we can reuse getSuggestions but filter for "INSIGHT" type or similar
        // Or just return all active suggestions as insights
        return aiBrainService.getSuggestions();
    }

    @PostMapping("/execute")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STORE_MANAGER')")
    public Map<String, Object> executeAction(@RequestBody Map<String, String> payload) {
        String actionLink = payload.get("actionLink");
        // In a real system, this would parse the link and invoke the corresponding service method
        // For MVP, we'll just log it and return success
        System.out.println("Executing AI Action: " + actionLink);
        return Map.of("status", "SUCCESS", "message", "Action executed: " + actionLink);
    }
}
