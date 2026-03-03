package com.sportsant.saas.recommendation.service.helper;

public class RecommendationResult {
    private Long activityId;
    private String activityName;
    private String activityType;
    
    // 各算法分数
    private Double cfScore = 0.0;      // 协同过滤分数
    private Double cbScore = 0.0;      // 内容推荐分数
    private Double popularityScore = 0.0; // 热门分数
    
    private Double finalScore = 0.0;   // 最终分数
    private String algorithmType;      // 主要算法类型
    private String reason;             // 推荐理由

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    public Double getCfScore() { return cfScore; }
    public void setCfScore(Double cfScore) { this.cfScore = cfScore; }
    public Double getCbScore() { return cbScore; }
    public void setCbScore(Double cbScore) { this.cbScore = cbScore; }
    public Double getPopularityScore() { return popularityScore; }
    public void setPopularityScore(Double popularityScore) { this.popularityScore = popularityScore; }
    public Double getFinalScore() { return finalScore; }
    public void setFinalScore(Double finalScore) { this.finalScore = finalScore; }
    public String getAlgorithmType() { return algorithmType; }
    public void setAlgorithmType(String algorithmType) { this.algorithmType = algorithmType; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
