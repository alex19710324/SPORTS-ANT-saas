package com.sportsant.saas.inventory.service;

import com.sportsant.saas.inventory.entity.InventoryPrediction;
import com.sportsant.saas.inventory.repository.InventoryPredictionRepository;
import com.sportsant.saas.mall.entity.MallOrder;
import com.sportsant.saas.mall.entity.MallProduct;
import com.sportsant.saas.mall.repository.MallOrderRepository;
import com.sportsant.saas.mall.repository.MallProductRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SmartReplenishmentService {
    
    private final MallProductRepository productRepository;
    private final MallOrderRepository orderRepository;
    private final InventoryPredictionRepository predictionRepository;
    
    public SmartReplenishmentService(MallProductRepository productRepository,
                                   MallOrderRepository orderRepository,
                                   InventoryPredictionRepository predictionRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.predictionRepository = predictionRepository;
    }
    
    /**
     * 线性回归预测算法 - 预测未来7天销量
     */
    @Transactional
    public InventoryPrediction predictProductSales(Long productId, String tenantId) {
        System.out.println("开始预测商品销量: productId=" + productId + ", tenantId=" + tenantId);
        
        // 1. 获取商品信息
        MallProduct product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("商品不存在: " + productId));
        
        // 2. 获取历史销售数据（最近30天）
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        
        List<MallOrder> historicalOrders = orderRepository.findByProductIdAndCreatedAtBetweenAndStatus(
            productId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59), "COMPLETED"
        );
        
        // 3. 按天聚合销量数据
        Map<LocalDate, Integer> dailySales = aggregateDailySales(historicalOrders, startDate, endDate);
        
        // 4. 计算线性回归参数
        RegressionResult regression = calculateLinearRegression(dailySales);
        
        // 5. 预测未来7天销量
        int predictedSales = predictFutureSales(regression, 7);
        
        // 6. 生成补货建议
        ReplenishmentAdvice advice = generateReplenishmentAdvice(
            product.getStock(), 
            predictedSales, 
            regression.getConfidence()
        );
        
        // 7. 保存预测结果
        InventoryPrediction prediction = new InventoryPrediction();
        prediction.setTenantId(tenantId);
        prediction.setProductId(productId);
        prediction.setProductName(product.getName());
        prediction.setPredictionDate(LocalDate.now());
        prediction.setCurrentStock(product.getStock());
        prediction.setDailySalesAvg((int)Math.round(regression.getDailyAverage()));
        prediction.setWeeklySalesAvg((int)Math.round(regression.getDailyAverage() * 7));
        prediction.setSalesTrend(regression.getSlope());
        prediction.setPredictedSales(predictedSales);
        prediction.setConfidenceLevel(regression.getConfidence());
        prediction.setReplenishmentAdvice(advice.getLevel());
        prediction.setSuggestedQuantity(advice.getSuggestedQuantity());
        prediction.setDaysOfSupply(advice.getDaysOfSupply());
        prediction.setEstimatedCost(calculateEstimatedCost(product, advice.getSuggestedQuantity()));
        prediction.setEstimatedRevenue(calculateEstimatedRevenue(product, predictedSales));
        prediction.setAlgorithmUsed("LINEAR_REGRESSION");
        prediction.setAlgorithmParams(String.format(
            "slope=%.4f, intercept=%.4f, rSquared=%.4f, dataPoints=%d",
            regression.getSlope(), regression.getIntercept(), 
            regression.getRSquared(), dailySales.size()
        ));
        
        InventoryPrediction saved = predictionRepository.save(prediction);
        System.out.println("预测完成: product=" + product.getName() + 
                          ", predictedSales=" + predictedSales + 
                          ", advice=" + advice.getLevel());
        
        return saved;
    }
    
    /**
     * 批量预测所有商品
     */
    @Transactional
    public List<InventoryPrediction> batchPredictAllProducts(String tenantId) {
        System.out.println("批量预测所有商品: tenantId=" + tenantId);
        
        List<MallProduct> activeProducts = productRepository.findByEnabledTrue();
        System.out.println("找到活跃商品数量: " + activeProducts.size());
        
        return activeProducts.stream()
            .map(product -> {
                try {
                    return predictProductSales(product.getId(), tenantId);
                } catch (Exception e) {
                    System.err.println("商品预测失败: " + product.getId() + " - " + e.getMessage());
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取紧急补货建议
     */
    public List<InventoryPrediction> getUrgentReplenishments(String tenantId) {
        return predictionRepository.findByTenantIdAndReplenishmentAdviceAndPredictionDate(
            tenantId, "URGENT", LocalDate.now()
        );
    }
    
    /**
     * 获取所有今日预测
     */
    public List<InventoryPrediction> getTodayPredictions(String tenantId) {
        return predictionRepository.findSortedPredictions(tenantId, LocalDate.now());
    }
    
    /**
     * 获取排序后的预测结果
     */
    public List<InventoryPrediction> getSortedPredictions(String tenantId) {
        return predictionRepository.findSortedPredictions(tenantId, LocalDate.now());
    }
    
    /**
     * 按天聚合销量
     */
    private Map<LocalDate, Integer> aggregateDailySales(
        List<MallOrder> orders, LocalDate startDate, LocalDate endDate) {
        
        Map<LocalDate, Integer> dailySales = new TreeMap<>();
        
        // 初始化所有日期为0
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dailySales.put(date, 0);
        }
        
        // 累加订单销量
        orders.forEach(order -> {
            if ("COMPLETED".equals(order.getStatus())) {
                LocalDate orderDate = order.getCreatedAt().toLocalDate();
                dailySales.put(orderDate, dailySales.getOrDefault(orderDate, 0) + 1);
            }
        });
        
        return dailySales;
    }
    
    /**
     * 计算线性回归参数
     */
    private RegressionResult calculateLinearRegression(Map<LocalDate, Integer> dailySales) {
        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();
        
        int dayIndex = 0;
        int totalSales = 0;
        
        for (Map.Entry<LocalDate, Integer> entry : dailySales.entrySet()) {
            xValues.add((double) dayIndex);
            yValues.add(entry.getValue().doubleValue());
            totalSales += entry.getValue();
            dayIndex++;
        }
        
        if (xValues.size() < 7) {
            // 数据不足，返回简单平均值
            double avg = totalSales / (double) Math.max(1, xValues.size());
            return new RegressionResult(0, avg, avg, 0.5, avg);
        }
        
        // 计算线性回归：y = mx + b
        int n = xValues.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        
        for (int i = 0; i < n; i++) {
            sumX += xValues.get(i);
            sumY += yValues.get(i);
            sumXY += xValues.get(i) * yValues.get(i);
            sumX2 += xValues.get(i) * xValues.get(i);
        }
        
        double xMean = sumX / n;
        double yMean = sumY / n;
        
        double numerator = 0;
        double denominator = 0;
        
        for (int i = 0; i < n; i++) {
            numerator += (xValues.get(i) - xMean) * (yValues.get(i) - yMean);
            denominator += Math.pow(xValues.get(i) - xMean, 2);
        }
        
        double slope = denominator != 0 ? numerator / denominator : 0;
        double intercept = yMean - slope * xMean;
        
        // 计算R²（决定系数）
        double ssTotal = 0;
        double ssResidual = 0;
        
        for (int i = 0; i < n; i++) {
            double yPredicted = slope * xValues.get(i) + intercept;
            ssTotal += Math.pow(yValues.get(i) - yMean, 2);
            ssResidual += Math.pow(yValues.get(i) - yPredicted, 2);
        }
        
        double rSquared = 1 - (ssResidual / ssTotal);
        double confidence = Math.max(0.3, Math.min(0.95, rSquared));
        
        return new RegressionResult(slope, intercept, yMean, confidence, yMean);
    }
    
    /**
     * 预测未来销量
     */
    private int predictFutureSales(RegressionResult regression, int days) {
        // 使用回归方程：y = mx + b
        double futureX = regression.getDailyAverage() > 0 ? 30 + days : days;
        double predicted = regression.getSlope() * futureX + regression.getIntercept();
        
        // 确保非负，并考虑置信度
        double adjustedPrediction = Math.max(0, predicted) * regression.getConfidence();
        
        // 四舍五入取整
        return (int) Math.round(adjustedPrediction * days);
    }
    
    /**
     * 生成补货建议
     */
    private ReplenishmentAdvice generateReplenishmentAdvice(
        int currentStock, int predictedSales, double confidence) {
        
        // 计算库存可维持天数
        double dailyNeed = predictedSales / 7.0;
        int daysOfSupply = dailyNeed > 0 ? (int) (currentStock / dailyNeed) : 999;
        
        String adviceLevel;
        int suggestedQuantity;
        
        if (daysOfSupply <= 3) {
            adviceLevel = "URGENT";
            suggestedQuantity = (int) Math.ceil(predictedSales * 1.5);
        } else if (daysOfSupply <= 7) {
            adviceLevel = "NORMAL";
            suggestedQuantity = predictedSales;
        } else if (daysOfSupply <= 14) {
            adviceLevel = "SAFE";
            suggestedQuantity = (int) Math.ceil(predictedSales * 0.5);
        } else {
            adviceLevel = "OVERSTOCK";
            suggestedQuantity = 0;
        }
        
        // 考虑置信度调整
        if (confidence < 0.5) {
            suggestedQuantity = (int) (suggestedQuantity * 0.7);
        } else if (confidence > 0.8) {
            suggestedQuantity = (int) (suggestedQuantity * 1.2);
        }
        
        return new ReplenishmentAdvice(adviceLevel, suggestedQuantity, daysOfSupply);
    }
    
    /**
     * 计算预估成本
     */
    private BigDecimal calculateEstimatedCost(MallProduct product, int quantity) {
        if (product.getCashPrice() != null && quantity > 0) {
            return product.getCashPrice().multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * 计算预估收入
     */
    private BigDecimal calculateEstimatedRevenue(MallProduct product, int predictedSales) {
        if (product.getCashPrice() != null && predictedSales > 0) {
            return product.getCashPrice().multiply(BigDecimal.valueOf(predictedSales));
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * 定时任务：每天凌晨1点自动预测
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void scheduledPrediction() {
        System.out.println("开始执行定时库存预测任务: " + LocalDateTime.now());
        
        // 获取所有租户
        List<String> tenantIds = getActiveTenantIds();
        
        for (String tenantId : tenantIds) {
            try {
                batchPredictAllProducts(tenantId);
                System.out.println("租户 " + tenantId + " 库存预测完成");
            } catch (Exception e) {
                System.err.println("租户 " + tenantId + " 库存预测失败: " + e.getMessage());
            }
        }
    }
    
    private List<String> getActiveTenantIds() {
        // 简化实现，实际应从租户服务获取
        return List.of("default-tenant");
    }
    
    // 内部辅助类
    private static class RegressionResult {
        private final double slope;
        private final double intercept;
        private final double dailyAverage;
        private final double confidence;
        private final double rSquared;
        
        public RegressionResult(double slope, double intercept, double dailyAverage, 
                               double confidence, double rSquared) {
            this.slope = slope;
            this.intercept = intercept;
            this.dailyAverage = dailyAverage;
            this.confidence = confidence;
            this.rSquared = rSquared;
        }
        
        public double getSlope() { return slope; }
        public double getIntercept() { return intercept; }
        public double getDailyAverage() { return dailyAverage; }
        public double getConfidence() { return confidence; }
        public double getRSquared() { return rSquared; }
    }
    
    private static class ReplenishmentAdvice {
        private final String level;
        private final int suggestedQuantity;
        private final int daysOfSupply;
        
        public ReplenishmentAdvice(String level, int suggestedQuantity, int daysOfSupply) {
            this.level = level;
            this.suggestedQuantity = suggestedQuantity;
            this.daysOfSupply = daysOfSupply;
        }
        
        public String getLevel() { return level; }
        public int getSuggestedQuantity() { return suggestedQuantity; }
        public int getDaysOfSupply() { return daysOfSupply; }
    }
}
