package com.sportsant.saas.i18n.repository;

import com.sportsant.saas.i18n.entity.I18nKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface I18nKeyRepository extends JpaRepository<I18nKey, Long> {
    Optional<I18nKey> findByKeyName(String keyName);
}
