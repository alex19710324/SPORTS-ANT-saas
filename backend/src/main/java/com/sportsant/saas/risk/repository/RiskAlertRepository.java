package com.sportsant.saas.risk.repository;

import com.sportsant.saas.risk.entity.RiskAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskAlertRepository extends JpaRepository<RiskAlert, Long> {
    List<RiskAlert> findByStatus(String status);
    List<RiskAlert> findByLevel(String level);
}
