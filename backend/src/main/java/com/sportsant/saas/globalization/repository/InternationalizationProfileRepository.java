package com.sportsant.saas.globalization.repository;

import com.sportsant.saas.globalization.entity.InternationalizationProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InternationalizationProfileRepository extends JpaRepository<InternationalizationProfile, Long> {
    Optional<InternationalizationProfile> findByLocale(String locale);
}
