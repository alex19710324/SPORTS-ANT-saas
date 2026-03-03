package com.sportsant.saas.lcms.repository;

import com.sportsant.saas.lcms.entity.LcmsKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LcmsKeyRepository extends JpaRepository<LcmsKey, Long> {
    List<LcmsKey> findByProjectCode(String projectCode);
    Optional<LcmsKey> findByProjectCodeAndKeyName(String projectCode, String keyName);
}
