package com.sportsant.saas.audit.service;

import com.sportsant.saas.audit.entity.AuditLog;
import com.sportsant.saas.audit.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Async // Log asynchronously to avoid blocking main thread
    public void log(Long userId, String username, String action, String resourceType, String resourceId, String details, String ipAddress, String status) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction(action);
        log.setResourceType(resourceType);
        log.setResourceId(resourceId);
        log.setDetails(details);
        log.setIpAddress(ipAddress);
        log.setStatus(status);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    public void logSuccess(Long userId, String username, String action, String resourceType, String resourceId, String details, String ipAddress) {
        log(userId, username, action, resourceType, resourceId, details, ipAddress, "SUCCESS");
    }

    public void logFailure(Long userId, String username, String action, String resourceType, String resourceId, String details, String ipAddress) {
        log(userId, username, action, resourceType, resourceId, details, ipAddress, "FAILURE");
    }
}
