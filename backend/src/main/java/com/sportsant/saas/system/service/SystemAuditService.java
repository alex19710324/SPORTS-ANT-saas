package com.sportsant.saas.system.service;

import com.sportsant.saas.system.entity.SystemAuditLog;
import com.sportsant.saas.system.repository.SystemAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemAuditService {

    @Autowired
    private SystemAuditLogRepository auditLogRepository;

    public void logAction(String userId, String username, String action, String module, String details, String ip) {
        SystemAuditLog log = new SystemAuditLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction(action);
        log.setModule(module);
        log.setDetails(details);
        log.setIpAddress(ip);
        auditLogRepository.save(log);
    }

    public List<SystemAuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
}
