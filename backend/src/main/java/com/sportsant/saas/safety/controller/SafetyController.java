package com.sportsant.saas.safety.controller;

import com.sportsant.saas.safety.entity.Incident;
import com.sportsant.saas.safety.service.SafetyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/safety")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SafetyController {

    @Autowired
    private SafetyService safetyService;

    @GetMapping("/incidents/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SECURITY') or hasRole('STORE_MANAGER')")
    public List<Incident> getActiveIncidents() {
        return safetyService.getActiveIncidents();
    }

    @PostMapping("/report")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SECURITY') or hasRole('STORE_MANAGER') or hasRole('TECHNICIAN')")
    public Incident reportIncident(@RequestBody Incident incident) {
        // Assume incident has title, description, type, severity, location
        // reportedBy could be extracted from SecurityContext, but passing in body for MVP simplicity
        return safetyService.reportIncident(incident);
    }

    @PutMapping("/incidents/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SECURITY') or hasRole('STORE_MANAGER')")
    public Incident resolveIncident(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String notes = payload.get("notes");
        return safetyService.updateStatus(id, "RESOLVED", notes);
    }
}
