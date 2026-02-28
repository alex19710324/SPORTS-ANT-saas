package com.sportsant.saas.report.controller;

import com.sportsant.saas.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('STORE_MANAGER') or hasRole('ADMIN')")
    public Map<String, Object> getRevenueReport(@RequestParam(defaultValue = "WEEKLY") String period) {
        return reportService.getRevenueReport(period);
    }

    @GetMapping("/visitors")
    @PreAuthorize("hasRole('STORE_MANAGER') or hasRole('ADMIN')")
    public Map<String, Object> getVisitorReport(@RequestParam(defaultValue = "PEAK_HOURS") String period) {
        return reportService.getVisitorReport(period);
    }
}
