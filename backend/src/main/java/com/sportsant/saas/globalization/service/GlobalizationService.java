package com.sportsant.saas.globalization.service;

import com.sportsant.saas.globalization.entity.InternationalizationProfile;
import com.sportsant.saas.globalization.repository.InternationalizationProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GlobalizationService {

    @Autowired
    private InternationalizationProfileRepository profileRepository;

    public Optional<InternationalizationProfile> getProfileByLocale(String locale) {
        return profileRepository.findByLocale(locale);
    }
    
    public InternationalizationProfile saveProfile(InternationalizationProfile profile) {
        return profileRepository.save(profile);
    }
}
