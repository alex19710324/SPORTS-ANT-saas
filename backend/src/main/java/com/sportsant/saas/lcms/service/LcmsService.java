package com.sportsant.saas.lcms.service;

import com.sportsant.saas.lcms.entity.LcmsKey;
import com.sportsant.saas.lcms.entity.LcmsProject;
import com.sportsant.saas.lcms.entity.LcmsTranslation;
import com.sportsant.saas.lcms.repository.LcmsKeyRepository;
import com.sportsant.saas.lcms.repository.LcmsProjectRepository;
import com.sportsant.saas.lcms.repository.LcmsTranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LcmsService {

    @Autowired
    private LcmsProjectRepository projectRepository;

    @Autowired
    private LcmsKeyRepository keyRepository;

    @Autowired
    private LcmsTranslationRepository translationRepository;

    public LcmsProject createProject(LcmsProject project) {
        return projectRepository.save(project);
    }

    public List<LcmsProject> getAllProjects() {
        return projectRepository.findAll();
    }

    @Transactional
    public LcmsKey createKey(LcmsKey key) {
        Optional<LcmsKey> existing = keyRepository.findByProjectCodeAndKeyName(key.getProjectCode(), key.getKeyName());
        if (existing.isPresent()) {
            // Already exists, maybe update description or ignore
            return existing.get();
        }
        return keyRepository.save(key);
    }

    public List<LcmsKey> getKeysByProject(String projectCode) {
        return keyRepository.findByProjectCode(projectCode);
    }

    @Transactional
    public LcmsTranslation updateTranslation(Long keyId, String locale, String value) {
        LcmsTranslation translation = translationRepository.findByKeyIdAndLocale(keyId, locale)
                .orElse(new LcmsTranslation());
        
        if (translation.getKeyId() == null) {
            translation.setKeyId(keyId);
            translation.setLocale(locale);
        }
        
        translation.setValue(value);
        translation.setVerified(true); // Manual update implies verification
        return translationRepository.save(translation);
    }

    public Map<String, String> getBundle(String projectCode, String locale) {
        List<LcmsKey> keys = keyRepository.findByProjectCode(projectCode);
        Map<String, String> bundle = new HashMap<>();
        
        for (LcmsKey key : keys) {
            // 1. Try requested locale
            Optional<LcmsTranslation> trans = translationRepository.findByKeyIdAndLocale(key.getId(), locale);
            
            if (trans.isPresent() && trans.get().getValue() != null && !trans.get().getValue().isEmpty()) {
                bundle.put(key.getKeyName(), trans.get().getValue());
            } else {
                // 2. Fallback to en-US
                Optional<LcmsTranslation> fallback = translationRepository.findByKeyIdAndLocale(key.getId(), "en-US");
                bundle.put(key.getKeyName(), fallback.map(LcmsTranslation::getValue).orElse(key.getKeyName()));
            }
        }
        return bundle;
    }
    
    // Mock Auto-Translate
    public String autoTranslate(String text, String targetLocale) {
        // In real world, call DeepL / Google Translate API
        return "[Auto " + targetLocale + "] " + text;
    }
}
