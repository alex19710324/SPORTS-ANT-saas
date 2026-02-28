package com.sportsant.saas.ai.repository;

import com.sportsant.saas.ai.entity.AiModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiModelRepository extends JpaRepository<AiModel, Long> {
    Optional<AiModel> findByName(String name);
}
