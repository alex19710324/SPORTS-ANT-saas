package com.sportsant.saas.chatbot.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "knowledge_base_items", indexes = {
    @Index(name = "idx_kb_category", columnList = "category, subcategory"),
    @Index(name = "idx_kb_tags", columnList = "tags"),
    @Index(name = "idx_kb_popularity", columnList = "popularityScore DESC"),
    @Index(name = "idx_kb_tenant", columnList = "tenantId")
})
public class KnowledgeBaseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String tenantId;
    
    // 分类信息
    @Column(nullable = false)
    private String category; // MEMBERSHIP, BOOKING, PAYMENT, FACILITY, GENERAL
    
    private String subcategory; // 子分类
    
    // 内容信息
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String question; // 标准问题
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer; // 标准答案
    
    @Column(columnDefinition = "TEXT")
    private String additionalInfo; // 附加信息
    
    // 关键词和标签
    @Column(columnDefinition = "TEXT")
    private String keywordsJson; // 关键词列表
    
    @Column(columnDefinition = "TEXT")
    private String tags; // 标签，逗号分隔
    
    // 向量嵌入（用于语义搜索）
    @Column(columnDefinition = "TEXT")
    private String embeddingJson; // 文本向量
    
    // 使用统计
    private Integer usageCount = 0;
    private Integer helpfulCount = 0;
    private Integer notHelpfulCount = 0;
    
    private Double helpfulRate; // 有帮助比例 0-1
    private Double popularityScore = 0.0; // 流行度分数 0-100
    
    // 意图关联
    @Column(columnDefinition = "TEXT")
    private String relatedIntentsJson; // 相关意图
    
    // 状态管理
    @Column(nullable = false)
    private String status; // DRAFT, ACTIVE, ARCHIVED
    
    private Integer version = 1;
    private String previousVersionId;
    
    // 权限控制
    private Boolean isPublic = true;
    private String accessLevel; // ALL, MEMBER, STAFF, ADMIN
    
    // 时间信息
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    private LocalDateTime lastUsedAt;
    
    // 创建者信息
    private String createdBy;
    private String updatedBy;
    
    // 多语言支持
    private String language = "zh-CN";
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = "ACTIVE";
        if (usageCount == null) usageCount = 0;
        if (helpfulCount == null) helpfulCount = 0;
        if (notHelpfulCount == null) notHelpfulCount = 0;
        if (isPublic == null) isPublic = true;
        if (popularityScore == null) popularityScore = 0.0;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSubcategory() { return subcategory; }
    public void setSubcategory(String subcategory) { this.subcategory = subcategory; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }
    public String getKeywordsJson() { return keywordsJson; }
    public void setKeywordsJson(String keywordsJson) { this.keywordsJson = keywordsJson; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getEmbeddingJson() { return embeddingJson; }
    public void setEmbeddingJson(String embeddingJson) { this.embeddingJson = embeddingJson; }
    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
    public Integer getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(Integer helpfulCount) { this.helpfulCount = helpfulCount; }
    public Integer getNotHelpfulCount() { return notHelpfulCount; }
    public void setNotHelpfulCount(Integer notHelpfulCount) { this.notHelpfulCount = notHelpfulCount; }
    public Double getHelpfulRate() { return helpfulRate; }
    public void setHelpfulRate(Double helpfulRate) { this.helpfulRate = helpfulRate; }
    public Double getPopularityScore() { return popularityScore; }
    public void setPopularityScore(Double popularityScore) { this.popularityScore = popularityScore; }
    public String getRelatedIntentsJson() { return relatedIntentsJson; }
    public void setRelatedIntentsJson(String relatedIntentsJson) { this.relatedIntentsJson = relatedIntentsJson; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public String getPreviousVersionId() { return previousVersionId; }
    public void setPreviousVersionId(String previousVersionId) { this.previousVersionId = previousVersionId; }
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(LocalDateTime lastUsedAt) { this.lastUsedAt = lastUsedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
