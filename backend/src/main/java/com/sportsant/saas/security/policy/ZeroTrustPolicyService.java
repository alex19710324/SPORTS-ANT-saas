package com.sportsant.saas.security.policy;

import com.sportsant.saas.ai.service.AiAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ZeroTrustPolicyService implements AiAware {
    private static final Logger logger = LoggerFactory.getLogger(ZeroTrustPolicyService.class);

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final AtomicBoolean globalLockdown = new AtomicBoolean(false);

    /**
     * In a Zero Trust model, every request's context is re-evaluated.
     * @param userId The user ID
     * @param ip The source IP
     * @param resource The target resource
     * @return true if access is granted
     */
    public boolean evaluateAccess(Long userId, String ip, String resource) {
        if (globalLockdown.get()) {
            logger.warn("Zero Trust: Access Denied due to Global Lockdown. User: {}", userId);
            return false;
        }

        // 1. Check IP Reputation (Mock)
        if (ip.startsWith("192.168.1.200")) { // Mock bad IP
             logger.warn("Zero Trust: Access Denied due to Bad IP Reputation. IP: {}", ip);
             return false;
        }

        // 2. Check User Behavior Anomalies (Mock - would call AI)
        // ...

        return true;
    }

    /**
     * The "Panic Button" - Locks down the entire system except for admins.
     */
    public void activateGlobalLockdown(String reason) {
        globalLockdown.set(true);
        logger.error("!!! GLOBAL LOCKDOWN ACTIVATED !!! Reason: {}", reason);
        
        eventPublisher.publishEvent(createEvent("GLOBAL_LOCKDOWN", Map.of(
            "reason", reason,
            "timestamp", System.currentTimeMillis()
        )));
    }

    public void deactivateGlobalLockdown() {
        globalLockdown.set(false);
        logger.info("Global Lockdown Deactivated.");
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        if ("ACTIVATE_LOCKDOWN".equals(suggestionType)) {
            @SuppressWarnings("unchecked")
            String reason = (String) ((Map<String, Object>) payload).get("reason");
            activateGlobalLockdown("AI Triggered: " + reason);
        }
    }
}
