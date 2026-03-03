package com.sportsant.saas.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.globalization.entity.InternationalizationProfile;
import com.sportsant.saas.globalization.service.GlobalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private GlobalizationService globalizationService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Map<String, PaymentGateway> providers;

    @Autowired
    public PaymentService(List<PaymentGateway> gatewayList) {
        this.providers = gatewayList.stream()
                .collect(Collectors.toMap(PaymentGateway::getProviderName, gateway -> gateway));
    }

    public PaymentResult initiatePayment(String locale, String orderNo, BigDecimal amount, String description) {
        Optional<InternationalizationProfile> profileOpt = globalizationService.getProfileByLocale(locale);
        String currency = "USD"; // Default
        String providerKey = "stripe"; // Default

        if (profileOpt.isPresent()) {
            InternationalizationProfile profile = profileOpt.get();
            try {
                // 1. Get Currency
                JsonNode currencyConfig = objectMapper.readTree(profile.getCurrencyConfig());
                currency = currencyConfig.get("code").asText();

                // 2. Get Payment Provider
                JsonNode servicesConfig = objectMapper.readTree(profile.getServicesConfig());
                providerKey = servicesConfig.get("payment").asText();

            } catch (Exception e) {
                System.err.println("Error parsing profile config: " + e.getMessage());
            }
        }

        // 3. Select Provider
        PaymentGateway gateway = providers.get(providerKey);
        if (gateway == null) {
            // Fallback
            gateway = providers.get("stripe");
            if (gateway == null) throw new RuntimeException("No payment provider available");
        }

        return gateway.initiatePayment(orderNo, amount, currency, description);
    }
}
