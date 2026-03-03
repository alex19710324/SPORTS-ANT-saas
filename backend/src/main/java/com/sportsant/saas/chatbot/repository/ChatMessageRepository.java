package com.sportsant.saas.chatbot.repository;

import com.sportsant.saas.chatbot.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    
    // 按对话ID和租户ID查询
    List<ChatMessage> findByConversationIdAndTenantIdOrderByCreatedAtAsc(String conversationId, String tenantId);
    
    // 按发送者类型查询
    List<ChatMessage> findByTenantIdAndSenderType(String tenantId, String senderType);
    
    // 按意图查询
    List<ChatMessage> findByTenantIdAndIntent(String tenantId, String intent);
    
    // 按知识库匹配查询
    List<ChatMessage> findByTenantIdAndKnowledgeMatched(String tenantId, Boolean knowledgeMatched);
    
    // 统计查询
    long countByTenantIdAndCreatedAtBetween(String tenantId, LocalDateTime startTime, LocalDateTime endTime);
    long countByTenantIdAndSenderTypeAndCreatedAtBetween(String tenantId, String senderType, LocalDateTime startTime, LocalDateTime endTime);
    
    // 统计意图分布
    @Query("SELECT m.intent, COUNT(m) FROM ChatMessage m " +
           "WHERE m.tenantId = :tenantId AND m.senderType = 'USER' " +
           "AND m.createdAt BETWEEN :startTime AND :endTime " +
           "GROUP BY m.intent")
    List<Object[]> countByIntentAndTenantIdAndCreatedAtBetween(
        @Param("tenantId") String tenantId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);
    
    // 获取未回复的消息
    @Query("SELECT m FROM ChatMessage m WHERE m.tenantId = :tenantId " +
           "AND m.senderType = 'USER' AND m.hasResponse = false " +
           "ORDER BY m.createdAt DESC")
    List<ChatMessage> findUnansweredMessages(@Param("tenantId") String tenantId);
    
    // 获取知识库使用统计
    @Query("SELECT m.knowledgeId, COUNT(m) FROM ChatMessage m " +
           "WHERE m.tenantId = :tenantId AND m.knowledgeMatched = true " +
           "AND m.createdAt BETWEEN :startTime AND :endTime " +
           "GROUP BY m.knowledgeId")
    List<Object[]> countKnowledgeUsage(
        @Param("tenantId") String tenantId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);
}
