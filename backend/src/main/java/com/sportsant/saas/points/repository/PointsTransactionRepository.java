package com.sportsant.saas.points.repository;

import com.sportsant.saas.points.entity.PointsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointsTransactionRepository extends JpaRepository<PointsTransaction, Long> {
    List<PointsTransaction> findByUserIdOrderByCreatedAtDesc(String userId);
    
    // Check daily limit: SUM(points) where ruleCode = ? and createdAt > today
    // This is simplified, in real app use @Query
    List<PointsTransaction> findByUserIdAndRuleCodeAndCreatedAtAfter(String userId, String ruleCode, LocalDateTime date);
}
