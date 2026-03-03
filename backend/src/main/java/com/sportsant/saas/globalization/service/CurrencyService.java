package com.sportsant.saas.globalization.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.globalization.entity.InternationalizationProfile;
import com.sportsant.saas.globalization.repository.InternationalizationProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Optional;

@Service
public class CurrencyService {

    @Autowired
    private InternationalizationProfileRepository profileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public String formatCurrency(BigDecimal amount, String locale) {
        if (amount == null) return "";
        
        Optional<InternationalizationProfile> profileOpt = profileRepository.findByLocale(locale);
        if (profileOpt.isEmpty()) {
            return amount.toString();
        }

        try {
            JsonNode config = objectMapper.readTree(profileOpt.get().getCurrencyConfig());
            String symbol = config.has("symbol") ? config.get("symbol").asText() : "";
            String format = config.has("format") ? config.get("format").asText() : symbol + "#";
            
            // Basic formatting: 2 decimal places
            DecimalFormat df = new DecimalFormat("#,##0.00");
            String formattedNumber = df.format(amount);
            
            // Replace placeholder # with number
            // Note: Simple replacement, robust implementation would use NumberFormat with Locale
            if (format.contains("#")) {
                return format.replace("#", formattedNumber);
            } else {
                return symbol + formattedNumber;
            }
        } catch (Exception e) {
            return amount.toString();
        }
    }
    
    public BigDecimal convert(BigDecimal amount, String fromLocale, String toLocale) {
        // Mock conversion: 1 USD = 7.2 CNY
        // In real system, this would call an external Exchange Rate API
        if (fromLocale.equals(toLocale)) return amount;
        
        // Simple mock logic
        if (fromLocale.equals("en-US") && toLocale.equals("zh-CN")) {
            return amount.multiply(BigDecimal.valueOf(7.2));
        } else if (fromLocale.equals("zh-CN") && toLocale.equals("en-US")) {
            return amount.divide(BigDecimal.valueOf(7.2), 2, BigDecimal.ROUND_HALF_UP);
        }
        
        return amount; 
    }
    
    public BigDecimal convertByCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (amount == null) return BigDecimal.ZERO;
        if (fromCurrency.equalsIgnoreCase(toCurrency)) return amount;
        
        // Mock Rates
        // USD -> CNY : 7.2
        // EUR -> CNY : 7.8
        // GBP -> CNY : 9.0
        
        BigDecimal rate = BigDecimal.ONE;
        
        // Normalize to CNY first (Base Currency)
        BigDecimal amountInCNY = amount;
        if ("USD".equalsIgnoreCase(fromCurrency)) {
            amountInCNY = amount.multiply(BigDecimal.valueOf(7.2));
        } else if ("EUR".equalsIgnoreCase(fromCurrency)) {
            amountInCNY = amount.multiply(BigDecimal.valueOf(7.8));
        } else if ("GBP".equalsIgnoreCase(fromCurrency)) {
            amountInCNY = amount.multiply(BigDecimal.valueOf(9.0));
        }
        
        // Convert from CNY to Target
        if ("CNY".equalsIgnoreCase(toCurrency)) {
            return amountInCNY;
        } else if ("USD".equalsIgnoreCase(toCurrency)) {
            return amountInCNY.divide(BigDecimal.valueOf(7.2), 2, BigDecimal.ROUND_HALF_UP);
        } else if ("EUR".equalsIgnoreCase(toCurrency)) {
            return amountInCNY.divide(BigDecimal.valueOf(7.8), 2, BigDecimal.ROUND_HALF_UP);
        } else if ("GBP".equalsIgnoreCase(toCurrency)) {
            return amountInCNY.divide(BigDecimal.valueOf(9.0), 2, BigDecimal.ROUND_HALF_UP);
        }
        
        return amount; // Fallback: 1:1 if unknown
    }
}
