package com.sportsant.saas.safety.service;

import com.sportsant.saas.ai.service.AiBrainService;
import com.sportsant.saas.safety.entity.Incident;
import com.sportsant.saas.safety.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SafetyServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private AiBrainService aiBrainService;

    @InjectMocks
    private SafetyService safetyService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testReportIncident() {
        Incident incident = new Incident();
        incident.setType("FIRE");
        incident.setLocation("Kitchen");
        incident.setDescription("Smoke detected");
        incident.setSeverity("HIGH");

        when(incidentRepository.save(any(Incident.class))).thenAnswer(i -> {
            Incident r = i.getArgument(0);
            r.setId(1L);
            r.setStatus("OPEN");
            return r;
        });

        Incident result = safetyService.reportIncident(incident);

        assertNotNull(result);
        assertEquals("OPEN", result.getStatus());
        assertEquals("FIRE", result.getType());
        verify(aiBrainService, times(1)).proposeSuggestion(anyString(), anyString(), eq("SAFETY"), eq("CRITICAL"), anyString());
    }

    @Test
    public void testResolveIncident() {
        Long id = 1L;
        Incident report = new Incident();
        report.setId(id);
        report.setStatus("OPEN");

        when(incidentRepository.findById(id)).thenReturn(Optional.of(report));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(i -> i.getArgument(0));

        safetyService.resolveIncident(id);

        assertEquals("RESOLVED", report.getStatus());
        verify(incidentRepository, times(1)).save(report);
    }
}
