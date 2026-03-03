package com.sportsant.saas.recommendation.repository;

import com.sportsant.saas.recommendation.entity.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {
    
    // 按会员ID和租户ID查询
    Optional<MemberProfile> findByMemberIdAndTenantId(Long memberId, String tenantId);
    
    // 按租户ID查询
    List<MemberProfile> findByTenantId(String tenantId);
    
    // 按租户ID和最后更新时间查询
    List<MemberProfile> findByTenantIdAndLastUpdatedAfter(String tenantId, LocalDateTime lastUpdated);
    
    // 按推荐分段查询
    List<MemberProfile> findByTenantIdAndRecommendationSegment(String tenantId, String segment);
    
    // 按兴趣标签查询（JSON包含）
    @Query("SELECT p FROM MemberProfile p WHERE p.tenantId = :tenantId " +
           "AND p.interestTagsJson LIKE %:tag%")
    List<MemberProfile> findByInterestTag(
        @Param("tenantId") String tenantId,
        @Param("tag") String tag);
    
    // 按活跃度查询
    List<MemberProfile> findByTenantIdAndActivityLevelGreaterThanEqual(
        String tenantId, Integer minActivityLevel);
    
    // 按消费水平查询
    List<MemberProfile> findByTenantIdAndConsumptionLevelGreaterThanEqual(
        String tenantId, Integer minConsumptionLevel);
    
    // 获取Top N会员（按推荐分数）
    List<MemberProfile> findTop10ByTenantIdOrderByRecommendationScoreDesc(String tenantId);
    
    // 获取最近更新的会员
    List<MemberProfile> findTop10ByTenantIdOrderByLastUpdatedDesc(String tenantId);
    
    // 统计会员分段分布
    @Query("SELECT p.recommendationSegment, COUNT(p) FROM MemberProfile p " +
           "WHERE p.tenantId = :tenantId GROUP BY p.recommendationSegment")
    List<Object[]> countBySegment(@Param("tenantId") String tenantId);
    
    // 清理过期画像
    int deleteByExpiresAtBefore(LocalDateTime expiresAt);
}
