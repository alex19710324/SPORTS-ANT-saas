package com.sportsant.saas.workbench.service;

import com.sportsant.saas.device.repository.DeviceRepository;
import com.sportsant.saas.finance.service.FinanceService;
import com.sportsant.saas.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class StoreManagerServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private FinanceService financeService;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private StoreManagerService storeManagerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetStoreOverview() {
        Long storeId = 1L;
        
        when(financeService.getTodayRevenue()).thenReturn(new BigDecimal("1000.00"));
        when(financeService.getTodayVisitors()).thenReturn(50L);
        when(deviceRepository.count()).thenReturn(10L);
        when(deviceRepository.countByStatus("OFFLINE")).thenReturn(1L);
        
        Map<String, Object> overview = storeManagerService.getStoreOverview(storeId);
        
        assertNotNull(overview);
        assertEquals(new BigDecimal("1000.00"), overview.get("todayRevenue"));
        assertEquals(50L, overview.get("todayVisitors"));
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
