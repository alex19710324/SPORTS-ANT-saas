package com.sportsant.saas.workbench.service;

import com.sportsant.saas.device.repository.DeviceRepository;
import com.sportsant.saas.device.repository.WorkOrderRepository;
import com.sportsant.saas.device.entity.Device;
import com.sportsant.saas.device.entity.WorkOrder;
import com.sportsant.saas.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TechnicianService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private InventoryService inventoryService;

    public Map<String, Object> getTechnicianOverview(Long technicianId) {
        // Mock data for now, in real app filter by technicianId or storeId
        List<WorkOrder> pendingOrders = workOrderRepository.findAll().stream()
                .filter(wo -> "OPEN".equals(wo.getStatus()) || "IN_PROGRESS".equals(wo.getStatus()))
                .limit(10)
                .collect(Collectors.toList());
        
        List<Device> offlineDevices = deviceRepository.findAll().stream()
                .filter(d -> "OFFLINE".equals(d.getStatus()))
                .limit(10)
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("pendingOrders", pendingOrders);
        data.put("offlineDevices", offlineDevices);
        data.put("todayInspectionsCompleted", 12);
        data.put("todayInspectionsTotal", 15);
        
        return data;
    }

    @Transactional
    public WorkOrder updateWorkOrderStatus(Long orderId, String newStatus, Long technicianId, List<Map<String, Object>> partsUsed) {
        WorkOrder order = workOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Work Order not found"));
        
        if ("IN_PROGRESS".equals(newStatus)) {
            order.setAssignedTo(technicianId);
            order.setStatus("IN_PROGRESS");
        } else if ("CLOSED".equals(newStatus)) {
            order.setStatus("CLOSED");
            order.setClosedAt(LocalDateTime.now());
            
            // Deduct Inventory Parts
            if (partsUsed != null && !partsUsed.isEmpty()) {
                Long storeId = 1L; // Assuming storeId context or from order.device.store
                for (Map<String, Object> part : partsUsed) {
                    String sku = (String) part.get("sku");
                    Integer quantity = (Integer) part.get("quantity");
                    inventoryService.updateStock(storeId, sku, -quantity);
                }
            }
        } else {
             throw new RuntimeException("Invalid status transition: " + newStatus);
        }
        
        return workOrderRepository.save(order);
    }
}
