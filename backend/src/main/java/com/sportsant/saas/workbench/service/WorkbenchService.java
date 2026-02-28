package com.sportsant.saas.workbench.service;

import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.device.entity.Device;
import com.sportsant.saas.device.repository.DeviceRepository;
import com.sportsant.saas.device.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkbenchService implements AiAware {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    public Map<String, Object> getManagerOverview(Long storeId) {
        Map<String, Object> data = new HashMap<>();
        
        // M01: Business Overview
        data.put("revenue", 12345.67);
        data.put("visitors", 230);
        data.put("redemptions", 189);
        data.put("blindboxSales", 3456.78);
        data.put("kocContribution", 890.12);
        
        // M01: Device Status
        long totalDevices = deviceRepository.count();
        long onlineDevices = deviceRepository.findByStatus("ONLINE").size();
        double onlineRate = totalDevices > 0 ? (double) onlineDevices / totalDevices : 0.0;
        data.put("deviceOnlineRate", String.format("%.1f%%", onlineRate * 100));
        
        // M01: Alerts
        data.put("alertCount", 5); // Total Alerts
        data.put("securityAlerts", 2); // Security-specific
        data.put("complaintPending", 3); // Complaints
        
        // M02: Pending Approvals (Mock)
        data.put("pendingApprovals", List.of(
            Map.of("id", 1, "type", "Purchase", "summary", "Office Supplies", "applicant", "Alice", "date", "2026-02-28"),
            Map.of("id", 2, "type", "Leave", "summary", "Sick Leave", "applicant", "Bob", "date", "2026-02-28")
        ));
        
        // M04: Cost Breakdown (Mock)
        data.put("costBreakdown", Map.of(
            "labor", 5000,
            "utilities", 1200,
            "materials", 800,
            "marketing", 1500
        ));

        data.put("trend", Map.of(
            "revenue", new int[]{12000, 12500, 12345},
            "visitors", new int[]{200, 210, 230}
        ));
        return data;
    }

    public Map<String, Object> getFrontDeskTasks() {
        Map<String, Object> data = new HashMap<>();
        
        // F04: Personal Performance
        data.put("todayTarget", 5000.00);
        data.put("currentSales", 1250.50);
        data.put("targetCompletion", 0.25); // 25%
        
        // F01: Pending Tasks
        data.put("pendingCheckins", 5);
        data.put("pendingVerifications", 2);
        
        // F06: High Peak Alert (Mock)
        data.put("peakAlert", true); // Should trigger a UI warning
        
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
        
        // 3. Inspection Progress (Mock for now - T02)
        // TODO: Fetch from InspectionRepository once created
        data.put("inspectionProgress", 0.45); 
        data.put("todayInspections", List.of(
            Map.of("id", 101, "area", "Zone A", "status", "Pending", "deviceCount", 12),
            Map.of("id", 102, "area", "Zone B", "status", "Completed", "deviceCount", 8)
        ));

        // 4. Preventive Maintenance (T06 - AI Driven)
        // Mocking AI suggestions
        data.put("maintenanceSuggestions", List.of(
            Map.of("deviceId", "VR-005", "reason", "Runtime > 500h", "urgency", "Medium"),
            Map.of("deviceId", "ARC-012", "reason", "Temp Spikes", "urgency", "High")
        ));
        
        return data;
    }

    public Map<String, Object> getSecurityTasks() {
        Map<String, Object> data = new HashMap<>();
        
        // S01: Safety Inspections
        data.put("todayInspections", List.of(
            Map.of("id", 201, "area", "Fire Exits", "status", "Pending", "items", 5),
            Map.of("id", 202, "area", "Electrical Room", "status", "Completed", "items", 3)
        ));
        
        // S02: Incidents
        data.put("incidents", List.of(
            Map.of("id", 301, "type", "Injury", "location", "Zone A", "status", "Investigating", "time", "10:30 AM")
        ));
        
        // S04: Fire Equipment Expiry
        data.put("expiringEquipment", 2); // 2 items expiring soon
        
        return data;
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        // ...
    }
}
