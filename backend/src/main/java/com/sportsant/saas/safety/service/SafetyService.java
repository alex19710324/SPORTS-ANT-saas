package com.sportsant.saas.safety.service;

import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.safety.entity.IncidentReport;
import com.sportsant.saas.safety.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SafetyService implements AiAware {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public IncidentReport reportIncident(String type, String location, String description, String reporter) {
        IncidentReport report = new IncidentReport();
        report.setType(type);
        report.setLocation(location);
        report.setDescription(description);
        report.setReporter(reporter);
        report.setStatus("REPORTED");
        
        IncidentReport saved = incidentRepository.save(report);

        // Notify AI Brain immediately
        eventPublisher.publishEvent(createEvent("SAFETY_INCIDENT", Map.of(
            "id", saved.getId(),
            "type", type,
            "location", location
        )));

        return saved;
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        // e.g. AI suggests triggering specific emergency protocol
    }
}
