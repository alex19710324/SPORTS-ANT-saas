package com.sportsant.saas.compliance.repository;

import com.sportsant.saas.compliance.entity.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {
    List<Consent> findByUserId(Long userId);
    
    // Find latest consent for a specific type
    Optional<Consent> findTopByUserIdAndAgreementTypeOrderByTimestampDesc(Long userId, String agreementType);
}
