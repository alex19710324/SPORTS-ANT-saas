package com.sportsant.saas.globalization.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.globalization.entity.InternationalizationProfile;
import com.sportsant.saas.globalization.repository.InternationalizationProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class GlobalizationDataInitializer implements CommandLineRunner {

    @Autowired
    private InternationalizationProfileRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        // Always update profiles to ensure latest config
        initChinaProfile();
        initUSProfile();
        initItalyProfile();
    }

    private void initChinaProfile() throws Exception {
        InternationalizationProfile p = repository.findByLocale("zh-CN")
                .orElse(new InternationalizationProfile());
        p.setLocale("zh-CN");
        p.setCountryName("China");
        p.setTimezone("Asia/Shanghai");
        
        // Currency
        Map<String, Object> currency = new HashMap<>();
        currency.put("code", "CNY");
        currency.put("symbol", "¥");
        currency.put("format", "¥#");
        p.setCurrencyConfig(objectMapper.writeValueAsString(currency));

        // Measurement
        Map<String, Object> measure = new HashMap<>();
        measure.put("system", "metric");
        measure.put("weight", "kg");
        measure.put("distance", "km");
        measure.put("temperature", "C");
        p.setMeasurementConfig(objectMapper.writeValueAsString(measure));
        
        // Formatting
        Map<String, Object> fmt = new HashMap<>();
        fmt.put("date", "YYYY年MM月DD日");
        fmt.put("time", "HH:mm");
        fmt.put("address", "eastern");
        p.setFormattingConfig(objectMapper.writeValueAsString(fmt));

        // Contact
        Map<String, Object> contact = new HashMap<>();
        contact.put("phoneCode", "+86");
        contact.put("phoneMask", "### #### ####");
        p.setContactConfig(objectMapper.writeValueAsString(contact));

        // Network
        Map<String, Object> net = new HashMap<>();
        net.put("apiBaseUrl", "https://api.cn.sportsant.com");
        net.put("cdnUrl", "https://cdn.cn.sportsant.com");
        net.put("dataResidency", "local");
        p.setNetworkConfig(objectMapper.writeValueAsString(net));
        
        // Services
        Map<String, Object> srv = new HashMap<>();
        srv.put("sms", "aliyun");
        srv.put("email", "aliyun-dm");
        srv.put("payment", "wechat");
        srv.put("map", "amap");
        srv.put("push", "jiguang");
        p.setServicesConfig(objectMapper.writeValueAsString(srv));
        
        // Compliance
        Map<String, Object> comp = new HashMap<>();
        comp.put("laws", new Object[]{
            Map.of("name", "PIPL", "code", "CN-PIPL", "description", "Personal Information Protection Law", "type", "data", "mandatory", true),
            Map.of("name", "DSL", "code", "CN-DSL", "description", "Data Security Law", "type", "data", "mandatory", true),
            Map.of("name", "Labor Law", "code", "CN-LABOR", "description", "Standard Working Hours System", "type", "legal", "mandatory", true)
        });
        comp.put("taxes", Map.of("name", "VAT", "rate", 0.13, "type", "VAT", "filingFrequency", "monthly"));
        comp.put("invoicing", Map.of("required", true, "system", "fapiao"));
        comp.put("dataPrivacy", Map.of("policy", "PIPL", "consentRequired", true, "cookieBanner", false));
        p.setComplianceConfig(objectMapper.writeValueAsString(comp));
        
        // AI Guidance
        Map<String, Object> ai = new HashMap<>();
        ai.put("promptContext", "You are an AI assistant for a business in China. You must strictly adhere to PIPL and DSL. Data cannot leave China. Recommend WeCom for internal comms.");
        ai.put("riskLevel", "high");
        ai.put("suggestions", new String[]{
            "Ensure all member data is stored on domestic servers.",
            "Implement real-name authentication for all users.",
            "Tax integration with Golden Tax System is required for invoicing."
        });
        p.setAiGuidanceConfig(objectMapper.writeValueAsString(ai));

        p.setHolidaysConfig(objectMapper.writeValueAsString(new String[]{
            "Spring Festival (CNY)", "National Day (Golden Week)", "Labor Day", "Mid-Autumn Festival"
        }));
        p.setSocialPlatformsConfig(objectMapper.writeValueAsString(new String[]{
            "WeChat", "Douyin", "Little Red Book (Xiaohongshu)", "Weibo"
        }));
        p.setPaymentMethodsConfig(objectMapper.writeValueAsString(new String[]{
            "WeChat Pay", "Alipay", "UnionPay"
        }));
        p.setKycRequirementsConfig(objectMapper.writeValueAsString(new String[]{
            "Mobile Phone Verification"
        }));

        // Validation
        Map<String, String> validation = new HashMap<>();
        validation.put("phoneRegex", "^1[3-9]\\d{9}$");
        validation.put("postalCodeRegex", "^[1-9]\\d{5}$");
        p.setValidationConfig(objectMapper.writeValueAsString(validation));

        // Support
        Map<String, String> support = new HashMap<>();
        support.put("email", "support@cn.sportsant.com");
        support.put("phone", "400-888-8888");
        support.put("hours", "9:00 - 18:00 (UTC+8)");
        p.setSupportConfig(objectMapper.writeValueAsString(support));

        // Feature Flags
        Map<String, Boolean> flags = new HashMap<>();
        flags.put("faceRecognition", true);
        flags.put("marketingSMS", true);
        flags.put("socialLogin", true);
        flags.put("guestCheckout", false);
        p.setFeatureFlagsConfig(objectMapper.writeValueAsString(flags));

        // Integrations
        Map<String, String[]> integrations = new HashMap<>();
        integrations.put("accounting", new String[]{"Kingdee (金蝶)", "Yonyou (用友)"});
        integrations.put("crm", new String[]{"WeCom (企业微信)", "DingTalk (钉钉)"});
        integrations.put("sso", new String[]{"WeChat", "DingTalk"});
        p.setIntegrationsConfig(objectMapper.writeValueAsString(integrations));

        // Sports Context
        Map<String, Object> sports = new HashMap<>();
        sports.put("popularSports", new String[]{"Badminton", "Table Tennis", "Basketball", "Swimming"});
        sports.put("facilityTerminology", "场地 (Court/Field)");
        sports.put("membershipModel", "prepaid");
        p.setSportsContextConfig(objectMapper.writeValueAsString(sports));

        // Data Governance
        Map<String, Object> gov = new HashMap<>();
        gov.put("retentionPeriodMonths", 60);
        gov.put("crossBorderTransferAllowed", false);
        p.setDataGovernanceConfig(objectMapper.writeValueAsString(gov));

        repository.save(p);
    }

    private void initUSProfile() throws Exception {
        Optional<InternationalizationProfile> existing = repository.findByLocale("en-US");
        InternationalizationProfile p = existing.orElse(new InternationalizationProfile());
        p.setLocale("en-US");
        p.setCountryName("United States");
        p.setTimezone("America/New_York");
        
        p.setCurrencyConfig(objectMapper.writeValueAsString(Map.of("code", "USD", "symbol", "$", "format", "$#")));
        p.setMeasurementConfig(objectMapper.writeValueAsString(Map.of("system", "imperial", "weight", "lb", "distance", "mi", "temperature", "F")));
        p.setFormattingConfig(objectMapper.writeValueAsString(Map.of("date", "MMM D, YYYY", "time", "h:mm A", "address", "western")));
        p.setContactConfig(objectMapper.writeValueAsString(Map.of("phoneCode", "+1", "phoneMask", "(###) ###-####")));
        p.setNetworkConfig(objectMapper.writeValueAsString(Map.of("apiBaseUrl", "https://api.us.sportsant.com", "cdnUrl", "https://cdn.us.sportsant.com", "dataResidency", "global")));
        p.setServicesConfig(objectMapper.writeValueAsString(Map.of("sms", "twilio", "email", "sendgrid", "payment", "stripe", "map", "google", "push", "fcm")));
        
        Map<String, Object> comp = new HashMap<>();
        comp.put("laws", new Object[]{
            Map.of("name", "CCPA", "code", "US-CCPA", "description", "California Consumer Privacy Act", "type", "data", "mandatory", true),
            Map.of("name", "ADA", "code", "US-ADA", "description", "Americans with Disabilities Act", "type", "legal", "mandatory", true)
        });
        comp.put("taxes", Map.of("name", "Sales Tax", "rate", 0.08, "type", "Sales Tax", "filingFrequency", "quarterly"));
        comp.put("invoicing", Map.of("required", false, "system", "standard"));
        comp.put("dataPrivacy", Map.of("policy", "CCPA", "consentRequired", true, "cookieBanner", true));
        p.setComplianceConfig(objectMapper.writeValueAsString(comp));
        
        Map<String, Object> ai = new HashMap<>();
        ai.put("promptContext", "You are an AI assistant for a business in the USA. Focus on ADA compliance and state-specific tax nexus.");
        ai.put("riskLevel", "medium");
        ai.put("suggestions", new String[]{
            "Verify ADA compliance for the booking portal.",
            "Check sales tax nexus for online merchandise sales."
        });
        p.setAiGuidanceConfig(objectMapper.writeValueAsString(ai));
        
        p.setHolidaysConfig(objectMapper.writeValueAsString(new String[]{
            "Christmas", "Thanksgiving", "Independence Day", "Labor Day"
        }));
        p.setSocialPlatformsConfig(objectMapper.writeValueAsString(new String[]{
            "Facebook", "Instagram", "X (Twitter)", "TikTok", "LinkedIn"
        }));
        p.setPaymentMethodsConfig(objectMapper.writeValueAsString(new String[]{
            "Credit Card (Visa/MC/Amex)", "Apple Pay", "PayPal", "Venmo"
        }));
        p.setKycRequirementsConfig(objectMapper.writeValueAsString(new String[]{
            "Email Verification", "Optional Phone Verification"
        }));
        
        // Validation
        Map<String, String> validation = new HashMap<>();
        validation.put("phoneRegex", "^\\(?([0-9]{3})\\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$");
        validation.put("postalCodeRegex", "^\\d{5}(-\\d{4})?$");
        p.setValidationConfig(objectMapper.writeValueAsString(validation));
        
        // Support
        Map<String, String> support = new HashMap<>();
        support.put("email", "support@us.sportsant.com");
        support.put("phone", "1-800-555-0199");
        support.put("hours", "9:00 AM - 5:00 PM (EST)");
        p.setSupportConfig(objectMapper.writeValueAsString(support));
        
        // Feature Flags
        Map<String, Boolean> flags = new HashMap<>();
        flags.put("faceRecognition", false);
        flags.put("marketingSMS", false);
        flags.put("socialLogin", true);
        flags.put("guestCheckout", true);
        p.setFeatureFlagsConfig(objectMapper.writeValueAsString(flags));

        // Integrations
        Map<String, String[]> integrations = new HashMap<>();
        integrations.put("accounting", new String[]{"QuickBooks", "NetSuite"});
        integrations.put("crm", new String[]{"Salesforce", "HubSpot"});
        integrations.put("sso", new String[]{"Okta", "Google Workspace", "Microsoft Entra"});
        p.setIntegrationsConfig(objectMapper.writeValueAsString(integrations));
        
        // Sports Context
        Map<String, Object> sports = new HashMap<>();
        sports.put("popularSports", new String[]{"Basketball", "Pickleball", "Tennis", "Soccer"});
        sports.put("facilityTerminology", "Court/Field");
        sports.put("membershipModel", "subscription");
        p.setSportsContextConfig(objectMapper.writeValueAsString(sports));
        
        // Data Governance
        Map<String, Object> gov = new HashMap<>();
        gov.put("retentionPeriodMonths", 36);
        gov.put("crossBorderTransferAllowed", true);
        p.setDataGovernanceConfig(objectMapper.writeValueAsString(gov));
        
        repository.save(p);
    }

    private void initItalyProfile() throws Exception {
        Optional<InternationalizationProfile> existing = repository.findByLocale("it-IT");
        InternationalizationProfile p = existing.orElse(new InternationalizationProfile());
        p.setLocale("it-IT");
        p.setCountryName("Italy");
        p.setTimezone("Europe/Rome");
        
        p.setCurrencyConfig(objectMapper.writeValueAsString(Map.of("code", "EUR", "symbol", "€", "format", "# €")));
        p.setMeasurementConfig(objectMapper.writeValueAsString(Map.of("system", "metric", "weight", "kg", "distance", "km", "temperature", "C")));
        p.setFormattingConfig(objectMapper.writeValueAsString(Map.of("date", "DD/MM/YYYY", "time", "HH:mm", "address", "western")));
        p.setContactConfig(objectMapper.writeValueAsString(Map.of("phoneCode", "+39", "phoneMask", "### #######")));
        p.setNetworkConfig(objectMapper.writeValueAsString(Map.of("apiBaseUrl", "https://api.eu.sportsant.com", "cdnUrl", "https://cdn.eu.sportsant.com", "dataResidency", "global")));
        p.setServicesConfig(objectMapper.writeValueAsString(Map.of("sms", "twilio", "email", "sendgrid", "payment", "paypal", "map", "google", "push", "fcm")));
        
        Map<String, Object> comp = new HashMap<>();
        comp.put("laws", new Object[]{
            Map.of("name", "GDPR", "code", "EU-GDPR", "description", "General Data Protection Regulation", "type", "data", "mandatory", true),
            Map.of("name", "Fatturazione Elettronica", "code", "IT-SDI", "description", "Mandatory E-invoicing via SDI", "type", "finance", "mandatory", true)
        });
        comp.put("taxes", Map.of("name", "IVA", "rate", 0.22, "type", "VAT", "filingFrequency", "monthly"));
        comp.put("invoicing", Map.of("required", true, "system", "sdi"));
        comp.put("dataPrivacy", Map.of("policy", "GDPR", "consentRequired", true, "cookieBanner", true));
        p.setComplianceConfig(objectMapper.writeValueAsString(comp));
        
        Map<String, Object> ai = new HashMap<>();
        ai.put("promptContext", "You are an AI assistant for a business in Italy. You must enforce strict GDPR compliance and handle SDI e-invoicing.");
        ai.put("riskLevel", "high");
        ai.put("suggestions", new String[]{
            "Connect to SDI for mandatory e-invoicing.",
            "Ensure explicit cookie consent (GDPR)."
        });
        p.setAiGuidanceConfig(objectMapper.writeValueAsString(ai));
        
        p.setHolidaysConfig(objectMapper.writeValueAsString(new String[]{
            "Christmas", "Easter", "Ferragosto (Aug 15)", "Republic Day"
        }));
        p.setSocialPlatformsConfig(objectMapper.writeValueAsString(new String[]{
            "Facebook", "Instagram", "WhatsApp", "LinkedIn"
        }));
        p.setPaymentMethodsConfig(objectMapper.writeValueAsString(new String[]{
            "Credit Card", "PayPal", "Satispay", "Bancomat Pay"
        }));
        p.setKycRequirementsConfig(objectMapper.writeValueAsString(new String[]{
            "Codice Fiscale", "Email Verification"
        }));
        
        // Validation
        Map<String, String> validation = new HashMap<>();
        validation.put("phoneRegex", "^(\\+39)?\\s?3\\d{2}\\s?\\d{6,7}$");
        validation.put("postalCodeRegex", "^\\d{5}$");
        validation.put("idCardRegex", "^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$");
        p.setValidationConfig(objectMapper.writeValueAsString(validation));
        
        // Support
        Map<String, String> support = new HashMap<>();
        support.put("email", "support@eu.sportsant.com");
        support.put("phone", "+39 02 1234 5678");
        support.put("hours", "9:00 - 18:00 (CET)");
        p.setSupportConfig(objectMapper.writeValueAsString(support));
        
        // Feature Flags
        Map<String, Boolean> flags = new HashMap<>();
        flags.put("faceRecognition", false);
        flags.put("marketingSMS", false);
        flags.put("socialLogin", true);
        flags.put("guestCheckout", true);
        p.setFeatureFlagsConfig(objectMapper.writeValueAsString(flags));

        // Integrations
        Map<String, String[]> integrations = new HashMap<>();
        integrations.put("accounting", new String[]{"Fatture in Cloud", "TeamSystem"});
        integrations.put("crm", new String[]{"Salesforce", "HubSpot"});
        integrations.put("sso", new String[]{"Google Workspace", "Microsoft Entra"});
        p.setIntegrationsConfig(objectMapper.writeValueAsString(integrations));
        
        // Sports Context
        Map<String, Object> sports = new HashMap<>();
        sports.put("popularSports", new String[]{"Soccer (Calcio)", "Padel", "Tennis", "Volleyball"});
        sports.put("facilityTerminology", "Campo");
        sports.put("membershipModel", "mixed");
        p.setSportsContextConfig(objectMapper.writeValueAsString(sports));
        
        // Data Governance
        Map<String, Object> gov = new HashMap<>();
        gov.put("retentionPeriodMonths", 24);
        gov.put("crossBorderTransferAllowed", false);
        p.setDataGovernanceConfig(objectMapper.writeValueAsString(gov));
        
        repository.save(p);
    }
}
