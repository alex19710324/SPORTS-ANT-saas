package com.sportsant.saas.lcms.repository;

import com.sportsant.saas.lcms.entity.LcmsTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LcmsTranslationRepository extends JpaRepository<LcmsTranslation, Long> {
    Optional<LcmsTranslation> findByKeyIdAndLocale(Long keyId, String locale);
    List<LcmsTranslation> findByKeyId(Long keyId);
}
