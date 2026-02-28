package com.sportsant.saas.data.controller;

import com.sportsant.saas.data.service.DataService;
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
    private DataService dataService;

    // --- P0: Real-time Analytics ---
    @GetMapping("/rt/{metric}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DATA_ANALYST')")
    public Map<String, Object> getRealTimeMetric(@PathVariable String metric) {
        return dataService.getRealTimeMetrics(metric);
    }

    // --- P1: Tag Service ---
    @PostMapping("/tags/query")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING')")
    @SuppressWarnings("unchecked")
    public Map<String, Object> queryTags(@RequestBody Map<String, Object> payload) {
        List<String> tags = (List<String>) payload.get("tags");
        return dataService.queryTags(tags);
    }
}
