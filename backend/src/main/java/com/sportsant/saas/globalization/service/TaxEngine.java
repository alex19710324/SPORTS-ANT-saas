package com.sportsant.saas.globalization.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.globalization.entity.InternationalizationProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service("globalizationTaxEngine")
public class TaxEngine {

    @Autowired
    private GlobalizationService globalizationService;

    @Autowired
    private ObjectMapper objectMapper;

    public TaxResult calculateTax(String locale, BigDecimal amount) {
        Optional<InternationalizationProfile> profileOpt = globalizationService.getProfileByLocale(locale);
        if (profileOpt.isEmpty()) {
            throw new IllegalArgumentException("Profile not found for locale: " + locale);
        }

        InternationalizationProfile profile = profileOpt.get();
        try {
            JsonNode compliance = objectMapper.readTree(profile.getComplianceConfig());
            JsonNode taxes = compliance.get("taxes");

            String taxName = taxes.get("name").asText();
            String taxType = taxes.get("type").asText();
            double rate = taxes.get("rate").asDouble();
            
            BigDecimal taxRate = BigDecimal.valueOf(rate);
            BigDecimal taxAmount = amount.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalAmount = amount.add(taxAmount);

            return new TaxResult(
                locale,
                profile.getCountryName(),
                taxName,
                taxType,
                taxRate,
                amount,
                taxAmount,
                totalAmount
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse compliance config", e);
        }
    }

    public static class TaxResult {
        public String locale;
        public String country;
        public String taxName;
        public String taxType;
        public BigDecimal taxRate;
        public BigDecimal netAmount;
        public BigDecimal taxAmount;
        public BigDecimal totalAmount;

        public TaxResult(String locale, String country, String taxName, String taxType, BigDecimal taxRate, BigDecimal netAmount, BigDecimal taxAmount, BigDecimal totalAmount) {
            this.locale = locale;
            this.country = country;
            this.taxName = taxName;
            this.taxType = taxType;
            this.taxRate = taxRate;
            this.netAmount = netAmount;
            this.taxAmount = taxAmount;
            this.totalAmount = totalAmount;
        }
    }
}
