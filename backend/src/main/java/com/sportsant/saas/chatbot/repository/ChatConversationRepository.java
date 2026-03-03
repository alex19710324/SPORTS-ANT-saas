package com.sportsant.saas.chatbot.repository;

import com.sportsant.saas.chatbot.entity.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, String> {
    
    // 按会话ID和租户ID查询
    Optional<ChatConversation> findBySessionIdAndTenantId(String sessionId, String tenantId);
    
    // 按租户ID和状态查询
    List<ChatConversation> findByTenantIdAndStatus(String tenantId, String status);
    
    // 按会员ID查询
    List<ChatConversation> findByMemberIdAndTenantId(Long memberId, String tenantId);
    
    // 按分类查询
    List<ChatConversation> findByTenantIdAndCategory(String tenantId, String category);
    
    // 按优先级查询
    List<ChatConversation> findByTenantIdAndPriority(String tenantId, String priority);
    
    // 按是否转人工查询
    List<ChatConversation> findByTenantIdAndEscalated(String tenantId, Boolean escalated);
    
    // 按是否解决查询
    List<ChatConversation> findByTenantIdAndResolved(String tenantId, Boolean resolved);
    
    // 按ID和租户ID查询
    Optional<ChatConversation> findByIdAndTenantId(String id, String tenantId);
    
    // 统计查询
    long countByTenantIdAndCreatedAtBetween(String tenantId, LocalDateTime startTime, LocalDateTime endTime);
    long countByTenantIdAndResolvedAndCreatedAtBetween(String tenantId, Boolean resolved, LocalDateTime startTime, LocalDateTime endTime);
    long countByTenantIdAndEscalatedAndCreatedAtBetween(String tenantId, Boolean escalated, LocalDateTime startTime, LocalDateTime endTime);
    
    // 获取需要人工处理的对话
    @Query("SELECT c FROM ChatConversation c WHERE c.tenantId = :tenantId " +
           "AND (c.escalated = true OR c.priority = 'URGENT') " +
           "AND c.status = 'ACTIVE' ORDER BY c.createdAt DESC")
    List<ChatConversation> findHumanAttentionRequired(@Param("tenantId") String tenantId);
    
    // 获取长时间未响应的对话
    @Query("SELECT c FROM ChatConversation c WHERE c.tenantId = :tenantId " +
           "AND c.status = 'ACTIVE' AND c.lastMessageAt < :threshold " +
           "ORDER BY c.lastMessageAt ASC")
    List<ChatConversation> findStaleConversations(
        @Param("tenantId") String tenantId,
        @Param("threshold") LocalDateTime threshold);
    
    // 统计意图分布
    @Query("SELECT c.primaryIntent, COUNT(c) FROM ChatConversation c " +
           "WHERE c.tenantId = :tenantId AND c.createdAt BETWEEN :startTime AND :endTime " +
           "GROUP BY c.primaryIntent")
    List<Object[]> countByIntentAndTimeRange(
        @Param("tenantId") String tenantId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);
}
