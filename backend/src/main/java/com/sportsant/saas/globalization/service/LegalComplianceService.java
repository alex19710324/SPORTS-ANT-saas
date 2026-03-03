package com.sportsant.saas.globalization.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.globalization.entity.InternationalizationProfile;
import com.sportsant.saas.globalization.repository.InternationalizationProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LegalComplianceService {

    @Autowired
    private InternationalizationProfileRepository profileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public List<LegalDocument> getRequiredDocuments(String locale) {
        List<LegalDocument> documents = new ArrayList<>();
        Optional<InternationalizationProfile> profileOpt = profileRepository.findByLocale(locale);
        
        if (profileOpt.isEmpty()) return documents;

        try {
            JsonNode compliance = objectMapper.readTree(profileOpt.get().getComplianceConfig());
            
            // 1. Data Privacy Policy
            if (compliance.has("dataPrivacy")) {
                JsonNode privacy = compliance.get("dataPrivacy");
                String policyName = privacy.has("policy") ? privacy.get("policy").asText() : "Privacy Policy";
                documents.add(new LegalDocument("privacy_policy", policyName, generatePrivacyContent(policyName, locale)));
            }
            
            // 2. Laws
            if (compliance.has("laws")) {
                for (JsonNode law : compliance.get("laws")) {
                    if (law.has("mandatory") && law.get("mandatory").asBoolean()) {
                        String name = law.get("name").asText();
                        documents.add(new LegalDocument("law_" + law.get("code").asText(), name + " Compliance", "Full text of " + name));
                    }
                }
            }
            
            // 3. Terms of Service (Standard)
            documents.add(new LegalDocument("tos", "Terms of Service", "Standard Terms for " + locale));

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return documents;
    }

    private String generatePrivacyContent(String policyType, String locale) {
        if ("GDPR".equalsIgnoreCase(policyType)) {
            return "We respect your data rights under GDPR...";
        } else if ("CCPA".equalsIgnoreCase(policyType)) {
            return "California residents have the right to...";
        } else if ("PIPL".equalsIgnoreCase(policyType)) {
            return "According to PIPL, your personal information...";
        }
        return "Generic Privacy Policy...";
    }

    public static class LegalDocument {
        public String code;
        public String title;
        public String content; // Or URL

        public LegalDocument(String code, String title, String content) {
            this.code = code;
            this.title = title;
            this.content = content;
        }
    }
}
