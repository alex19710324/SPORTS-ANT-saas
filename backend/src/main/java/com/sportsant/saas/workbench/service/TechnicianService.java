package com.sportsant.saas.workbench.service;

import com.sportsant.saas.device.repository.DeviceRepository;
import com.sportsant.saas.device.repository.WorkOrderRepository;
import com.sportsant.saas.device.entity.Device;
import com.sportsant.saas.device.entity.WorkOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TechnicianService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    public Map<String, Object> getTechnicianOverview(Long technicianId) {
        // Mock data for now, in real app filter by technicianId or storeId
        List<WorkOrder> pendingOrders = workOrderRepository.findAll().stream()
                .filter(wo -> "PENDING".equals(wo.getStatus()) || "IN_PROGRESS".equals(wo.getStatus()))
                .limit(5)
                .toList();
        
        List<Device> offlineDevices = deviceRepository.findAll().stream()
                .filter(d -> "OFFLINE".equals(d.getStatus()))
                .limit(5)
                .toList();

        Map<String, Object> data = new HashMap<>();
        data.put("pendingOrders", pendingOrders);
        data.put("offlineDevices", offlineDevices);
        data.put("todayInspectionsCompleted", 12);
        data.put("todayInspectionsTotal", 15);
        
        return data;
    }
}
