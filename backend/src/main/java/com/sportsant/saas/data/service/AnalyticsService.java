package com.sportsant.saas.data.service;

import com.sportsant.saas.device.repository.DeviceRepository;
import com.sportsant.saas.mall.repository.MallOrderRepository;
import com.sportsant.saas.membership.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired
    private MallOrderRepository mallOrderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        // 1. Total Revenue
        BigDecimal totalRevenue = mallOrderRepository.sumTotalRevenue();
        data.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO); 
        
        // 2. Member Growth (Last 7 Days)
        List<Integer> memberGrowth = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);
            Integer count = memberRepository.countMembersJoinedBetween(start, end);
            memberGrowth.add(count);
            dates.add(date.toString());
        }
        data.put("memberGrowth", memberGrowth);
        data.put("dates", dates);
        
        // 3. Peak Hours (Simulated based on orders for now, or use existing logic)
        // Ideally we query order timestamps to find peak hours
        data.put("peakHours", calculatePeakHoursFromOrders());
        
        // 4. Device Health
        data.put("deviceHealth", getDeviceHealthStats());

        // 5. Real-time Visitors (Mock for now)
        data.put("realtimeVisitors", 128);

        return data;
    }

    private Map<String, Integer> calculatePeakHoursFromOrders() {
        // Simple bucket count of orders by hour for today
        // In real Data Middle Platform, this would be a pre-calculated aggregate in ClickHouse/Redis
        Map<String, Integer> peakHours = new HashMap<>();
        // Initialize
        for(int h=8; h<=22; h+=2) {
            peakHours.put(String.format("%02d:00", h), 0);
        }
        
        // Fetch today's orders
        // Note: For performance, we should use a specific aggregate query. 
        // Here we mock the distribution pattern but scale it by actual order count if possible.
        // For MVP, let's stick to the mock distribution but maybe randomized slightly.
        peakHours.put("08:00", 20);
        peakHours.put("10:00", 45);
        peakHours.put("12:00", 30);
        peakHours.put("14:00", 25);
        peakHours.put("16:00", 40);
        peakHours.put("18:00", 85); 
        peakHours.put("20:00", 70);
        peakHours.put("22:00", 15);
        
        return peakHours;
    }

    private Map<String, Object> getDeviceHealthStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", deviceRepository.count());
        stats.put("online", deviceRepository.countByStatus("ONLINE"));
        stats.put("fault", deviceRepository.countByStatus("FAULT"));
        stats.put("offline", deviceRepository.countByStatus("OFFLINE"));
        
        Double avgScore = deviceRepository.getAverageHealthScore();
        stats.put("avgHealthScore", avgScore != null ? avgScore.intValue() : 100);
        
        return stats;
    }

    public Integer getTodayVisitorCount() {
        // Mock implementation or fetch from real data source (e.g. gate access logs)
        return 128 + (int)(Math.random() * 50);
    }
}
