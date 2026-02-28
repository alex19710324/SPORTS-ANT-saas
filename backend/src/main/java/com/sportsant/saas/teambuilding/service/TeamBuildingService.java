package com.sportsant.saas.teambuilding.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TeamBuildingService {

    public List<Map<String, Object>> getAvailableActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        activities.add(Map.of(
            "id", 1,
            "title", "Laser Tag Battle",
            "description", "Adrenaline pumping laser tag for teams.",
            "duration", "2 hours",
            "price", 2000,
            "image", "https://coresg-normal.trae.ai/api/ide/v1/text_to_image?prompt=laser%20tag%20arena%20neon%20lights&image_size=landscape_4_3"
        ));
        
        activities.add(Map.of(
            "id", 2,
            "title", "VR Escape Room",
            "description", "Solve puzzles in a virtual world.",
            "duration", "1 hour",
            "price", 1500,
            "image", "https://coresg-normal.trae.ai/api/ide/v1/text_to_image?prompt=vr%20escape%20room%20sci-fi&image_size=landscape_4_3"
        ));
        
        return activities;
    }

    public Map<String, Object> bookActivity(Long userId, Long activityId, String date) {
        // Mock Booking Logic
        return Map.of(
            "bookingId", "TB-" + System.currentTimeMillis(),
            "status", "CONFIRMED",
            "activityId", activityId,
            "date", date
        );
    }
}
