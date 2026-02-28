package com.sportsant.saas.data.service;

import com.sportsant.saas.iot.entity.Zone;
import com.sportsant.saas.iot.repository.ZoneRepository;
import com.sportsant.saas.membership.repository.MemberRepository;
import com.sportsant.saas.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class AnalyticsService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        // 1. Total Revenue (Mock calculation for MVP, real would sum OrderRepository)
        data.put("totalRevenue", 158900.00); 
        
        // 2. Member Growth (Mock trend)
        data.put("memberGrowth", List.of(120, 132, 101, 134, 90, 230, 210));
        
        // 3. Peak Hours (Based on IoT Occupancy or Bookings)
        data.put("peakHours", calculatePeakHours());
        
        // 4. Category Sales
        data.put("categorySales", Map.of(
            "Memberships", 45,
            "Classes", 30,
            "Merchandise", 15,
            "F&B", 10
        ));

        return data;
    }

    private Map<String, Integer> calculatePeakHours() {
        // In a real system, query historical booking/IoT data
        // Mock: Hour -> Average Occupancy
        Map<String, Integer> peakHours = new HashMap<>();
        peakHours.put("08:00", 20);
        peakHours.put("10:00", 45);
        peakHours.put("12:00", 30);
        peakHours.put("14:00", 25);
        peakHours.put("16:00", 40);
        peakHours.put("18:00", 85); // Peak
        peakHours.put("20:00", 70);
        peakHours.put("22:00", 15);
        return peakHours;
    }
}
