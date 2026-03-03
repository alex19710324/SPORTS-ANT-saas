package com.sportsant.saas.audit.service;

import com.sportsant.saas.audit.entity.OperationAuditLog;
import com.sportsant.saas.audit.repository.OperationAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationAuditService {

    @Autowired
    private OperationAuditLogRepository auditLogRepository;

    @Async
    public void saveLog(OperationAuditLog log) {
        auditLogRepository.save(log);
    }

    public List<OperationAuditLog> getRecentLogs() {
        return auditLogRepository.findTop100ByOrderByCreatedAtDesc();
    }
}
