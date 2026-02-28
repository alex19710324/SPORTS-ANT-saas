package com.sportsant.saas.security.audit;

import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.security.blockchain.AuditLedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuditService implements AiAware {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private AuditLedgerService auditLedgerService;

    public void log(String username, String action, String ip, String status, String details) {
        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(action);
        log.setIpAddress(ip);
        log.setStatus(status);
        log.setDetails(details);
        auditLogRepository.save(log);

        // Record to Blockchain (Bank-Grade Security)
        String ledgerData = String.format("%s|%s|%s|%s|%s", username, action, ip, status, details);
        auditLedgerService.recordAudit(ledgerData);

        // Notify AI Brain if it's a sensitive action or failure
        if ("FAILURE".equals(status) || action.startsWith("SENSITIVE_")) {
            eventPublisher.publishEvent(createEvent("SECURITY_AUDIT", Map.of(
                "username", username,
                "action", action,
                "status", status,
                "ip", ip
            )));
        }
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        // e.g. AI suggests locking an account based on audit patterns
    }
}
