package com.sportsant.saas.report.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRevenueReport_Weekly() {
        Map<String, Object> report = reportService.getRevenueReport("WEEKLY");
        
        assertNotNull(report);
        assertEquals("WEEKLY", report.get("period"));
        assertTrue(report.containsKey("labels"));
        assertTrue(report.containsKey("data"));
        
        List<?> data = (List<?>) report.get("data");
        assertEquals(7, data.size());
    }

    @Test
    public void testGetRevenueReport_Monthly() {
        Map<String, Object> report = reportService.getRevenueReport("MONTHLY");
        
        assertNotNull(report);
        assertEquals("MONTHLY", report.get("period"));
        
        List<?> data = (List<?>) report.get("data");
        assertEquals(5, data.size());
    }

    @Test
    public void testGetVisitorReport() {
        Map<String, Object> report = reportService.getVisitorReport("PEAK_HOURS");
        
        assertNotNull(report);
        List<?> labels = (List<?>) report.get("labels");
        // 8:00 to 22:00 is 15 hours
        assertEquals(15, labels.size());
    }
}
