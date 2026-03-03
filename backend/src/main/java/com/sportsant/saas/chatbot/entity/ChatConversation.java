package com.sportsant.saas.chatbot.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "chat_conversations", indexes = {
    @Index(name = "idx_conversation_session", columnList = "sessionId"),
    @Index(name = "idx_conversation_member", columnList = "memberId, tenantId"),
    @Index(name = "idx_conversation_status", columnList = "status"),
    @Index(name = "idx_conversation_created", columnList = "createdAt DESC")
})
public class ChatConversation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String sessionId; // 会话ID
    
    @Column(nullable = false)
    private String tenantId;
    
    private Long memberId;
    private String memberName;
    
    // 对话信息
    @Column(nullable = false)
    private String channel; // WEB, MOBILE, WECHAT, WHATSAPP
    
    @Column(nullable = false)
    private String status; // ACTIVE, RESOLVED, ESCALATED, CLOSED
    
    @Column(nullable = false)
    private String category; // MEMBERSHIP, BOOKING, PAYMENT, TECHNICAL, GENERAL
    
    @Column(nullable = false)
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    
    // 意图识别
    private String primaryIntent; // 主要意图
    private String secondaryIntent; // 次要意图
    private Double intentConfidence; // 意图置信度 0-1
    
    @Column(columnDefinition = "TEXT")
    private String intentEntitiesJson; // 实体识别结果
    
    // 情感分析
    private String sentiment; // POSITIVE, NEUTRAL, NEGATIVE
    private Double sentimentScore; // 情感分数 -1到1
    
    // 对话统计
    private Integer messageCount = 0;
    private Integer botMessageCount = 0;
    private Integer humanMessageCount = 0;
    
    private Integer escalationCount = 0; // 转人工次数
    private Boolean escalated = false; // 是否已转人工
    
    // 解决信息
    private Boolean resolved = false;
    private String resolutionType; // BOT_RESOLVED, HUMAN_RESOLVED, SELF_RESOLVED
    private String resolutionSummary;
    
    // 满意度
    private Integer satisfactionScore; // 1-5
    private String feedback;
    
    // 时间信息
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime firstMessageAt;
    private LocalDateTime lastMessageAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    
    // 性能指标
    private Long firstResponseTimeMs; // 首次响应时间（毫秒）
    private Long avgResponseTimeMs; // 平均响应时间
    private Long totalDurationMs; // 总对话时长
    
    // 上下文信息
    @Column(columnDefinition = "TEXT")
    private String contextJson; // 对话上下文
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "ACTIVE";
        if (priority == null) priority = "MEDIUM";
        if (messageCount == null) messageCount = 0;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getPrimaryIntent() { return primaryIntent; }
    public void setPrimaryIntent(String primaryIntent) { this.primaryIntent = primaryIntent; }
    public String getSecondaryIntent() { return secondaryIntent; }
    public void setSecondaryIntent(String secondaryIntent) { this.secondaryIntent = secondaryIntent; }
    public Double getIntentConfidence() { return intentConfidence; }
    public void setIntentConfidence(Double intentConfidence) { this.intentConfidence = intentConfidence; }
    public String getIntentEntitiesJson() { return intentEntitiesJson; }
    public void setIntentEntitiesJson(String intentEntitiesJson) { this.intentEntitiesJson = intentEntitiesJson; }
    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }
    public Double getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(Double sentimentScore) { this.sentimentScore = sentimentScore; }
    public Integer getMessageCount() { return messageCount; }
    public void setMessageCount(Integer messageCount) { this.messageCount = messageCount; }
    public Integer getBotMessageCount() { return botMessageCount; }
    public void setBotMessageCount(Integer botMessageCount) { this.botMessageCount = botMessageCount; }
    public Integer getHumanMessageCount() { return humanMessageCount; }
    public void setHumanMessageCount(Integer humanMessageCount) { this.humanMessageCount = humanMessageCount; }
    public Integer getEscalationCount() { return escalationCount; }
    public void setEscalationCount(Integer escalationCount) { this.escalationCount = escalationCount; }
    public Boolean getEscalated() { return escalated; }
    public void setEscalated(Boolean escalated) { this.escalated = escalated; }
    public Boolean getResolved() { return resolved; }
    public void setResolved(Boolean resolved) { this.resolved = resolved; }
    public String getResolutionType() { return resolutionType; }
    public void setResolutionType(String resolutionType) { this.resolutionType = resolutionType; }
    public String getResolutionSummary() { return resolutionSummary; }
    public void setResolutionSummary(String resolutionSummary) { this.resolutionSummary = resolutionSummary; }
    public Integer getSatisfactionScore() { return satisfactionScore; }
    public void setSatisfactionScore(Integer satisfactionScore) { this.satisfactionScore = satisfactionScore; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getFirstMessageAt() { return firstMessageAt; }
    public void setFirstMessageAt(LocalDateTime firstMessageAt) { this.firstMessageAt = firstMessageAt; }
    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
    public Long getFirstResponseTimeMs() { return firstResponseTimeMs; }
    public void setFirstResponseTimeMs(Long firstResponseTimeMs) { this.firstResponseTimeMs = firstResponseTimeMs; }
    public Long getAvgResponseTimeMs() { return avgResponseTimeMs; }
    public void setAvgResponseTimeMs(Long avgResponseTimeMs) { this.avgResponseTimeMs = avgResponseTimeMs; }
    public Long getTotalDurationMs() { return totalDurationMs; }
    public void setTotalDurationMs(Long totalDurationMs) { this.totalDurationMs = totalDurationMs; }
    public String getContextJson() { return contextJson; }
    public void setContextJson(String contextJson) { this.contextJson = contextJson; }
}
