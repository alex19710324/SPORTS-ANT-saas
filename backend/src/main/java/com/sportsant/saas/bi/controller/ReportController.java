package com.sportsant.saas.bi.controller;

import com.sportsant.saas.bi.dto.DashboardMetrics;
import com.sportsant.saas.bi.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bi")
@Tag(name = "报表中心", description = "经营数据、BI 看板")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/dashboard")
    public DashboardMetrics getDashboardMetrics() {
        return reportService.getOverallMetrics();
    }
}
