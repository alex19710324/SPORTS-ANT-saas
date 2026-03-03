package com.sportsant.saas.recommendation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_profiles", indexes = {
    @Index(name = "idx_member_tenant", columnList = "memberId, tenantId"),
    @Index(name = "idx_last_updated", columnList = "lastUpdated DESC"),
    @Index(name = "idx_interest_tags", columnList = "interestTagsJson"),
    @Index(name = "idx_recommendation_score", columnList = "recommendationScore DESC")
})
public class MemberProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String tenantId;
    
    @Column(nullable = false)
    private Long memberId;
    
    @Column(nullable = false)
    private String memberName;
    
    // 基础信息
    private Integer age;
    private String gender;
    private String occupation;
    private String location;
    
    // 兴趣标签（JSON格式）
    @Column(columnDefinition = "TEXT")
    private String interestTagsJson; // ["fitness", "yoga", "swimming", "basketball"]
    
    // 消费偏好
    private String preferredActivityType; // GROUP, PERSONAL, COMPETITION
    private String preferredTimeSlot; // MORNING, AFTERNOON, EVENING, NIGHT
    private String preferredDayOfWeek; // WEEKDAY, WEEKEND
    private Double preferredPriceRangeMin;
    private Double preferredPriceRangeMax;
    
    // 行为特征
    private Integer totalActivitiesAttended;
    private Integer totalActivitiesBooked;
    private Double totalSpentAmount;
    private Double avgMonthlySpent;
    private Integer avgMonthlyVisits;
    
    // 活跃度指标
    private Integer activityLevel; // 1-5 (1:低活跃, 5:高活跃)
    private Integer consumptionLevel; // 1-5 (1:低消费, 5:高消费)
    private Integer loyaltyLevel; // 1-5 (1:新会员, 5:忠诚会员)
    
    // 时间特征
    private String mostActiveHour; // 08-10, 12-14, 18-20, 20-22
    private String mostActiveDay; // MON, TUE, WED, THU, FRI, SAT, SUN
    private Integer daysSinceLastActivity;
    private Integer daysSinceLastVisit;
    
    // 社交特征
    private Boolean hasSocialConnections;
    private Integer friendCount;
    private Integer groupActivityParticipationRate; // 0-100
    
    // 推荐相关
    private Double recommendationScore; // 0-100，推荐系统使用
    private String recommendationSegment; // NEW_USER, ACTIVE_USER, VIP_USER, CHURN_RISK
    private String recommendationStrategy; // POPULAR, PERSONALIZED, DISCOUNT, SOCIAL
    
    // 算法特征
    @Column(columnDefinition = "TEXT")
    private String featureVectorJson; // 特征向量，用于机器学习
    
    // 统计信息
    private LocalDateTime firstActivityDate;
    private LocalDateTime lastActivityDate;
    private LocalDateTime lastProfileUpdate;
    
    @Column(nullable = false)
    private LocalDateTime lastUpdated;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt; // 画像有效期
    
    @PrePersist
    protected void onCreate() {
        lastUpdated = LocalDateTime.now();
        expiresAt = lastUpdated.plusDays(7); // 画像有效期7天
        if (recommendationScore == null) recommendationScore = 50.0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getInterestTagsJson() { return interestTagsJson; }
    public void setInterestTagsJson(String interestTagsJson) { this.interestTagsJson = interestTagsJson; }
    public String getPreferredActivityType() { return preferredActivityType; }
    public void setPreferredActivityType(String preferredActivityType) { this.preferredActivityType = preferredActivityType; }
    public String getPreferredTimeSlot() { return preferredTimeSlot; }
    public void setPreferredTimeSlot(String preferredTimeSlot) { this.preferredTimeSlot = preferredTimeSlot; }
    public String getPreferredDayOfWeek() { return preferredDayOfWeek; }
    public void setPreferredDayOfWeek(String preferredDayOfWeek) { this.preferredDayOfWeek = preferredDayOfWeek; }
    public Double getPreferredPriceRangeMin() { return preferredPriceRangeMin; }
    public void setPreferredPriceRangeMin(Double preferredPriceRangeMin) { this.preferredPriceRangeMin = preferredPriceRangeMin; }
    public Double getPreferredPriceRangeMax() { return preferredPriceRangeMax; }
    public void setPreferredPriceRangeMax(Double preferredPriceRangeMax) { this.preferredPriceRangeMax = preferredPriceRangeMax; }
    public Integer getTotalActivitiesAttended() { return totalActivitiesAttended; }
    public void setTotalActivitiesAttended(Integer totalActivitiesAttended) { this.totalActivitiesAttended = totalActivitiesAttended; }
    public Integer getTotalActivitiesBooked() { return totalActivitiesBooked; }
    public void setTotalActivitiesBooked(Integer totalActivitiesBooked) { this.totalActivitiesBooked = totalActivitiesBooked; }
    public Double getTotalSpentAmount() { return totalSpentAmount; }
    public void setTotalSpentAmount(Double totalSpentAmount) { this.totalSpentAmount = totalSpentAmount; }
    public Double getAvgMonthlySpent() { return avgMonthlySpent; }
    public void setAvgMonthlySpent(Double avgMonthlySpent) { this.avgMonthlySpent = avgMonthlySpent; }
    public Integer getAvgMonthlyVisits() { return avgMonthlyVisits; }
    public void setAvgMonthlyVisits(Integer avgMonthlyVisits) { this.avgMonthlyVisits = avgMonthlyVisits; }
    public Integer getActivityLevel() { return activityLevel; }
    public void setActivityLevel(Integer activityLevel) { this.activityLevel = activityLevel; }
    public Integer getConsumptionLevel() { return consumptionLevel; }
    public void setConsumptionLevel(Integer consumptionLevel) { this.consumptionLevel = consumptionLevel; }
    public Integer getLoyaltyLevel() { return loyaltyLevel; }
    public void setLoyaltyLevel(Integer loyaltyLevel) { this.loyaltyLevel = loyaltyLevel; }
    public String getMostActiveHour() { return mostActiveHour; }
    public void setMostActiveHour(String mostActiveHour) { this.mostActiveHour = mostActiveHour; }
    public String getMostActiveDay() { return mostActiveDay; }
    public void setMostActiveDay(String mostActiveDay) { this.mostActiveDay = mostActiveDay; }
    public Integer getDaysSinceLastActivity() { return daysSinceLastActivity; }
    public void setDaysSinceLastActivity(Integer daysSinceLastActivity) { this.daysSinceLastActivity = daysSinceLastActivity; }
    public Integer getDaysSinceLastVisit() { return daysSinceLastVisit; }
    public void setDaysSinceLastVisit(Integer daysSinceLastVisit) { this.daysSinceLastVisit = daysSinceLastVisit; }
    public Boolean getHasSocialConnections() { return hasSocialConnections; }
    public void setHasSocialConnections(Boolean hasSocialConnections) { this.hasSocialConnections = hasSocialConnections; }
    public Integer getFriendCount() { return friendCount; }
    public void setFriendCount(Integer friendCount) { this.friendCount = friendCount; }
    public Integer getGroupActivityParticipationRate() { return groupActivityParticipationRate; }
    public void setGroupActivityParticipationRate(Integer groupActivityParticipationRate) { this.groupActivityParticipationRate = groupActivityParticipationRate; }
    public Double getRecommendationScore() { return recommendationScore; }
    public void setRecommendationScore(Double recommendationScore) { this.recommendationScore = recommendationScore; }
    public String getRecommendationSegment() { return recommendationSegment; }
    public void setRecommendationSegment(String recommendationSegment) { this.recommendationSegment = recommendationSegment; }
    public String getRecommendationStrategy() { return recommendationStrategy; }
    public void setRecommendationStrategy(String recommendationStrategy) { this.recommendationStrategy = recommendationStrategy; }
    public String getFeatureVectorJson() { return featureVectorJson; }
    public void setFeatureVectorJson(String featureVectorJson) { this.featureVectorJson = featureVectorJson; }
    public LocalDateTime getFirstActivityDate() { return firstActivityDate; }
    public void setFirstActivityDate(LocalDateTime firstActivityDate) { this.firstActivityDate = firstActivityDate; }
    public LocalDateTime getLastActivityDate() { return lastActivityDate; }
    public void setLastActivityDate(LocalDateTime lastActivityDate) { this.lastActivityDate = lastActivityDate; }
    public LocalDateTime getLastProfileUpdate() { return lastProfileUpdate; }
    public void setLastProfileUpdate(LocalDateTime lastProfileUpdate) { this.lastProfileUpdate = lastProfileUpdate; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
