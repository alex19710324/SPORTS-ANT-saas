package com.sportsant.saas.language.repository;

import com.sportsant.saas.language.entity.LanguagePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguagePackageRepository extends JpaRepository<LanguagePackage, Long> {
    Optional<LanguagePackage> findByCode(String code);
}
