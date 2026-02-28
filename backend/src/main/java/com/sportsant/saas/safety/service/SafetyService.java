package com.sportsant.saas.safety.service;

import com.sportsant.saas.ai.service.AiBrainService;
import com.sportsant.saas.safety.entity.Incident;
import com.sportsant.saas.safety.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SafetyService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private AiBrainService aiBrainService;

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    public List<Incident> getActiveIncidents() {
        // Return incidents that are not RESOLVED
        // For MVP, just simple filter or custom query. 
        // Using findAll + stream for simplicity
        return incidentRepository.findAll().stream()
                .filter(i -> !"RESOLVED".equals(i.getStatus()))
                .toList();
    }

    @Transactional
    public Incident reportIncident(Incident incident) {
        // AI Analysis: If severity is HIGH/CRITICAL, suggest immediate action
        if ("HIGH".equals(incident.getSeverity()) || "CRITICAL".equals(incident.getSeverity())) {
            aiBrainService.proposeSuggestion(
                "CRITICAL INCIDENT: " + incident.getTitle(),
                "A severe incident has been reported at " + incident.getLocation() + ". Immediate response required.",
                "SAFETY",
                "CRITICAL",
                "/workbench/security" // Link to security dashboard
            );
        }

        return incidentRepository.save(incident);
    }

    @Transactional
    public Incident updateStatus(Long id, String status, String notes) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));
        
        incident.setStatus(status);
        if (notes != null) {
            incident.setResolutionNotes(notes);
        }
        
        if ("RESOLVED".equals(status)) {
            incident.setResolvedAt(LocalDateTime.now());
        }

        return incidentRepository.save(incident);
    }
}
