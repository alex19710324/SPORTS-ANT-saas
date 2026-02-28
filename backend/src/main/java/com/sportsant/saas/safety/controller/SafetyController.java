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
}
