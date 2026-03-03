package com.sportsant.saas.sop.controller;

import com.sportsant.saas.sop.entity.EmergencyLog;
import com.sportsant.saas.sop.entity.EmergencyPlan;
import com.sportsant.saas.sop.service.EmergencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emergency")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EmergencyController {

    @Autowired
    private EmergencyService emergencyService;

    @GetMapping("/plans")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public List<EmergencyPlan> getAllPlans() {
        return emergencyService.getAllPlans();
    }

    @PostMapping("/report")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public EmergencyLog reportIncident(@RequestBody Map<String, String> payload) {
        String type = payload.get("type");
        String description = payload.get("description");
        String reporter = SecurityContextHolder.getContext().getAuthentication().getName();
        return emergencyService.reportIncident(type, description, reporter);
    }

    @PostMapping("/resolve/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public EmergencyLog resolveIncident(@PathVariable Long id) {
        return emergencyService.resolveIncident(id);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public List<EmergencyLog> getActiveIncidents() {
        return emergencyService.getActiveIncidents();
    }
}
