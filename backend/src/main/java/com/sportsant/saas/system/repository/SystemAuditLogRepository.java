package com.sportsant.saas.system.repository;

import com.sportsant.saas.system.entity.SystemAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemAuditLogRepository extends JpaRepository<SystemAuditLog, Long> {
    List<SystemAuditLog> findByUserId(String userId);
    List<SystemAuditLog> findByModule(String module);
}
