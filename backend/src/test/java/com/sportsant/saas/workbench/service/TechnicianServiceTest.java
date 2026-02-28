package com.sportsant.saas.workbench.service;

import com.sportsant.saas.device.entity.WorkOrder;
import com.sportsant.saas.device.repository.DeviceRepository;
import com.sportsant.saas.device.repository.WorkOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TechnicianServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private TechnicianService technicianService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateStatus_ToInProgress() {
        Long orderId = 1L;
        Long techId = 101L;
        
        WorkOrder order = new WorkOrder();
        order.setId(orderId);
        order.setStatus("OPEN");
        
        when(workOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(workOrderRepository.save(any(WorkOrder.class))).thenAnswer(i -> i.getArguments()[0]);
        
        WorkOrder updated = technicianService.updateWorkOrderStatus(orderId, "IN_PROGRESS", techId);
        
        assertEquals("IN_PROGRESS", updated.getStatus());
        assertEquals(techId, updated.getAssignedTo());
        verify(workOrderRepository, times(1)).save(order);
    }

    @Test
    public void testUpdateStatus_ToClosed() {
        Long orderId = 1L;
        Long techId = 101L;
        
        WorkOrder order = new WorkOrder();
        order.setId(orderId);
        order.setStatus("IN_PROGRESS");
        order.setAssignedTo(techId);
        
        when(workOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(workOrderRepository.save(any(WorkOrder.class))).thenAnswer(i -> i.getArguments()[0]);
        
        WorkOrder updated = technicianService.updateWorkOrderStatus(orderId, "CLOSED", techId);
        
        assertEquals("CLOSED", updated.getStatus());
        assertNotNull(updated.getClosedAt());
        verify(workOrderRepository, times(1)).save(order);
    }
}
