package com.sportsant.saas.security.mfa;

import com.sportsant.saas.ai.service.AiAware;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MfaService implements AiAware {

    // User ID -> Secret Key (Mock)
    private final ConcurrentHashMap<Long, String> userSecrets = new ConcurrentHashMap<>();
    
    // User ID -> Pending Code
    private final ConcurrentHashMap<Long, String> pendingCodes = new ConcurrentHashMap<>();

    public String generateSecret(Long userId) {
        // In real app: Generate Google Authenticator compatible secret
        String secret = "SECRET-" + userId + "-" + new SecureRandom().nextInt(999999);
        userSecrets.put(userId, secret);
        return secret;
    }

    public void sendCode(Long userId, String method) { // SMS or Email
        String code = String.format("%06d", new SecureRandom().nextInt(999999));
        pendingCodes.put(userId, code);
        
        // Mock sending
        System.out.println("MFA Code for User " + userId + ": " + code + " (via " + method + ")");
    }

    public boolean verifyCode(Long userId, String code) {
        String validCode = pendingCodes.get(userId);
        if (validCode != null && validCode.equals(code)) {
            pendingCodes.remove(userId);
            return true;
        }
        return false;
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        if ("REQUIRE_MFA".equals(suggestionType)) {
            @SuppressWarnings("unchecked")
            Long userId = (Long) ((Map<String, Object>) payload).get("userId");
            // Flag user session as needing MFA
            System.out.println("AI enforcing MFA for user " + userId);
        }
    }
}
