package com.sportsant.saas.language.service;

import com.sportsant.saas.language.entity.LanguagePackage;
import com.sportsant.saas.language.repository.LanguagePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LanguageService {

    @Autowired
    private LanguagePackageRepository languagePackageRepository;

    public List<LanguagePackage> getAllLanguages() {
        return languagePackageRepository.findAll();
    }

    public Optional<LanguagePackage> getLanguageByCode(String code) {
        return languagePackageRepository.findByCode(code);
    }

    public LanguagePackage createOrUpdateLanguage(String code, String name, String content, String version) {
        Optional<LanguagePackage> existing = languagePackageRepository.findByCode(code);
        LanguagePackage languagePackage;
        if (existing.isPresent()) {
            languagePackage = existing.get();
            languagePackage.setName(name);
            languagePackage.setContent(content);
            languagePackage.setVersion(version);
        } else {
            languagePackage = new LanguagePackage();
            languagePackage.setCode(code);
            languagePackage.setName(name);
            languagePackage.setContent(content);
            languagePackage.setVersion(version);
        }
        return languagePackageRepository.save(languagePackage);
    }
}
