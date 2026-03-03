package com.sportsant.saas.finance.engine;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class TaxEngine {

    // Simple In-Memory Rule Store (Mocking a database or external service)
    private static final Map<String, Double> TAX_RATES = new HashMap<>();

    static {
        // China
        TAX_RATES.put("CN:VAT:STANDARD", 0.13); // General Goods
        TAX_RATES.put("CN:VAT:SERVICE", 0.06);  // Services (e.g. Membership)
        
        // USA (Sales Tax - Simplified Average)
        TAX_RATES.put("US:CA:SALES", 0.0725);
        TAX_RATES.put("US:NY:SALES", 0.088);
        
        // EU (VAT)
        TAX_RATES.put("EU:DE:VAT", 0.19);
        TAX_RATES.put("EU:FR:VAT", 0.20);
    }

    public TaxResult calculate(String region, String category, BigDecimal amount) {
        // Default to Standard VAT if not specified
        String ruleKey = region + ":" + (category != null ? category : "VAT:STANDARD");
        
        // Fallback Logic
        if (!TAX_RATES.containsKey(ruleKey)) {
            if (region.startsWith("CN")) ruleKey = "CN:VAT:STANDARD";
            else if (region.startsWith("US")) ruleKey = "US:NY:SALES"; // Default to NY
            else if (region.startsWith("EU")) ruleKey = "EU:DE:VAT"; // Default to DE
            else ruleKey = "CN:VAT:STANDARD"; // Global Fallback
        }

        double rate = TAX_RATES.getOrDefault(ruleKey, 0.0);
        BigDecimal taxAmount = amount.multiply(BigDecimal.valueOf(rate));

        return new TaxResult(rate, taxAmount, ruleKey);
    }

    public static class TaxResult {
        private final double rate;
        private final BigDecimal amount;
        private final String appliedRule;

        public TaxResult(double rate, BigDecimal amount, String appliedRule) {
            this.rate = rate;
            this.amount = amount;
            this.appliedRule = appliedRule;
        }

        public double getRate() { return rate; }
        public BigDecimal getAmount() { return amount; }
        public String getAppliedRule() { return appliedRule; }
    }
}
