package com.sportsant.saas.safety.controller;

import com.sportsant.saas.safety.service.SafetyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/safety")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SafetyController {

    @Autowired
    private SafetyService safetyService;

    @GetMapping("/tasks")
    @PreAuthorize("hasRole('SECURITY') or hasRole('ADMIN')")
    public Map<String, Object> getSafetyOverview() {
        return safetyService.getSafetyOverview();
    }

    @PostMapping("/incidents")
    @PreAuthorize("hasRole('SECURITY') or hasRole('ADMIN')")
    public void reportIncident(@RequestBody Map<String, String> payload) {
        String type = payload.get("type");
        String location = payload.get("location");
        String description = payload.get("description");
        // For now, hardcode reporter or get from context
        safetyService.reportIncident(type, location, description, "Security Officer");
    }

    @PostMapping("/incidents/{id}/resolve")
    @PreAuthorize("hasRole('SECURITY') or hasRole('ADMIN')")
    public void resolveIncident(@PathVariable Long id) {
        safetyService.resolveIncident(id);
    }
}
