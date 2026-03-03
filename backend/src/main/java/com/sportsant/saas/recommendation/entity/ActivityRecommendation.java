package com.sportsant.saas.recommendation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "activity_recommendations", indexes = {
    @Index(name = "idx_member_recommendation", columnList = "memberId, recommendationTime DESC"),
    @Index(name = "idx_activity_recommendation", columnList = "activityId, recommendationScore DESC"),
    @Index(name = "idx_algorithm_type", columnList = "algorithmType"),
    @Index(name = "idx_delivery_status", columnList = "deliveryStatus")
})
public class ActivityRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String tenantId;
    
    @Column(nullable = false)
    private Long memberId;
    
    @Column(nullable = false)
    private Long activityId;
    
    @Column(nullable = false)
    private String activityName;
    
    @Column(nullable = false)
    private String activityType; // FITNESS, YOGA, SWIMMING, COMPETITION
    
    // 推荐信息
    @Column(nullable = false)
    private LocalDateTime recommendationTime;
    
    @Column(nullable = false)
    private Double recommendationScore; // 0-100，推荐分数
    
    @Column(nullable = false)
    private Integer rankingPosition; // 推荐排名（1-N）
    
    @Column(nullable = false)
    private String algorithmType; // COLLABORATIVE_FILTERING, CONTENT_BASED, HYBRID, POPULARITY
    
    private String algorithmVersion;
    private transient Map<String, Object> algorithmParameters;
    
    // 推荐理由
    @Column(columnDefinition = "TEXT")
    private String recommendationReason; // 推荐理由文本
    
    private String matchType; // INTEREST_MATCH, BEHAVIOR_MATCH, SOCIAL_MATCH, DISCOUNT_MATCH
    private Double interestMatchScore; // 兴趣匹配度 0-1
    private Double behaviorMatchScore; // 行为匹配度 0-1
    private Double socialMatchScore;   // 社交匹配度 0-1
    private Double priceMatchScore;    // 价格匹配度 0-1
    
    // 活动特征
    private String activityCategory;
    private String activityDifficulty; // BEGINNER, INTERMEDIATE, ADVANCED
    private Integer activityDuration; // 分钟
    private Double activityPrice;
    private String activityLocation;
    private LocalDateTime activityStartTime;
    private LocalDateTime activityEndTime;
    private Integer availableSlots;
    
    // 个性化调整
    private Double personalizedDiscount; // 个性化折扣 0-1
    private String personalizedMessage;
    private String personalizedSchedule; // 建议参加时间
    
    // 交付状态
    @Column(nullable = false)
    private String deliveryStatus; // GENERATED, DELIVERED, VIEWED, CLICKED, BOOKED, ATTENDED
    
    private LocalDateTime deliveredAt;
    private LocalDateTime viewedAt;
    private LocalDateTime clickedAt;
    private LocalDateTime bookedAt;
    private LocalDateTime attendedAt;
    
    // 效果追踪
    private Boolean clicked = false;
    private Boolean booked = false;
    private Boolean attended = false;
    private Integer clickCount = 0;
    private Integer viewCount = 0;
    
    private Double ctr; // 点击率
    private Double conversionRate; // 转化率
    private Double revenueGenerated;
    
    // A/B测试
    private String abTestGroup; // CONTROL, VARIANT_A, VARIANT_B
    private String abTestId;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt; // 推荐有效期
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        recommendationTime = LocalDateTime.now();
        deliveryStatus = "GENERATED";
        expiresAt = createdAt.plusDays(3); // 推荐有效期3天
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    public LocalDateTime getRecommendationTime() { return recommendationTime; }
    public void setRecommendationTime(LocalDateTime recommendationTime) { this.recommendationTime = recommendationTime; }
    public Double getRecommendationScore() { return recommendationScore; }
    public void setRecommendationScore(Double recommendationScore) { this.recommendationScore = recommendationScore; }
    public Integer getRankingPosition() { return rankingPosition; }
    public void setRankingPosition(Integer rankingPosition) { this.rankingPosition = rankingPosition; }
    public String getAlgorithmType() { return algorithmType; }
    public void setAlgorithmType(String algorithmType) { this.algorithmType = algorithmType; }
    public String getAlgorithmVersion() { return algorithmVersion; }
    public void setAlgorithmVersion(String algorithmVersion) { this.algorithmVersion = algorithmVersion; }
    public Map<String, Object> getAlgorithmParameters() { return algorithmParameters; }
    public void setAlgorithmParameters(Map<String, Object> algorithmParameters) { this.algorithmParameters = algorithmParameters; }
    public String getRecommendationReason() { return recommendationReason; }
    public void setRecommendationReason(String recommendationReason) { this.recommendationReason = recommendationReason; }
    public String getMatchType() { return matchType; }
    public void setMatchType(String matchType) { this.matchType = matchType; }
    public Double getInterestMatchScore() { return interestMatchScore; }
    public void setInterestMatchScore(Double interestMatchScore) { this.interestMatchScore = interestMatchScore; }
    public Double getBehaviorMatchScore() { return behaviorMatchScore; }
    public void setBehaviorMatchScore(Double behaviorMatchScore) { this.behaviorMatchScore = behaviorMatchScore; }
    public Double getSocialMatchScore() { return socialMatchScore; }
    public void setSocialMatchScore(Double socialMatchScore) { this.socialMatchScore = socialMatchScore; }
    public Double getPriceMatchScore() { return priceMatchScore; }
    public void setPriceMatchScore(Double priceMatchScore) { this.priceMatchScore = priceMatchScore; }
    public String getActivityCategory() { return activityCategory; }
    public void setActivityCategory(String activityCategory) { this.activityCategory = activityCategory; }
    public String getActivityDifficulty() { return activityDifficulty; }
    public void setActivityDifficulty(String activityDifficulty) { this.activityDifficulty = activityDifficulty; }
    public Integer getActivityDuration() { return activityDuration; }
    public void setActivityDuration(Integer activityDuration) { this.activityDuration = activityDuration; }
    public Double getActivityPrice() { return activityPrice; }
    public void setActivityPrice(Double activityPrice) { this.activityPrice = activityPrice; }
    public String getActivityLocation() { return activityLocation; }
    public void setActivityLocation(String activityLocation) { this.activityLocation = activityLocation; }
    public LocalDateTime getActivityStartTime() { return activityStartTime; }
    public void setActivityStartTime(LocalDateTime activityStartTime) { this.activityStartTime = activityStartTime; }
    public LocalDateTime getActivityEndTime() { return activityEndTime; }
    public void setActivityEndTime(LocalDateTime activityEndTime) { this.activityEndTime = activityEndTime; }
    public Integer getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(Integer availableSlots) { this.availableSlots = availableSlots; }
    public Double getPersonalizedDiscount() { return personalizedDiscount; }
    public void setPersonalizedDiscount(Double personalizedDiscount) { this.personalizedDiscount = personalizedDiscount; }
    public String getPersonalizedMessage() { return personalizedMessage; }
    public void setPersonalizedMessage(String personalizedMessage) { this.personalizedMessage = personalizedMessage; }
    public String getPersonalizedSchedule() { return personalizedSchedule; }
    public void setPersonalizedSchedule(String personalizedSchedule) { this.personalizedSchedule = personalizedSchedule; }
    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
    public LocalDateTime getViewedAt() { return viewedAt; }
    public void setViewedAt(LocalDateTime viewedAt) { this.viewedAt = viewedAt; }
    public LocalDateTime getClickedAt() { return clickedAt; }
    public void setClickedAt(LocalDateTime clickedAt) { this.clickedAt = clickedAt; }
    public LocalDateTime getBookedAt() { return bookedAt; }
    public void setBookedAt(LocalDateTime bookedAt) { this.bookedAt = bookedAt; }
    public LocalDateTime getAttendedAt() { return attendedAt; }
    public void setAttendedAt(LocalDateTime attendedAt) { this.attendedAt = attendedAt; }
    public Boolean getClicked() { return clicked; }
    public void setClicked(Boolean clicked) { this.clicked = clicked; }
    public Boolean getBooked() { return booked; }
    public void setBooked(Boolean booked) { this.booked = booked; }
    public Boolean getAttended() { return attended; }
    public void setAttended(Boolean attended) { this.attended = attended; }
    public Integer getClickCount() { return clickCount; }
    public void setClickCount(Integer clickCount) { this.clickCount = clickCount; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public Double getCtr() { return ctr; }
    public void setCtr(Double ctr) { this.ctr = ctr; }
    public Double getConversionRate() { return conversionRate; }
    public void setConversionRate(Double conversionRate) { this.conversionRate = conversionRate; }
    public Double getRevenueGenerated() { return revenueGenerated; }
    public void setRevenueGenerated(Double revenueGenerated) { this.revenueGenerated = revenueGenerated; }
    public String getAbTestGroup() { return abTestGroup; }
    public void setAbTestGroup(String abTestGroup) { this.abTestGroup = abTestGroup; }
    public String getAbTestId() { return abTestId; }
    public void setAbTestId(String abTestId) { this.abTestId = abTestId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
