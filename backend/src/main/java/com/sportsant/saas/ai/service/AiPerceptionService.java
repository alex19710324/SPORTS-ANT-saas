package com.sportsant.saas.ai.service;

import com.sportsant.saas.ai.event.SystemEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AiPerceptionService {
    private static final Logger logger = LoggerFactory.getLogger(AiPerceptionService.class);

    @Autowired
    private AiBrainService aiBrainService;

    @Autowired
    private com.sportsant.saas.communication.service.NotificationService notificationService;

    // --- 1. Generic Event Listener (The Brain's Ears) ---
    @EventListener
    public void onSystemEvent(SystemEvent event) {
        logger.info("AI Perception: Received event {} from {}", event.getType(), event.getSource());
        
        // Safety Incident Perception
        if ("SAFETY_INCIDENT".equals(event.getType())) {
            String type = (String) event.getPayload().get("type");
            String location = (String) event.getPayload().get("location");
            
            // 1. Suggestion for Manager
            aiBrainService.proposeSuggestion(
                "Emergency Alert: " + type + " at " + location,
                "A safety incident has been reported. Suggest activating emergency protocols and notifying nearby staff.",
                "CRITICAL",
                "HIGH",
                "/api/safety/emergency/activate"
            );
            
            // 2. Direct Notification to Security
            notificationService.sendToRole("ROLE_SECURITY", 
                "Emergency: " + type, 
                "Location: " + location + ". Immediate response required!", 
                "ERROR", 
                "/workbench/security"
            );
        }

        // Inventory Alert Perception
        if ("LOW_STOCK_ALERT".equals(event.getType())) {
            String sku = (String) event.getPayload().get("sku");
            String name = (String) event.getPayload().get("name");
            Integer current = (Integer) event.getPayload().get("current");
            
            // 1. Suggestion for Manager
            aiBrainService.proposeSuggestion(
                "Low Stock Alert: " + name,
                "Item " + name + " (SKU: " + sku + ") is running low (" + current + " remaining). Suggest reordering immediately.",
                "INVENTORY",
                "HIGH",
                "/api/inventory/reorder/" + sku
            );
            
            // 2. Direct Notification to Store Manager
            notificationService.sendToRole("ROLE_STORE_MANAGER",
                "Low Stock: " + name,
                "Only " + current + " remaining. Please reorder.",
                "WARNING",
                "/inventory"
            );
        }

        // Security Audit Perception
        if ("SECURITY_AUDIT".equals(event.getType()) && "FAILURE".equals(event.getPayload().get("status"))) {
             // Logic already handled in onLoginFailure, but this catches generic failures
        }
        
        // Existing logic...
        if ("LARGE_TRANSACTION".equals(event.getType())) {
             Double amount = (Double) event.getPayload().get("amount");
             aiBrainService.proposeSuggestion(
                "Marketing Opportunity: High Value Customer",
                "A transaction of " + amount + " was detected. Suggest sending a VIP thank you email.",
                "MARKETING",
                "LOW",
                "/api/marketing/campaign/vip-reward"
            );
        }
    }

    // --- 2. Security Perception ---
    private final AtomicInteger loginFailureCount = new AtomicInteger(0);

    @EventListener
    public void onLoginFailure(AuthenticationFailureBadCredentialsEvent event) {
        int failures = loginFailureCount.incrementAndGet();
        logger.warn("AI Perception: Detected login failure. Count: {}", failures);

        if (failures >= 5) {
            // Trigger Decision: Suggest IP Ban or Account Lock
            aiBrainService.proposeSuggestion(
                "High Security Risk: Multiple Login Failures",
                "Detected " + failures + " consecutive login failures. Suggest temporarily locking login attempts or blocking the source IP.",
                "SECURITY",
                "HIGH",
                "/api/admin/security/lockdown"
            );
            // Reset count to avoid spamming suggestions
            loginFailureCount.set(0); 
        }
    }

    // --- 3. System Health Perception (Scheduled) ---
    @Scheduled(fixedRate = 60000) // Every 1 minute
    public void monitorSystemHealth() {
        // Mock: Check Memory Usage
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        double usageRatio = (double) (totalMemory - freeMemory) / totalMemory;

        if (usageRatio > 0.85) {
            logger.warn("AI Perception: High Memory Usage detected: {}%", String.format("%.2f", usageRatio * 100));
            aiBrainService.proposeSuggestion(
                "Performance Alert: High Memory Usage",
                "System memory usage is above 85%. Suggest checking for memory leaks or scaling up the instance.",
                "PERFORMANCE",
                "MEDIUM",
                "/api/admin/system/gc" // Mock endpoint to trigger GC
            );
        }
    }
}
