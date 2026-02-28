package com.sportsant.saas.teambuilding.controller;

import com.sportsant.saas.security.UserDetailsImpl;
import com.sportsant.saas.teambuilding.service.TeamBuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teambuilding")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TeamBuildingController {

    @Autowired
    private TeamBuildingService teamBuildingService;

    @GetMapping("/activities")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Map<String, Object>> getActivities() {
        return teamBuildingService.getAvailableActivities();
    }

    @PostMapping("/book")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Map<String, Object> bookActivity(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody Map<String, Object> payload) {
        Long activityId = Long.valueOf(payload.get("activityId").toString());
        String date = (String) payload.get("date");
        return teamBuildingService.bookActivity(userDetails.getId(), activityId, date);
    }
}
