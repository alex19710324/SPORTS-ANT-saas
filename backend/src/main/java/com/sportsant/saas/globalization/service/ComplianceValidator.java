package com.sportsant.saas.globalization.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.globalization.entity.InternationalizationProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ComplianceValidator {

    @Autowired
    private GlobalizationService globalizationService;

    @Autowired
    private ObjectMapper objectMapper;

    public ValidationResult validatePhone(String locale, String phoneNumber) {
        return validate(locale, "phoneRegex", phoneNumber);
    }

    public ValidationResult validatePostalCode(String locale, String postalCode) {
        return validate(locale, "postalCodeRegex", postalCode);
    }

    public ValidationResult validateIdCard(String locale, String idCard) {
        return validate(locale, "idCardRegex", idCard);
    }

    private ValidationResult validate(String locale, String regexKey, String value) {
        Optional<InternationalizationProfile> profileOpt = globalizationService.getProfileByLocale(locale);
        if (profileOpt.isEmpty()) {
            return new ValidationResult(false, "Profile not found for locale: " + locale);
        }

        InternationalizationProfile profile = profileOpt.get();
        try {
            JsonNode validation = objectMapper.readTree(profile.getValidationConfig());
            if (!validation.has(regexKey)) {
                return new ValidationResult(true, "No validation rule for " + regexKey + " in " + locale);
            }

            String regex = validation.get(regexKey).asText();
            boolean isValid = Pattern.matches(regex, value);

            return new ValidationResult(isValid, isValid ? "Valid" : "Invalid format for " + locale);

        } catch (Exception e) {
            return new ValidationResult(false, "Configuration error: " + e.getMessage());
        }
    }

    public static class ValidationResult {
        public boolean valid;
        public String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
    }
}
