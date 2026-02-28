package com.sportsant.saas.workbench.service;

import com.sportsant.saas.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StoreManagerServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreManagerService storeManagerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetStoreOverview() {
        Long storeId = 1L;
        Map<String, Object> overview = storeManagerService.getStoreOverview(storeId);
        
        assertNotNull(overview);
        assertTrue(overview.containsKey("todayRevenue"));
        assertTrue(overview.containsKey("pendingApprovals"));
        
        Object approvals = overview.get("pendingApprovals");
        assertTrue(approvals instanceof List);
        List<?> approvalList = (List<?>) approvals;
        assertEquals(2, approvalList.size());
    }

    @Test
    public void testApproveRequest() {
        // Since approveRequest only prints to console for now, we just ensure it doesn't throw exception
        assertDoesNotThrow(() -> storeManagerService.approveRequest(1L, 10L));
    }
}
