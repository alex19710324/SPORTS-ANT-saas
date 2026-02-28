package com.sportsant.saas.i18n.service;

import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.i18n.entity.I18nKey;
import com.sportsant.saas.i18n.repository.I18nKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TranslationService implements AiAware {

    @Autowired
    private I18nKeyRepository i18nKeyRepository;

    public Map<String, String> getLanguagePack(String lang) {
        List<I18nKey> allKeys = i18nKeyRepository.findAll();
        Map<String, String> pack = new HashMap<>();
        
        for (I18nKey key : allKeys) {
            String value = null;
            if ("en-US".equals(lang)) value = key.getEnUs();
            else if ("ja-JP".equals(lang)) value = key.getJaJp();
            else value = key.getZhCn();
            
            if (value != null) {
                pack.put(key.getKeyName(), value);
            }
        }
        return pack;
    }

    public I18nKey createOrUpdateKey(I18nKey key) {
        return i18nKeyRepository.findByKeyName(key.getKeyName())
            .map(existing -> {
                existing.setZhCn(key.getZhCn());
                existing.setEnUs(key.getEnUs());
                existing.setJaJp(key.getJaJp());
                return i18nKeyRepository.save(existing);
            })
            .orElseGet(() -> i18nKeyRepository.save(key));
    }

    public void autoTranslate(Long keyId) {
        // Mock AI Auto-Translation
        i18nKeyRepository.findById(keyId).ifPresent(key -> {
            if (key.getEnUs() == null) key.setEnUs("Auto-Trans: " + key.getZhCn());
            if (key.getJaJp() == null) key.setJaJp("自動翻訳: " + key.getZhCn());
            i18nKeyRepository.save(key);
        });
    }

    public Map<String, String> detectRegion(String ip) {
        // Mock GeoIP
        if ("127.0.0.1".equals(ip)) return Map.of("country", "CN", "lang", "zh-CN");
        return Map.of("country", "US", "lang", "en-US");
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        // AI: "Suggest translating 'Flash Sale' to 'Limited Time Deal' for US market"
    }
}
