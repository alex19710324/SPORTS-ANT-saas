package com.sportsant.saas.ai.repository;

import com.sportsant.saas.ai.entity.MemberChurnPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberChurnPredictionRepository extends JpaRepository<MemberChurnPrediction, String> {

    // 根据会员ID查询最新预测
    Optional<MemberChurnPrediction> findFirstByMemberIdOrderByPredictedAtDesc(Long memberId);

    // 查询高风险流失会员
    List<MemberChurnPrediction> findByRiskLevel(String riskLevel);

    // 查询需要重新预测的记录
    @Query("SELECT p FROM MemberChurnPrediction p WHERE p.validUntil < :now")
    List<MemberChurnPrediction> findExpiredPredictions(@Param("now") LocalDateTime now);

    // 统计各风险级别数量
    @Query("SELECT p.riskLevel, COUNT(p) FROM MemberChurnPrediction p GROUP BY p.riskLevel")
    List<Object[]> countByRiskLevel();
}
