package com.sportsant.saas.ai.controller;

import com.sportsant.saas.ai.entity.AiSuggestion;
import com.sportsant.saas.ai.service.AiBrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/brain")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AiBrainController {

    @Autowired
    private AiBrainService aiBrainService;

    // 1. Get current suggestions from the AI Brain
    @GetMapping("/suggestions")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AiSuggestion> getSuggestions() {
        return aiBrainService.getPendingSuggestions();
    }

    // 2. Feedback loop (Accept/Reject) - Evolution
    @PostMapping("/feedback/{suggestionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> provideFeedback(@PathVariable Long suggestionId, @RequestBody Map<String, Object> payload) {
        boolean accepted = (boolean) payload.get("accepted");
        String feedback = (String) payload.getOrDefault("feedback", "");
        
        try {
            AiSuggestion updated = aiBrainService.processFeedback(suggestionId, accepted, feedback);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. System Health Check (Guardian View)
    @GetMapping("/health")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getSystemHealth() {
        // Mock health metrics
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        double usageRatio = (double) (totalMemory - freeMemory) / totalMemory;
        
        return Map.of(
            "status", usageRatio > 0.9 ? "CRITICAL" : (usageRatio > 0.7 ? "WARNING" : "HEALTHY"),
            "memory_usage_percent", String.format("%.2f", usageRatio * 100),
            "cpu_load", "Low (Mock)",
            "active_threats", 0,
            "ai_guardian_message", usageRatio > 0.7 ? "I'm monitoring high memory usage. Check suggestions." : "All systems operational. I'm standing by."
        );
    }
}
