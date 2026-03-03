package com.sportsant.saas.compliance.service;

import com.sportsant.saas.compliance.entity.Consent;
import com.sportsant.saas.compliance.repository.ConsentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConsentService {

    @Autowired
    private ConsentRepository consentRepository;

    @Transactional
    public Consent recordConsent(Long userId, String agreementType, String version, boolean agreed, String ipAddress, String userAgent) {
        // Log the consent event
        Consent consent = new Consent(userId, agreementType, version, agreed, ipAddress, userAgent);
        return consentRepository.save(consent);
    }

    public boolean hasConsented(Long userId, String agreementType) {
        Optional<Consent> latest = consentRepository.findTopByUserIdAndAgreementTypeOrderByTimestampDesc(userId, agreementType);
        return latest.isPresent() && latest.get().isAgreed();
    }
    
    public List<Consent> getHistory(Long userId) {
        return consentRepository.findByUserId(userId);
    }
    
    @Transactional
    public void withdrawConsent(Long userId, String agreementType, String ipAddress, String userAgent) {
        // To withdraw, we record a new entry with agreed=false
        Optional<Consent> latest = consentRepository.findTopByUserIdAndAgreementTypeOrderByTimestampDesc(userId, agreementType);
        String version = latest.map(Consent::getVersion).orElse("unknown");
        
        recordConsent(userId, agreementType, version, false, ipAddress, userAgent);
    }
}
