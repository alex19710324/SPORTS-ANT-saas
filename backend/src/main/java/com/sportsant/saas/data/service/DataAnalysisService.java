package com.sportsant.saas.data.service;

import com.sportsant.saas.finance.service.FinanceService;
import com.sportsant.saas.membership.repository.MemberRepository;
import com.sportsant.saas.device.repository.DeviceRepository;
import com.sportsant.saas.store.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataAnalysisService {

    @Autowired
    private FinanceService financeService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private StoreRepository storeRepository;

    public Map<String, Object> getGlobalKPIs() {
        Map<String, Object> kpis = new HashMap<>();
        
        // Total Members
        kpis.put("totalMembers", memberRepository.count());
        
        // Total Devices
        kpis.put("totalDevices", deviceRepository.count());
        
        // Active Stores
        kpis.put("activeStores", storeRepository.count());
        
        // Total Revenue (Aggregated from Finance Service)
        kpis.put("totalRevenue", financeService.getTodayRevenue()); 
        
        return kpis;
    }

    public Map<String, Object> getRevenueTrend() {
        // Mock Trend Data for Chart
        List<String> dates = new ArrayList<>();
        List<BigDecimal> values = new ArrayList<>();
        
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            dates.add(today.minusDays(i).toString());
            values.add(BigDecimal.valueOf(10000 + Math.random() * 5000));
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("dates", dates);
        data.put("values", values);
        return data;
    }

    public List<Map<String, Object>> getStoreLeaderboard() {
        // Mock Leaderboard
        return storeRepository.findAll().stream().map(store -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", store.getId());
            map.put("name", store.getName());
            map.put("revenue", BigDecimal.valueOf(50000 + Math.random() * 50000).intValue());
            map.put("members", 100 + (int)(Math.random() * 200));
            map.put("score", 90 + (int)(Math.random() * 10)); // Operation Score
            return map;
        }).sorted((a, b) -> ((Integer)b.get("revenue")).compareTo((Integer)a.get("revenue")))
          .limit(10)
          .collect(Collectors.toList());
    }
}
