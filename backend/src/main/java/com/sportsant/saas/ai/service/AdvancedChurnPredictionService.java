package com.sportsant.saas.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.ai.entity.MemberChurnPrediction;
import com.sportsant.saas.ai.repository.MemberChurnPredictionRepository;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.repository.MemberRepository;
import com.sportsant.saas.booking.repository.BookingRepository;
import com.sportsant.saas.mall.repository.MallOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

@Service
public class AdvancedChurnPredictionService {

    private static final Logger log = LoggerFactory.getLogger(AdvancedChurnPredictionService.class);

    private final MemberChurnPredictionRepository predictionRepository;
    private final MemberRepository memberRepository;
    private final BookingRepository bookingRepository;
    private final MallOrderRepository mallOrderRepository;
    private final ObjectMapper objectMapper;

    public AdvancedChurnPredictionService(
            MemberChurnPredictionRepository predictionRepository,
            MemberRepository memberRepository,
            BookingRepository bookingRepository,
            MallOrderRepository mallOrderRepository,
            ObjectMapper objectMapper) {
        this.predictionRepository = predictionRepository;
        this.memberRepository = memberRepository;
        this.bookingRepository = bookingRepository;
        this.mallOrderRepository = mallOrderRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 为单个会员计算流失预测
     */
    @Transactional
    public MemberChurnPrediction predictMemberChurn(Long memberId) {
        log.info("计算会员流失预测: memberId={}", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("会员不存在"));

        // 收集特征数据
        Map<String, Object> features = collectMemberFeatures(member);

        // 计算流失概率（简化版，实际应使用ML模型）
        double churnProbability = calculateChurnProbability(features);

        // 确定风险级别
        String riskLevel = determineRiskLevel(churnProbability);

        // 生成原因分析
        String reasons = generateChurnReasons(features, churnProbability);

        // 生成推荐行动
        String recommendations = generateRecommendations(riskLevel, features);

        // 创建预测记录
        MemberChurnPrediction prediction = new MemberChurnPrediction();
        prediction.setMemberId(memberId);
        prediction.setChurnProbability(churnProbability);
        prediction.setRiskLevel(riskLevel);

        try {
            prediction.setFeatures(objectMapper.writeValueAsString(features));
        } catch (Exception e) {
            log.error("序列化特征数据失败", e);
        }

        prediction.setReasons(reasons);
        prediction.setRecommendations(recommendations);

        return predictionRepository.save(prediction);
    }

    /**
     * 批量预测（定时任务）
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    @Transactional
    public void batchPredictChurn() {
        log.info("开始批量会员流失预测");

        // 获取所有活跃会员
        List<Member> activeMembers = memberRepository.findByStatus("ACTIVE");

        int predictedCount = 0;
        int highRiskCount = 0;

        for (Member member : activeMembers) {
            try {
                MemberChurnPrediction prediction = predictMemberChurn(member.getId());
                predictedCount++;

                if ("HIGH".equals(prediction.getRiskLevel()) || "CRITICAL".equals(prediction.getRiskLevel())) {
                    highRiskCount++;
                    // 触发预警通知
                    triggerChurnAlert(prediction);
                }
            } catch (Exception e) {
                log.error("预测会员流失失败: memberId={}", member.getId(), e);
            }
        }

        log.info("批量预测完成: 预测{}个会员，其中{}个高风险", predictedCount, highRiskCount);
    }

    /**
     * 收集会员特征数据
     */
    private Map<String, Object> collectMemberFeatures(Member member) {
        Map<String, Object> features = new HashMap<>();

        // 基础特征
        features.put("memberLevel", member.getCurrentLevel() != null ? member.getCurrentLevel().getName() : "NONE");
        features.put("totalSpent", member.getGrowthValue()); // Use growth value as proxy for spent if totalSpent missing
        features.put("balance", member.getBalance());
        features.put("points", member.getPoints());

        // 时间特征
        if (member.getLastVisitDate() != null) {
            long daysSinceLastVisit = ChronoUnit.DAYS.between(member.getLastVisitDate(), LocalDateTime.now());
            features.put("daysSinceLastVisit", daysSinceLastVisit);
        }

        if (member.getJoinDate() != null) {
            long membershipDays = ChronoUnit.DAYS.between(member.getJoinDate(), LocalDateTime.now());
            features.put("membershipDays", membershipDays);
        }

        // 行为特征
        int bookingCount = bookingRepository.findByMemberId(member.getId()).size();
        features.put("totalBookings", (long)bookingCount);

        int orderCount = mallOrderRepository.findByMemberId(String.valueOf(member.getId())).size();
        features.put("totalOrders", (long)orderCount);

        // 计算访问频率（简化）
        long membershipDays = (long) features.getOrDefault("membershipDays", 1L);
        if (membershipDays > 0 && bookingCount > 0) {
            double visitFrequency = (double) bookingCount / membershipDays * 30; // 月均访问次数
            features.put("monthlyVisitFrequency", visitFrequency);
        }

        return features;
    }

    /**
     * 计算流失概率（简化算法）
     */
    private double calculateChurnProbability(Map<String, Object> features) {
        double probability = 0.0;

        // 规则1：超过30天未访问 +30%
        Long daysSinceLastVisit = (Long) features.get("daysSinceLastVisit");
        if (daysSinceLastVisit != null && daysSinceLastVisit > 30) {
            probability += 0.3;
        }

        // 规则2：低消费频率 +20%
        Double monthlyVisitFrequency = (Double) features.get("monthlyVisitFrequency");
        if (monthlyVisitFrequency != null && monthlyVisitFrequency < 1.0) {
            probability += 0.2;
        }

        // 规则3：低等级会员 +15%
        String memberLevel = (String) features.get("memberLevel");
        if ("BRONZE".equals(memberLevel) || "SILVER".equals(memberLevel) || "NONE".equals(memberLevel)) {
            probability += 0.15;
        }

        // 规则4：余额低 +10%
        Double balance = (Double) features.get("balance");
        if (balance != null && balance < 50.0) {
            probability += 0.1;
        }

        // 确保概率在0-1之间
        return Math.min(Math.max(probability, 0.0), 1.0);
    }

    /**
     * 确定风险级别
     */
    private String determineRiskLevel(double probability) {
        if (probability >= 0.7) return "CRITICAL";
        if (probability >= 0.5) return "HIGH";
        if (probability >= 0.3) return "MEDIUM";
        return "LOW";
    }

    /**
     * 生成流失原因
     */
    private String generateChurnReasons(Map<String, Object> features, double probability) {
        StringBuilder reasons = new StringBuilder();

        Long daysSinceLastVisit = (Long) features.get("daysSinceLastVisit");
        if (daysSinceLastVisit != null && daysSinceLastVisit > 30) {
            reasons.append(String.format("• 已%d天未到店访问\n", daysSinceLastVisit));
        }

        Double monthlyVisitFrequency = (Double) features.get("monthlyVisitFrequency");
        if (monthlyVisitFrequency != null && monthlyVisitFrequency < 1.0) {
            reasons.append(String.format("• 月均访问频率低(%.1f次/月)\n", monthlyVisitFrequency));
        }

        Double balance = (Double) features.get("balance");
        if (balance != null && balance < 50.0) {
            reasons.append(String.format("• 账户余额低(¥%.2f)\n", balance));
        }

        if (reasons.length() == 0) {
            reasons.append("• 基于综合行为模式分析\n");
        }

        return reasons.toString();
    }

    /**
     * 生成推荐行动
     */
    private String generateRecommendations(String riskLevel, Map<String, Object> features) {
        StringBuilder recommendations = new StringBuilder();

        switch (riskLevel) {
            case "CRITICAL":
                recommendations.append("🚨 立即采取行动：\n");
                recommendations.append("• 专属客服电话回访\n");
                recommendations.append("• 发送高价值回归礼包(50元优惠券)\n");
                recommendations.append("• 邀请参加会员专属活动\n");
                break;

            case "HIGH":
                recommendations.append("⚠️ 高优先级处理：\n");
                recommendations.append("• 发送个性化回归邀请\n");
                recommendations.append("• 提供30元体验券\n");
                recommendations.append("• 推荐新上项目\n");
                break;

            case "MEDIUM":
                recommendations.append("📢 定期维护：\n");
                recommendations.append("• 发送月度活动资讯\n");
                recommendations.append("• 提供会员积分加倍活动\n");
                recommendations.append("• 生日月特别优惠\n");
                break;

            default:
                recommendations.append("✅ 正常维护：\n");
                recommendations.append("• 保持常规沟通\n");
                recommendations.append("• 推送热门活动\n");
                recommendations.append("• 积分到期提醒\n");
        }

        return recommendations.toString();
    }

    /**
     * 触发流失预警
     */
    private void triggerChurnAlert(MemberChurnPrediction prediction) {
        log.warn("🚨 会员流失高风险预警: memberId={}, riskLevel={}, probability={}",
                prediction.getMemberId(), prediction.getRiskLevel(), prediction.getChurnProbability());

        // TODO: 发送通知到运营人员
        // notificationService.sendChurnAlert(prediction);
    }

    public List<MemberChurnPrediction> predictHighRiskMembers(int limit) {
        // Find top high-risk members
        // For MVP, we can reuse batchPredictChurn logic or query repository if we store predictions
        // Here we'll return a list of recent high-risk predictions
        // Assuming we want to return the predictions we just made or query them.
        // Let's implement a repository query for this.
        return predictionRepository.findByRiskLevel("CRITICAL"); // Simplify for now
    }
}
