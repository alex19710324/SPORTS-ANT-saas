package com.sportsant.saas.report.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    public Map<String, Object> getRevenueReport(String period) {
        Map<String, Object> report = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();

        LocalDate today = LocalDate.now();
        
        if ("WEEKLY".equals(period)) {
            for (int i = 6; i >= 0; i--) {
                labels.add(today.minusDays(i).toString());
                data.add(Math.random() * 5000 + 1000); // Mock daily revenue
            }
        } else if ("MONTHLY".equals(period)) {
            for (int i = 4; i >= 0; i--) {
                labels.add("Week " + (5 - i));
                data.add(Math.random() * 30000 + 10000); // Mock weekly revenue
            }
        }

        report.put("labels", labels);
        report.put("data", data);
        report.put("period", period);
        return report;
    }

    public Map<String, Object> getVisitorReport(String period) {
        Map<String, Object> report = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        if ("PEAK_HOURS".equals(period)) {
            for (int i = 8; i <= 22; i++) {
                labels.add(i + ":00");
                data.add((int) (Math.random() * 100)); // Mock hourly visitors
            }
        }

        report.put("labels", labels);
        report.put("data", data);
        return report;
    }
}
