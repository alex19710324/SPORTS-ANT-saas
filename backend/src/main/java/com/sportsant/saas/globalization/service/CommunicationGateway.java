package com.sportsant.saas.globalization.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.globalization.entity.InternationalizationProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommunicationGateway {

    @Autowired
    private GlobalizationService globalizationService;

    @Autowired
    private ObjectMapper objectMapper;

    // Simulate sending an SMS
    public String sendSms(String locale, String phoneNumber, String message) {
        Optional<InternationalizationProfile> profileOpt = globalizationService.getProfileByLocale(locale);
        if (profileOpt.isEmpty()) {
            return "Failed: Profile not found for locale " + locale;
        }

        InternationalizationProfile profile = profileOpt.get();
        try {
            JsonNode services = objectMapper.readTree(profile.getServicesConfig());
            String smsProvider = services.get("sms").asText();

            // Routing logic based on provider
            switch (smsProvider.toLowerCase()) {
                case "aliyun":
                    return sendViaAliyun(phoneNumber, message);
                case "twilio":
                    return sendViaTwilio(phoneNumber, message);
                case "aws-sns":
                    return sendViaAwsSns(phoneNumber, message);
                case "local":
                    return sendViaLocalGateway(phoneNumber, message);
                default:
                    return "Failed: Unknown SMS provider " + smsProvider;
            }

        } catch (Exception e) {
            return "Failed: Configuration error - " + e.getMessage();
        }
    }

    private String sendViaAliyun(String phone, String msg) {
        // Mock implementation
        return "Sent via Aliyun SMS to " + phone + ": " + msg;
    }

    private String sendViaTwilio(String phone, String msg) {
        // Mock implementation
        return "Sent via Twilio to " + phone + ": " + msg;
    }

    private String sendViaAwsSns(String phone, String msg) {
        // Mock implementation
        return "Sent via AWS SNS to " + phone + ": " + msg;
    }
    
    private String sendViaLocalGateway(String phone, String msg) {
        return "Sent via Local Gateway to " + phone + ": " + msg;
    }
}
