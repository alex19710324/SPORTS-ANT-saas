package com.sportsant.saas.hq.controller;

import com.sportsant.saas.franchise.entity.FranchiseApplication;
import com.sportsant.saas.hq.service.HQService;
import com.sportsant.saas.store.entity.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HQControllerTest {

    @Mock
    private HQService hqService;

    @InjectMocks
    private HQController hqController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetGlobalOverview() {
        Map<String, Object> mockData = Map.of("totalRevenue", 10000);
        when(hqService.getGlobalOverview()).thenReturn(mockData);

        Map<String, Object> result = hqController.getGlobalOverview();
        assertEquals(mockData, result);
    }

    @Test
    public void testGetStoreMapData() {
        when(hqService.getStoreMapData()).thenReturn(List.of(new Store(), new Store()));
        Map<String, Object> result = hqController.getStoreMapData();
        assertTrue(result.containsKey("stores"));
        assertEquals(2, ((List<?>) result.get("stores")).size());
    }

    @Test
    public void testGetFranchiseApplications() {
        when(hqService.getFranchiseApplications()).thenReturn(List.of(new FranchiseApplication()));
        Map<String, Object> result = hqController.getFranchiseApplications();
        assertTrue(result.containsKey("applications"));
        assertEquals(1, ((List<?>) result.get("applications")).size());
    }

    @Test
    public void testApproveApplication() {
        Long appId = 1L;
        FranchiseApplication app = new FranchiseApplication();
        app.setId(appId);
        app.setStatus("APPROVED");

        when(hqService.approveFranchise(eq(appId), eq(true), anyString())).thenReturn(app);

        FranchiseApplication result = hqController.approveApplication(appId);
        assertEquals("APPROVED", result.getStatus());
    }
}
