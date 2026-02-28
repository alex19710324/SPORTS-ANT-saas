package com.sportsant.saas.marketing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.marketing.engine.RuleEngine;
import com.sportsant.saas.marketing.entity.Activity;
import com.sportsant.saas.marketing.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityService implements AiAware {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private RuleEngine ruleEngine;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Activity createActivity(Activity activity) {
        // Validate Rules
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> rules = objectMapper.readValue(activity.getRulesJson(), Map.class);
            if (!ruleEngine.validateActivityRules(activity.getType(), rules)) {
                throw new IllegalArgumentException("Invalid Rules for " + activity.getType());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid JSON format");
        }
        
        return activityRepository.save(activity);
    }

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public Map<String, String> generateContent(Long activityId) {
        // Mock AI Content Generation
        return activityRepository.findById(activityId).map(activity -> {
            Map<String, String> content = new HashMap<>();
            content.put("wechat_title", "🔥 Limited Time Offer: " + activity.getName());
            content.put("wechat_body", "Don't miss out on our " + activity.getType() + " event! " + activity.getRulesJson());
            content.put("poster_url", "https://cdn.sportsant.com/posters/mock_" + activityId + ".jpg");
            return content;
        }).orElse(Map.of());
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        // AI: "Suggest extending Flash Sale by 2 hours due to high traffic"
    }
}
