package com.sportsant.saas.sop.repository;

import com.sportsant.saas.sop.entity.EmergencyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmergencyPlanRepository extends JpaRepository<EmergencyPlan, Long> {
    Optional<EmergencyPlan> findByTitle(String title);
}
