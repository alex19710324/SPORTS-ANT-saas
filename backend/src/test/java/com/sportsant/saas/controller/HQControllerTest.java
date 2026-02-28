package com.sportsant.saas.controller;

import com.sportsant.saas.franchise.entity.FranchiseApplication;
import com.sportsant.saas.franchise.repository.FranchiseApplicationRepository;
import com.sportsant.saas.store.entity.Store;
import com.sportsant.saas.store.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HQControllerTest {

    @Mock
    private StoreService storeService;

    @Mock
    private FranchiseApplicationRepository applicationRepository;

    @InjectMocks
    private HQController hqController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetGlobalOverview() {
        Map<String, Object> mockAggregates = Map.of("totalRevenue", 10000);
        when(storeService.getGlobalAggregates()).thenReturn(mockAggregates);

        Map<String, Object> result = hqController.getGlobalOverview();
        assertEquals(mockAggregates, result);
    }

    @Test
    public void testGetAllStores() {
        when(storeService.getAllStores()).thenReturn(List.of(new Store(), new Store()));
        List<Store> result = hqController.getAllStores();
        assertEquals(2, result.size());
    }

    @Test
    public void testGetPendingApplications() {
        when(applicationRepository.findByStatus("PENDING")).thenReturn(List.of(new FranchiseApplication()));
        List<FranchiseApplication> result = hqController.getPendingApplications();
        assertEquals(1, result.size());
    }

    @Test
    public void testApproveApplication() {
        Long appId = 1L;
        FranchiseApplication app = new FranchiseApplication();
        app.setId(appId);
        app.setStatus("PENDING");

        when(applicationRepository.findById(appId)).thenReturn(Optional.of(app));
        when(applicationRepository.save(any(FranchiseApplication.class))).thenAnswer(i -> i.getArgument(0));

        hqController.approveApplication(appId);

        assertEquals("APPROVED", app.getStatus());
        verify(applicationRepository, times(1)).save(app);
    }
}
