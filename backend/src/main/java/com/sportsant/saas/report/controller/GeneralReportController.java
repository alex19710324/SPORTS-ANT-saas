package com.sportsant.saas.report.controller;

import com.sportsant.saas.report.service.GeneralReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "通用报表", description = "营收统计、趋势分析")
public class GeneralReportController {

    @Autowired
    private GeneralReportService reportService;

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
