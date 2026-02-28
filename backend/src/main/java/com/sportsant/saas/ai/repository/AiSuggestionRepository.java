package com.sportsant.saas.ai.repository;

import com.sportsant.saas.ai.entity.AiSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiSuggestionRepository extends JpaRepository<AiSuggestion, Long> {
    List<AiSuggestion> findByStatusOrderByCreatedAtDesc(String status);
    List<AiSuggestion> findByPriority(String priority);
}
