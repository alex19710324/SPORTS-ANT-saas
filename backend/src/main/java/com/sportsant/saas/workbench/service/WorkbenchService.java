package com.sportsant.saas.workbench.service;

import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.device.entity.Device;
import com.sportsant.saas.device.repository.DeviceRepository;
import com.sportsant.saas.device.repository.WorkOrderRepository;
import com.sportsant.saas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkbenchService implements AiAware {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    public Map<String, Object> getManagerOverview(Long storeId) {
        // ... (Existing Logic) ...
        Map<String, Object> data = new HashMap<>();
        data.put("revenue", 12345.67);
        data.put("visitors", 230);
        data.put("redemptions", 189);
        data.put("alertCount", 2);
        data.put("trend", Map.of(
            "revenue", new int[]{12000, 12500, 12345},
            "visitors", new int[]{200, 210, 230}
        ));
        return data;
    }

    public Map<String, Object> getFrontDeskTasks() {
        // ... (Existing Logic) ...
        Map<String, Object> data = new HashMap<>();
        data.put("todayTarget", 5000);
        data.put("currentSales", 1200);
        data.put("pendingCheckins", 3);
        return data;
    }
    
    /**
     * Now integrated with Real Device Data
     */
    public Map<String, Object> getTechnicianTasks() {
        Map<String, Object> data = new HashMap<>();
        
        // 1. Pending Work Orders
        int pendingOrders = workOrderRepository.findByStatus("PENDING").size();
        data.put("pendingWorkOrders", pendingOrders);
        
        // 2. Offline/Faulty Devices
        List<Device> faultyDevices = deviceRepository.findByStatus("FAULT");
        List<Device> offlineDevices = deviceRepository.findByStatus("OFFLINE");
        data.put("offlineDevices", offlineDevices.size());
        data.put("faultyDevices", faultyDevices.size());
        
        // 3. Inspection Progress (Mock for now)
        data.put("inspectionProgress", 0.45); 
        
        return data;
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        // ...
    }
}
