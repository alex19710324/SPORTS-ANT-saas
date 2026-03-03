package com.sportsant.saas.risk.controller;

import com.sportsant.saas.risk.entity.RiskAlert;
import com.sportsant.saas.risk.service.RiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risk")
public class RiskController {

    @Autowired
    private RiskService riskService;

    @GetMapping("/alerts")
    public List<RiskAlert> getAlerts() {
        return riskService.getAllAlerts();
    }

    @PostMapping("/alerts/{id}/resolve")
    public RiskAlert resolveAlert(@PathVariable Long id, @RequestParam String resolution) {
        return riskService.resolveAlert(id, resolution, "ADMIN"); // Mock Resolver
    }
}
