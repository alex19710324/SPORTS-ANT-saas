package com.sportsant.saas.ai.repository;

import com.sportsant.saas.ai.entity.AiFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiFeatureRepository extends JpaRepository<AiFeature, Long> {
    Optional<AiFeature> findByName(String name);
}
