package com.sportsant.saas.security.defense;

import com.sportsant.saas.ai.service.AiAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimiterService implements AiAware {
    private static final Logger logger = LoggerFactory.getLogger(RateLimiterService.class);

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    // IP -> Request Count (In real prod, use Redis)
    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    
    // IP -> Is Blocked
    private final ConcurrentHashMap<String, Boolean> blockedIps = new ConcurrentHashMap<>();

    public boolean isAllowed(String ipAddress) {
        if (blockedIps.getOrDefault(ipAddress, false)) {
            return false;
        }

        AtomicInteger count = requestCounts.computeIfAbsent(ipAddress, k -> new AtomicInteger(0));
        int current = count.incrementAndGet();

        // Threshold: 100 requests per minute (Mock reset logic omitted for brevity)
        if (current > 100) {
            blockIp(ipAddress, "Rate Limit Exceeded");
            return false;
        }
        return true;
    }

    public void blockIp(String ipAddress, String reason) {
        blockedIps.put(ipAddress, true);
        logger.warn("Blocking IP: {} Reason: {}", ipAddress, reason);
        
        // Notify AI
        eventPublisher.publishEvent(createEvent("SECURITY_BLOCK", Map.of(
            "ip", ipAddress,
            "reason", reason
        )));
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        if ("BLOCK_IP".equals(suggestionType)) {
            @SuppressWarnings("unchecked")
            String ip = (String) ((Map<String, Object>) payload).get("ip");
            blockIp(ip, "AI Suggestion");
        }
    }
}
