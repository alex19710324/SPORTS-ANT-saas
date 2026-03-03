package com.sportsant.saas.recommendation.service;

import com.sportsant.saas.recommendation.entity.MemberProfile;
import com.sportsant.saas.recommendation.entity.ActivityRecommendation;
import com.sportsant.saas.recommendation.repository.MemberProfileRepository;
import com.sportsant.saas.recommendation.repository.ActivityRecommendationRepository;
import com.sportsant.saas.recommendation.service.helper.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ActivityRecommendationService {
    
    private final MemberProfileRepository memberProfileRepository;
    private final ActivityRecommendationRepository recommendationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public ActivityRecommendationService(MemberProfileRepository memberProfileRepository, 
                                         ActivityRecommendationRepository recommendationRepository) {
        this.memberProfileRepository = memberProfileRepository;
        this.recommendationRepository = recommendationRepository;
    }
    
    /**
     * 为会员生成个性化活动推荐
     */
    @Transactional
    public List<ActivityRecommendation> generatePersonalizedRecommendations(Long memberId, String tenantId, int limit) {
        System.out.println("为会员生成个性化推荐: memberId=" + memberId + ", tenantId=" + tenantId);
        
        // 获取会员画像
        MemberProfile memberProfile = memberProfileRepository.findByMemberIdAndTenantId(memberId, tenantId)
            .orElseGet(() -> createDefaultMemberProfile(memberId, tenantId));
        
        // 获取候选活动
        List<ActivityCandidate> candidateActivities = getCandidateActivities(tenantId);
        
        // 应用多种推荐算法
        List<RecommendationResult> allResults = new ArrayList<>();
        
        // 1. 协同过滤推荐
        List<RecommendationResult> cfResults = collaborativeFilteringRecommend(memberProfile, candidateActivities);
        allResults.addAll(cfResults);
        
        // 2. 内容推荐
        List<RecommendationResult> cbResults = contentBasedRecommend(memberProfile, candidateActivities);
        allResults.addAll(cbResults);
        
        // 3. 热门推荐
        List<RecommendationResult> popularResults = popularityRecommend(memberProfile, candidateActivities);
        allResults.addAll(popularResults);
        
        // 4. 混合推荐（加权融合）
        List<RecommendationResult> hybridResults = hybridRecommend(allResults);
        
        // 排序并选择Top N
        List<RecommendationResult> topResults = hybridResults.stream()
            .sorted((a, b) -> Double.compare(b.getFinalScore(), a.getFinalScore()))
            .limit(limit)
            .collect(Collectors.toList());
        
        // 转换为推荐实体并保存
        List<ActivityRecommendation> recommendations = new ArrayList<>();
        int ranking = 1;
        
        for (RecommendationResult result : topResults) {
            ActivityRecommendation recommendation = createRecommendationEntity(
                memberProfile, result, ranking
            );
            recommendations.add(recommendationRepository.save(recommendation));
            ranking++;
        }
        
        System.out.println("推荐生成完成: memberId=" + memberId + ", 生成推荐数=" + recommendations.size());
        return recommendations;
    }
    
    /**
     * 协同过滤推荐（基于相似会员）
     */
    private List<RecommendationResult> collaborativeFilteringRecommend(MemberProfile memberProfile, 
                                                                      List<ActivityCandidate> candidates) {
        System.out.println("执行协同过滤推荐: memberId=" + memberProfile.getMemberId());
        
        // 获取相似会员（简化实现）
        List<MemberProfile> similarMembers = findSimilarMembers(memberProfile);
        
        // 计算活动评分
        List<RecommendationResult> results = new ArrayList<>();
        
        for (ActivityCandidate activity : candidates) {
            double score = 0.0;
            int similarMemberCount = 0;
            
            // 计算相似会员对该活动的平均评分
            for (MemberProfile similarMember : similarMembers) {
                Double memberRating = getMemberActivityRating(similarMember.getMemberId(), activity.getId());
                if (memberRating != null) {
                    score += memberRating;
                    similarMemberCount++;
                }
            }
            
            if (similarMemberCount > 0) {
                score = score / similarMemberCount;
                
                RecommendationResult result = new RecommendationResult();
                result.setActivityId(activity.getId());
                result.setActivityName(activity.getName());
                result.setCfScore(score * 100); // 转换为0-100分
                result.setFinalScore(score * 100 * 0.4); // 协同过滤权重40%
                result.setAlgorithmType("COLLABORATIVE_FILTERING");
                result.setReason("相似会员喜欢此活动");
                
                results.add(result);
            }
        }
        
        return results;
    }
    
    /**
     * 内容推荐（基于兴趣匹配）
     */
    private List<RecommendationResult> contentBasedRecommend(MemberProfile memberProfile, 
                                                            List<ActivityCandidate> candidates) {
        System.out.println("执行内容推荐: memberId=" + memberProfile.getMemberId());
        
        // 解析会员兴趣标签
        Set<String> memberInterests = parseInterestTags(memberProfile.getInterestTagsJson());
        
        List<RecommendationResult> results = new ArrayList<>();
        
        for (ActivityCandidate activity : candidates) {
            // 计算兴趣匹配度
            double interestMatch = calculateInterestMatch(memberInterests, activity.getTags());
            
            // 计算时间匹配度
            double timeMatch = calculateTimeMatch(memberProfile, activity);
            
            // 计算价格匹配度
            double priceMatch = calculatePriceMatch(memberProfile, activity);
            
            // 综合评分
            double contentScore = interestMatch * 0.5 + timeMatch * 0.3 + priceMatch * 0.2;
            
            if (contentScore > 0.3) { // 阈值过滤
                RecommendationResult result = new RecommendationResult();
                result.setActivityId(activity.getId());
                result.setActivityName(activity.getName());
                result.setCbScore(contentScore * 100);
                result.setFinalScore(contentScore * 100 * 0.35); // 内容推荐权重35%
                result.setAlgorithmType("CONTENT_BASED");
                result.setReason("与您的兴趣和偏好匹配");
                
                results.add(result);
            }
        }
        
        return results;
    }
    
    /**
     * 热门推荐
     */
    private List<RecommendationResult> popularityRecommend(MemberProfile memberProfile, 
                                                          List<ActivityCandidate> candidates) {
        System.out.println("执行热门推荐: memberId=" + memberProfile.getMemberId());
        
        List<RecommendationResult> results = new ArrayList<>();
        
        for (ActivityCandidate activity : candidates) {
            double popularityScore = activity.getPopularityScore();
            
            // 新用户或低活跃用户给予更高权重
            double memberFactor = 1.0;
            if (memberProfile.getTotalActivitiesAttended() == null || memberProfile.getTotalActivitiesAttended() < 3) {
                memberFactor = 1.5; // 新用户更依赖热门推荐
            }
            
            RecommendationResult result = new RecommendationResult();
            result.setActivityId(activity.getId());
            result.setActivityName(activity.getName());
            result.setPopularityScore(popularityScore * 100);
            result.setFinalScore(popularityScore * 100 * 0.25 * memberFactor); // 热门推荐权重25%
            result.setAlgorithmType("POPULARITY");
            result.setReason("当前热门活动");
            
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * 混合推荐（加权融合）
     */
    private List<RecommendationResult> hybridRecommend(List<RecommendationResult> allResults) {
        // 按活动ID分组，合并不同算法的结果
        Map<Long, RecommendationResult> mergedResults = new HashMap<>();
        
        for (RecommendationResult result : allResults) {
            Long activityId = result.getActivityId();
            
            if (!mergedResults.containsKey(activityId)) {
                mergedResults.put(activityId, result);
            } else {
                RecommendationResult existing = mergedResults.get(activityId);
                // 合并分数（取最高分）
                existing.setFinalScore(Math.max(existing.getFinalScore(), result.getFinalScore()));
                
                // 记录各算法分数
                if (result.getCfScore() > 0) existing.setCfScore(result.getCfScore());
                if (result.getCbScore() > 0) existing.setCbScore(result.getCbScore());
                if (result.getPopularityScore() > 0) existing.setPopularityScore(result.getPopularityScore());
            }
        }
        
        return new ArrayList<>(mergedResults.values());
    }
    
    /**
     * 批量为所有会员生成推荐
     */
    @Transactional
    public Map<String, Object> batchGenerateRecommendations(String tenantId) {
        System.out.println("批量生成推荐: tenantId=" + tenantId);
        
        // 获取所有会员
        List<MemberProfile> allMembers = memberProfileRepository.findByTenantId(tenantId);
        
        int totalGenerated = 0;
        int totalMembers = allMembers.size();
        
        for (MemberProfile member : allMembers) {
            try {
                generatePersonalizedRecommendations(member.getMemberId(), tenantId, 10);
                totalGenerated++;
                
                // 每处理10个会员输出进度
                if (totalGenerated % 10 == 0) {
                    System.out.println("推荐生成进度: " + totalGenerated + "/" + totalMembers);
                }
            } catch (Exception e) {
                System.err.println("会员推荐生成失败: memberId=" + member.getMemberId() + ", error=" + e.getMessage());
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalMembers", totalMembers);
        result.put("successCount", totalGenerated);
        result.put("failedCount", totalMembers - totalGenerated);
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }
    
    /**
     * 获取会员的实时推荐
     */
    public List<ActivityRecommendation> getRealTimeRecommendations(Long memberId, String tenantId, int limit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysAgo = now.minusDays(3);
        
        // 获取最近3天内的推荐
        List<ActivityRecommendation> recentRecommendations = recommendationRepository
            .findByMemberIdAndTenantIdAndRecommendationTimeAfterOrderByRecommendationScoreDesc(
                memberId, tenantId, threeDaysAgo
            );
        
        // 如果推荐不足，实时生成新的
        if (recentRecommendations.size() < limit) {
            List<ActivityRecommendation> newRecommendations = generatePersonalizedRecommendations(
                memberId, tenantId, limit - recentRecommendations.size()
            );
            recentRecommendations.addAll(newRecommendations);
        }
        
        // 排序并返回Top N
        return recentRecommendations.stream()
            .sorted((a, b) -> Double.compare(b.getRecommendationScore(), a.getRecommendationScore()))
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * 记录推荐效果（点击、预订、参加）
     */
    @Transactional
    public ActivityRecommendation trackRecommendationEffect(String recommendationId, String action) {
        System.out.println("记录推荐效果: recommendationId=" + recommendationId + ", action=" + action);
        
        ActivityRecommendation recommendation = recommendationRepository.findById(recommendationId)
            .orElseThrow(() -> new RuntimeException("推荐记录不存在: " + recommendationId));
        
        switch (action) {
            case "VIEW":
                recommendation.setViewCount(recommendation.getViewCount() + 1);
                recommendation.setViewedAt(LocalDateTime.now());
                recommendation.setDeliveryStatus("VIEWED");
                break;
            case "CLICK":
                recommendation.setClicked(true);
                recommendation.setClickCount(recommendation.getClickCount() + 1);
                recommendation.setClickedAt(LocalDateTime.now());
                recommendation.setDeliveryStatus("CLICKED");
                break;
            case "BOOK":
                recommendation.setBooked(true);
                recommendation.setBookedAt(LocalDateTime.now());
                recommendation.setDeliveryStatus("BOOKED");
                break;
            case "ATTEND":
                recommendation.setAttended(true);
                recommendation.setAttendedAt(LocalDateTime.now());
                recommendation.setDeliveryStatus("ATTENDED");
                break;
        }
        
        // 计算CTR和转化率
        if (recommendation.getViewCount() > 0) {
            recommendation.setCtr((double) recommendation.getClickCount() / recommendation.getViewCount());
        }
        
        return recommendationRepository.save(recommendation);
    }
    
    /**
     * 定时任务：每天凌晨1点批量生成推荐
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void scheduledBatchRecommendations() {
        System.out.println("开始执行定时批量推荐: " + LocalDateTime.now());
        
        List<String> tenantIds = getActiveTenantIds();
        
        for (String tenantId : tenantIds) {
            try {
                batchGenerateRecommendations(tenantId);
                System.out.println("租户 " + tenantId + " 批量推荐完成");
            } catch (Exception e) {
                System.err.println("租户 " + tenantId + " 批量推荐失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 定时任务：清理过期推荐
     */
    @Scheduled(cron = "0 0 4 * * ?")
    @Transactional
    public void cleanupExpiredRecommendations() {
        System.out.println("开始清理过期推荐: " + LocalDateTime.now());
        int deletedCount = recommendationRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        System.out.println("清理完成，删除 " + deletedCount + " 条过期推荐记录");
    }
    
    // ========== 私有辅助方法 ==========
    
    /**
     * 创建默认会员画像
     */
    private MemberProfile createDefaultMemberProfile(Long memberId, String tenantId) {
        System.out.println("创建默认会员画像: memberId=" + memberId);
        
        MemberProfile profile = new MemberProfile();
        profile.setTenantId(tenantId);
        profile.setMemberId(memberId);
        profile.setMemberName("会员-" + memberId);
        profile.setRecommendationScore(50.0);
        profile.setRecommendationSegment("NEW_USER");
        profile.setRecommendationStrategy("POPULAR");
        
        try {
            profile.setInterestTagsJson(objectMapper.writeValueAsString(List.of("fitness", "general")));
        } catch (JsonProcessingException e) {
            profile.setInterestTagsJson("[]");
        }
        
        return memberProfileRepository.save(profile);
    }
    
    /**
     * 获取候选活动（简化实现）
     */
    private List<ActivityCandidate> getCandidateActivities(String tenantId) {
        // 这里应该从活动服务获取
        // 暂时返回模拟数据
        List<ActivityCandidate> candidates = new ArrayList<>();
        
        candidates.add(new ActivityCandidate(1L, "晨间瑜伽", "YOGA", 
            Set.of("yoga", "relaxation", "morning"), 85.0));
        candidates.add(new ActivityCandidate(2L, "高强度间歇训练", "FITNESS", 
            Set.of("fitness", "hiit", "cardio"), 92.0));
        candidates.add(new ActivityCandidate(3L, "游泳课程", "SWIMMING", 
            Set.of("swimming", "water", "cardio"), 78.0));
        candidates.add(new ActivityCandidate(4L, "篮球比赛", "COMPETITION", 
            Set.of("basketball", "competition", "team"), 65.0));
        candidates.add(new ActivityCandidate(5L, "普拉提核心训练", "FITNESS", 
            Set.of("pilates", "core", "strength"), 88.0));
        
        return candidates;
    }
    
    /**
     * 查找相似会员（简化实现）
     */
    private List<MemberProfile> findSimilarMembers(MemberProfile memberProfile) {
        // 这里应该实现相似度计算
        // 暂时返回随机会员
        return memberProfileRepository.findTop10ByTenantIdOrderByLastUpdatedDesc(
            memberProfile.getTenantId()
        );
    }
    
    /**
     * 获取会员对活动的评分（简化实现）
     */
    private Double getMemberActivityRating(Long memberId, Long activityId) {
        // 这里应该从评分数据库获取
        // 暂时返回随机评分
        Random random = new Random(memberId.hashCode() + activityId.hashCode());
        return 3.0 + random.nextDouble() * 2.0; // 3-5分
    }
    
    /**
     * 解析兴趣标签
     */
    private Set<String> parseInterestTags(String interestTagsJson) {
        try {
            if (interestTagsJson == null || interestTagsJson.trim().isEmpty()) {
                return new HashSet<>();
            }
            List<String> tags = objectMapper.readValue(interestTagsJson, List.class);
            return new HashSet<>(tags);
        } catch (Exception e) {
            return new HashSet<>();
        }
    }
    
    /**
     * 计算兴趣匹配度
     */
    private double calculateInterestMatch(Set<String> memberInterests, Set<String> activityTags) {
        if (memberInterests.isEmpty() || activityTags.isEmpty()) {
            return 0.0;
        }
        
        // 计算Jaccard相似度
        Set<String> intersection = new HashSet<>(memberInterests);
        intersection.retainAll(activityTags);
        
        Set<String> union = new HashSet<>(memberInterests);
        union.addAll(activityTags);
        
        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }
    
    /**
     * 计算时间匹配度
     */
    private double calculateTimeMatch(MemberProfile memberProfile, ActivityCandidate activity) {
        // 简化实现
        return 0.7; // 默认70%匹配
    }
    
    /**
     * 计算价格匹配度
     */
    private double calculatePriceMatch(MemberProfile memberProfile, ActivityCandidate activity) {
        // 简化实现
        return 0.8; // 默认80%匹配
    }
    
    /**
     * 创建推荐实体
     */
    private ActivityRecommendation createRecommendationEntity(MemberProfile memberProfile, RecommendationResult result, int ranking) {
        ActivityRecommendation recommendation = new ActivityRecommendation();
        recommendation.setTenantId(memberProfile.getTenantId());
        recommendation.setMemberId(memberProfile.getMemberId());
        recommendation.setActivityId(result.getActivityId());
        recommendation.setActivityName(result.getActivityName());
        recommendation.setActivityType(result.getActivityType());
        recommendation.setRecommendationScore(result.getFinalScore());
        recommendation.setRankingPosition(ranking);
        recommendation.setAlgorithmType(result.getAlgorithmType());
        recommendation.setAlgorithmVersion("1.0.0");
        recommendation.setRecommendationReason(result.getReason());
        recommendation.setMatchType("INTEREST_MATCH");
        recommendation.setInterestMatchScore(result.getCbScore() / 100.0);
        recommendation.setDeliveryStatus("GENERATED");
        
        // 设置活动特征（简化）
        recommendation.setActivityCategory("FITNESS");
        recommendation.setActivityDifficulty("INTERMEDIATE");
        recommendation.setActivityDuration(60);
        recommendation.setActivityPrice(99.0);
        recommendation.setActivityLocation("健身中心");
        recommendation.setActivityStartTime(LocalDateTime.now().plusDays(1));
        recommendation.setActivityEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
        recommendation.setAvailableSlots(20);
        
        // 个性化调整
        recommendation.setPersonalizedDiscount(0.1); // 10%折扣
        recommendation.setPersonalizedMessage("根据您的兴趣为您推荐");
        recommendation.setPersonalizedSchedule("建议参加时间: " + 
            memberProfile.getMostActiveHour() != null ? memberProfile.getMostActiveHour() : "18:00-20:00");
        
        return recommendation;
    }
    
    /**
     * 获取活跃租户ID列表
     */
    private List<String> getActiveTenantIds() {
        // 简化实现，实际应从租户服务获取
        return List.of("default-tenant");
    }
}
