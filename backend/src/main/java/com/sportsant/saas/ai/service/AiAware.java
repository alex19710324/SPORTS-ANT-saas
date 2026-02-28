package com.sportsant.saas.ai.service;

import com.sportsant.saas.ai.event.SystemEvent;
import org.springframework.stereotype.Component;

@Component
public interface AiAware {
    /**
     * Called when the AI Brain decides to intervene or suggest an action
     * specific to this component.
     */
    void onAiSuggestion(String suggestionType, Object payload);
    
    /**
     * Method to publish events to the AI Brain.
     * Implementing services should inject ApplicationEventPublisher and use this.
     */
    default SystemEvent createEvent(String type, java.util.Map<String, Object> payload) {
        return new SystemEvent(this.getClass().getSimpleName(), type, payload);
    }
}
