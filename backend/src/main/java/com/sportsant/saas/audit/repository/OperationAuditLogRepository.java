package com.sportsant.saas.audit.repository;

import com.sportsant.saas.audit.entity.OperationAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationAuditLogRepository extends JpaRepository<OperationAuditLog, Long> {
    List<OperationAuditLog> findTop100ByOrderByCreatedAtDesc();
    List<OperationAuditLog> findByUsername(String username);
}
