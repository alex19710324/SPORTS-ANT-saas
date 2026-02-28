package com.sportsant.saas.safety.repository;

import com.sportsant.saas.safety.entity.IncidentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends JpaRepository<IncidentReport, Long> {
}
