package com.sportsant.saas.safety.service;

import com.sportsant.saas.safety.entity.IncidentReport;
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
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SafetyService safetyService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testReportIncident() {
        String type = "FIRE";
        String location = "Kitchen";
        String description = "Smoke detected";
        String reporter = "Sensor";

        when(incidentRepository.save(any(IncidentReport.class))).thenAnswer(i -> {
            IncidentReport r = i.getArgument(0);
            r.setId(1L);
            return r;
        });

        IncidentReport result = safetyService.reportIncident(type, location, description, reporter);

        assertNotNull(result);
        assertEquals("REPORTED", result.getStatus());
        assertEquals(type, result.getType());
    }

    @Test
    public void testResolveIncident() {
        Long id = 1L;
        IncidentReport report = new IncidentReport();
        report.setId(id);
        report.setStatus("REPORTED");

        when(incidentRepository.findById(id)).thenReturn(Optional.of(report));
        when(incidentRepository.save(any(IncidentReport.class))).thenAnswer(i -> i.getArgument(0));

        safetyService.resolveIncident(id);

        assertEquals("RESOLVED", report.getStatus());
        verify(incidentRepository, times(1)).save(report);
    }
}
