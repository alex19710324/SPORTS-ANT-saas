package com.sportsant.saas.recommendation.repository;

import com.sportsant.saas.recommendation.entity.ActivityRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRecommendationRepository extends JpaRepository<ActivityRecommendation, String> {
    
    // 按会员ID和租户ID查询
    List<ActivityRecommendation> findByMemberIdAndTenantId(Long memberId, String tenantId);
    
    // 按会员ID、租户ID和时间范围查询
    List<ActivityRecommendation> findByMemberIdAndTenantIdAndRecommendationTimeAfterOrderByRecommendationScoreDesc(
        Long memberId, String tenantId, LocalDateTime recommendationTime);
    
    // 按活动ID查询
    List<ActivityRecommendation> findByActivityId(Long activityId);
    
    // 按算法类型查询
    List<ActivityRecommendation> findByAlgorithmType(String algorithmType);
    
    // 按交付状态查询
    List<ActivityRecommendation> findByDeliveryStatus(String deliveryStatus);
    
    // 按A/B测试分组查询
    List<ActivityRecommendation> findByAbTestGroup(String abTestGroup);
    
    // 获取高评分推荐
    @Query("SELECT r FROM ActivityRecommendation r WHERE r.recommendationScore >= :minScore " +
           "AND r.tenantId = :tenantId ORDER BY r.recommendationScore DESC")
    List<ActivityRecommendation> findHighScoreRecommendations(
        @Param("tenantId") String tenantId,
        @Param("minScore") double minScore);
    
    // 统计推荐效果
    @Query("SELECT COUNT(r) FROM ActivityRecommendation r WHERE r.tenantId = :tenantId " +
           "AND r.deliveryStatus = :status")
    long countByDeliveryStatus(@Param("tenantId") String tenantId, @Param("status") String status);
    
    @Query("SELECT AVG(r.recommendationScore) FROM ActivityRecommendation r " +
           "WHERE r.tenantId = :tenantId AND r.clicked = true")
    Double findAverageScoreForClicked(@Param("tenantId") String tenantId);
    
    @Query("SELECT AVG(r.recommendationScore) FROM ActivityRecommendation r " +
           "WHERE r.tenantId = :tenantId AND r.booked = true")
    Double findAverageScoreForBooked(@Param("tenantId") String tenantId);
    
    // 计算CTR
    @Query("SELECT SUM(r.clickCount), SUM(r.viewCount) FROM ActivityRecommendation r " +
           "WHERE r.tenantId = :tenantId AND r.viewCount > 0")
    Object[] calculateCtrStats(@Param("tenantId") String tenantId);
    
    // 按时间段统计
    @Query("SELECT COUNT(r) FROM ActivityRecommendation r WHERE r.tenantId = :tenantId " +
           "AND r.createdAt BETWEEN :startTime AND :endTime")
    long countByTimeRange(
        @Param("tenantId") String tenantId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);
    
    // 清理过期推荐
    int deleteByExpiresAtBefore(LocalDateTime expiresAt);
}
