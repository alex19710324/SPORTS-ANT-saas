package com.sportsant.saas.data.controller;

import com.sportsant.saas.data.service.DataAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DataController {

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @GetMapping("/kpi")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ')")
    public Map<String, Object> getGlobalKPIs() {
        return dataAnalysisService.getGlobalKPIs();
    }

    @GetMapping("/revenue-trend")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ')")
    public Map<String, Object> getRevenueTrend() {
        return dataAnalysisService.getRevenueTrend();
    }

    @GetMapping("/store-leaderboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ')")
    public List<Map<String, Object>> getStoreLeaderboard() {
        return dataAnalysisService.getStoreLeaderboard();
    }
}
