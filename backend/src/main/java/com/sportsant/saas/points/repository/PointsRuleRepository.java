package com.sportsant.saas.points.repository;

import com.sportsant.saas.points.entity.PointsRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointsRuleRepository extends JpaRepository<PointsRule, Long> {
    Optional<PointsRule> findByCode(String code);
}
