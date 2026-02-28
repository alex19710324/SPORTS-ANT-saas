package com.sportsant.saas.marketing.controller;

import com.sportsant.saas.marketing.entity.Activity;
import com.sportsant.saas.marketing.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marketing")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MarketingController {

    @Autowired
    private ActivityService activityService;

    // --- P0: Activity Management ---
    @PostMapping("/activities")
    @PreAuthorize("hasRole('MARKETING') or hasRole('ADMIN')")
    public Activity createActivity(@RequestBody Activity activity) {
        return activityService.createActivity(activity);
    }

    @GetMapping("/activities")
    @PreAuthorize("hasRole('MARKETING') or hasRole('ADMIN')")
    public List<Activity> listActivities() {
        return activityService.getAllActivities();
    }

    // --- P1: AI Content Generation ---
    @PostMapping("/activities/{id}/generate-content")
    @PreAuthorize("hasRole('MARKETING') or hasRole('ADMIN')")
    public Map<String, String> generateContent(@PathVariable Long id) {
        return activityService.generateContent(id);
    }
}
