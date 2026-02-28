package com.sportsant.saas.workbench.service;

import com.sportsant.saas.device.entity.WorkOrder;
import com.sportsant.saas.device.repository.DeviceRepository;
import com.sportsant.saas.device.repository.WorkOrderRepository;
import com.sportsant.saas.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TechnicianServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private TechnicianService technicianService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateWorkOrderStatus_CloseWithParts() {
        Long orderId = 1L;
        Long techId = 101L;
        WorkOrder order = new WorkOrder();
        order.setId(orderId);
        order.setStatus("IN_PROGRESS");

        when(workOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(workOrderRepository.save(any(WorkOrder.class))).thenAnswer(i -> i.getArgument(0));

        List<Map<String, Object>> partsUsed = List.of(
            Map.of("sku", "SKU-WIRE", "quantity", 2),
            Map.of("sku", "SKU-PAD", "quantity", 1)
        );

        WorkOrder result = technicianService.updateWorkOrderStatus(orderId, "CLOSED", techId, partsUsed);

        assertEquals("CLOSED", result.getStatus());
        assertNotNull(result.getClosedAt());
        
        // Verify Inventory Deductions
        verify(inventoryService, times(1)).updateStock(eq(1L), eq("SKU-WIRE"), eq(-2));
        verify(inventoryService, times(1)).updateStock(eq(1L), eq("SKU-PAD"), eq(-1));
    }
}
