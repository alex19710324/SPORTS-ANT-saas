package com.sportsant.saas.marketing.engine;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Mock Rule Engine for Marketing Activities.
 * In a real system, this would be more complex (e.g., Drools).
 */
@Component
public class RuleEngine {

    public boolean validateActivityRules(String type, Map<String, Object> rules) {
        if ("GROUP_BUY".equals(type)) {
            // Must have groupSize > 1
            if (rules.containsKey("groupSize")) {
                int size = Integer.parseInt(rules.get("groupSize").toString());
                return size > 1;
            }
            return false;
        }
        return true; // Default valid
    }

    public String generateDescription(String type, Map<String, Object> rules) {
        if ("GROUP_BUY".equals(type)) {
            return "Join a group of " + rules.get("groupSize") + " to get " + (Double.parseDouble(rules.get("discount").toString()) * 100) + "% off!";
        }
        return "Special Offer";
    }
}
