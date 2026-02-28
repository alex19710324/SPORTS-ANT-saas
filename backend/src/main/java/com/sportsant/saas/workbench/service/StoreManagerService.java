package com.sportsant.saas.workbench.service;

import com.sportsant.saas.device.repository.DeviceRepository;
import com.sportsant.saas.finance.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StoreManagerService {

    @Autowired
    private FinanceService financeService;

    @Autowired
    private DeviceRepository deviceRepository;

    public Map<String, Object> getStoreOverview(Long storeId) {
        // In a real app, we would fetch this from the database based on the storeId
        // For now, we'll return mock data for the dashboard
        Map<String, Object> data = new HashMap<>();
        data.put("todayRevenue", financeService.getTodayRevenue()); // Real Data
        data.put("todayVisitors", financeService.getTodayVisitors()); // Real Data
        
        long totalDevices = deviceRepository.count();
        long offlineDevices = deviceRepository.countByStatus("OFFLINE");
        double onlineRate = totalDevices > 0 ? (double)(totalDevices - offlineDevices) / totalDevices * 100 : 0;
        data.put("deviceOnlineRate", String.format("%.1f", onlineRate));
        
        data.put("alertCount", 2);
        data.put("kocContribution", new BigDecimal("4500.00")); // Revenue from KOCs
        
        // Mock pending approvals
        List<Map<String, Object>> approvals = new ArrayList<>();
        Map<String, Object> approval1 = new HashMap<>();
        approval1.put("id", 1L);
        approval1.put("type", "Refund");
        approval1.put("applicant", "John Doe");
        approval1.put("summary", "Refund for Order #1234");
        approvals.add(approval1);

        Map<String, Object> approval2 = new HashMap<>();
        approval2.put("id", 2L);
        approval2.put("type", "Leave");
        approval2.put("applicant", "Alice Smith");
        approval2.put("summary", "Sick leave for 2 days");
        approvals.add(approval2);
        
        data.put("pendingApprovals", approvals);
        
        // Mock cost breakdown
        Map<String, BigDecimal> costs = new HashMap<>();
        costs.put("Staff", new BigDecimal("5000"));
        costs.put("Utilities", new BigDecimal("1200"));
        costs.put("Maintenance", new BigDecimal("800"));
        data.put("costBreakdown", costs);

        return data;
    }

    public void approveRequest(Long approvalId, Long managerId) {
        // Logic to approve request (update database)
        System.out.println("Manager " + managerId + " approved request " + approvalId);
        // In real implementation: find approval entity, check status, update status, save.
    }
}
