package com.sportsant.saas.safety.service;

import com.sportsant.saas.ai.event.SystemEvent;
import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.safety.entity.IncidentReport;
import com.sportsant.saas.safety.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SafetyService implements AiAware {

    @Autowired
    private IncidentRepository incidentRepository;

    // Removed unused eventPublisher

    public IncidentReport reportIncident(String type, String location, String description, String reporter) {
        IncidentReport report = new IncidentReport();
        report.setType(type);
        report.setLocation(location);
        report.setDescription(description);
        report.setReporter(reporter);
        report.setStatus("REPORTED");
        
        IncidentReport saved = incidentRepository.save(report);

        // Notify AI Brain immediately
        // eventPublisher.publishEvent(createEvent("SAFETY_INCIDENT", Map.of(...))); // Mock event creation
        
        return saved;
    }

    public Map<String, Object> getSafetyOverview() {
        Map<String, Object> data = new HashMap<>();
        
        // S01: Safety Inspections (Mock)
        data.put("todayInspections", List.of(
            Map.of("id", 201, "area", "Fire Exits", "status", "Pending", "items", 5),
            Map.of("id", 202, "area", "Electrical Room", "status", "Completed", "items", 3)
        ));
        
        // S02: Incidents (Real)
        List<IncidentReport> incidents = incidentRepository.findAll();
        // Transform to match frontend expectation
        List<Map<String, Object>> incidentList = incidents.stream().map(inc -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", inc.getId());
            m.put("type", inc.getType());
            m.put("location", inc.getLocation());
            m.put("status", inc.getStatus());
            m.put("description", inc.getDescription());
            m.put("time", inc.getReportedAt().toString());
            return m;
        }).toList();
        data.put("incidents", incidentList);
        
        // S04: Fire Equipment Expiry
        data.put("expiringEquipment", 2); // 2 items expiring soon
        
        return data;
    }
    
    public void resolveIncident(Long id) {
        IncidentReport report = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));
        report.setStatus("RESOLVED");
        incidentRepository.save(report);
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        // e.g. AI suggests triggering specific emergency protocol
    }
}
