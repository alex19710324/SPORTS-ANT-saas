package com.sportsant.saas.sop.repository;

import com.sportsant.saas.sop.entity.EmergencyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyLogRepository extends JpaRepository<EmergencyLog, Long> {
    List<EmergencyLog> findByStatus(String status);
    List<EmergencyLog> findByType(String type);
}
