package com.sportsant.saas.workbench.service;

import com.sportsant.saas.store.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StoreManagerService {

    @Autowired
    private StoreRepository storeRepository;

    public Map<String, Object> getStoreOverview(Long storeId) {
        // In a real app, we would fetch this from the database based on the storeId
        // For now, we'll return mock data for the dashboard
        Map<String, Object> data = new HashMap<>();
        data.put("todayRevenue", new BigDecimal("12500.50"));
        data.put("todayVisitors", 320);
        data.put("deviceOnlineRate", 98.5); // Percentage
        data.put("pendingApprovals", 5);
        data.put("alertCount", 2);
        data.put("kocContribution", new BigDecimal("4500.00")); // Revenue from KOCs
        
        return data;
    }
}
