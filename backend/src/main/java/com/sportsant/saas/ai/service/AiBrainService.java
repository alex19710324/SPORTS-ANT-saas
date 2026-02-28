package com.sportsant.saas.ai.service;

import com.sportsant.saas.ai.entity.AiSuggestion;
import com.sportsant.saas.ai.repository.AiSuggestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AiBrainService {

    @Autowired
    private AiSuggestionRepository suggestionRepository;

    /**
     * Submit a suggestion from the Perception/Decision engine.
     */
    public AiSuggestion proposeSuggestion(String title, String content, String type, String priority, String actionableApi) {
        AiSuggestion suggestion = new AiSuggestion();
        suggestion.setTitle(title);
        suggestion.setContent(content);
        suggestion.setType(type);
        suggestion.setPriority(priority);
        suggestion.setActionableApi(actionableApi);
        suggestion.setStatus("PENDING");
        return suggestionRepository.save(suggestion);
    }

    /**
     * Get pending suggestions for the admin/user dashboard.
     */
    public List<AiSuggestion> getPendingSuggestions() {
        return suggestionRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    /**
     * Process user feedback on a suggestion (Evolution step).
     */
    public AiSuggestion processFeedback(Long suggestionId, boolean accepted, String feedback) {
        Optional<AiSuggestion> opt = suggestionRepository.findById(suggestionId);
        if (opt.isPresent()) {
            AiSuggestion suggestion = opt.get();
            suggestion.setStatus(accepted ? "ACCEPTED" : "REJECTED");
            suggestion.setFeedback(feedback);
            
            // TODO: In a real system, this feedback would be fed back into the AI model for reinforcement learning.
            // e.g. evolutionService.learn(suggestion, accepted);
            
            return suggestionRepository.save(suggestion);
        }
        throw new RuntimeException("Suggestion not found");
    }
}
