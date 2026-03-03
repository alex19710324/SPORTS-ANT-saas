package com.sportsant.saas.chatbot.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "chat_messages", indexes = {
    @Index(name = "idx_message_conversation", columnList = "conversationId, createdAt"),
    @Index(name = "idx_message_sender", columnList = "senderType, senderId"),
    @Index(name = "idx_message_intent", columnList = "intent")
})
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String conversationId;
    
    @Column(nullable = false)
    private String tenantId;
    
    // 发送者信息
    @Column(nullable = false)
    private String senderType; // USER, BOT, HUMAN_AGENT
    
    private String senderId; // 用户ID或客服ID
    private String senderName;
    
    // 消息内容
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(columnDefinition = "TEXT")
    private String contentType; // TEXT, IMAGE, FILE, LOCATION
    
    // 意图分析
    private String intent; // GREETING, QUESTION, COMPLAINT, BOOKING, PAYMENT
    private Double intentConfidence; // 0-1
    
    @Column(columnDefinition = "TEXT")
    private String entitiesJson; // 提取的实体
    
    // 情感分析
    private String sentiment; // POSITIVE, NEUTRAL, NEGATIVE
    private Double sentimentScore; // -1到1
    
    // 回复信息（针对用户消息）
    private Boolean hasResponse = false;
    private String responseMessageId; // 对应的回复消息ID
    
    // 知识库匹配
    private Boolean knowledgeMatched = false;
    private String knowledgeId;
    private String knowledgeTitle;
    private Double knowledgeMatchScore; // 0-1
    
    // 转人工相关
    private Boolean requiresHuman = false;
    private String escalationReason;
    
    // 时间信息
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    
    // 性能指标
    private Long responseTimeMs; // 响应时间（毫秒）
    
    // 元数据
    @Column(columnDefinition = "TEXT")
    private String metadataJson; // 附加元数据
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (hasResponse == null) hasResponse = false;
        if (knowledgeMatched == null) knowledgeMatched = false;
        if (requiresHuman == null) requiresHuman = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }
    public Double getIntentConfidence() { return intentConfidence; }
    public void setIntentConfidence(Double intentConfidence) { this.intentConfidence = intentConfidence; }
    public String getEntitiesJson() { return entitiesJson; }
    public void setEntitiesJson(String entitiesJson) { this.entitiesJson = entitiesJson; }
    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }
    public Double getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(Double sentimentScore) { this.sentimentScore = sentimentScore; }
    public Boolean getHasResponse() { return hasResponse; }
    public void setHasResponse(Boolean hasResponse) { this.hasResponse = hasResponse; }
    public String getResponseMessageId() { return responseMessageId; }
    public void setResponseMessageId(String responseMessageId) { this.responseMessageId = responseMessageId; }
    public Boolean getKnowledgeMatched() { return knowledgeMatched; }
    public void setKnowledgeMatched(Boolean knowledgeMatched) { this.knowledgeMatched = knowledgeMatched; }
    public String getKnowledgeId() { return knowledgeId; }
    public void setKnowledgeId(String knowledgeId) { this.knowledgeId = knowledgeId; }
    public String getKnowledgeTitle() { return knowledgeTitle; }
    public void setKnowledgeTitle(String knowledgeTitle) { this.knowledgeTitle = knowledgeTitle; }
    public Double getKnowledgeMatchScore() { return knowledgeMatchScore; }
    public void setKnowledgeMatchScore(Double knowledgeMatchScore) { this.knowledgeMatchScore = knowledgeMatchScore; }
    public Boolean getRequiresHuman() { return requiresHuman; }
    public void setRequiresHuman(Boolean requiresHuman) { this.requiresHuman = requiresHuman; }
    public String getEscalationReason() { return escalationReason; }
    public void setEscalationReason(String escalationReason) { this.escalationReason = escalationReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    public Long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(Long responseTimeMs) { this.responseTimeMs = responseTimeMs; }
    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }
}
