package com.sportsant.saas.data.controller;

import com.sportsant.saas.data.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ') or hasRole('STORE_MANAGER')")
    public Map<String, Object> getDashboardData() {
        return analyticsService.getDashboardData();
    }
}
