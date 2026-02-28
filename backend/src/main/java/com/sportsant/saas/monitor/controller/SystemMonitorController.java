package com.sportsant.saas.monitor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SystemMonitorController {

    @Autowired
    private MetricsEndpoint metricsEndpoint;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getSystemMetrics() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // CPU
        dashboard.put("systemCpu", getMetric("system.cpu.usage"));
        dashboard.put("processCpu", getMetric("process.cpu.usage"));
        
        // Memory
        dashboard.put("jvmMemoryUsed", getMetric("jvm.memory.used"));
        dashboard.put("jvmMemoryMax", getMetric("jvm.memory.max"));
        
        // HTTP
        dashboard.put("httpRequests", getMetric("http.server.requests", "count"));
        
        // Threads
        dashboard.put("liveThreads", getMetric("jvm.threads.live"));
        
        // Uptime
        dashboard.put("uptime", getMetric("process.uptime"));

        return dashboard;
    }

    private Double getMetric(String name) {
        return getMetric(name, "VALUE");
    }

    private Double getMetric(String name, String type) {
        MetricsEndpoint.MetricDescriptor metric = metricsEndpoint.metric(name, null);
        if (metric != null && !metric.getMeasurements().isEmpty()) {
            if ("count".equalsIgnoreCase(type)) {
                // Find COUNT measurement
                return metric.getMeasurements().stream()
                    .filter(m -> "COUNT".equals(m.getStatistic().name()))
                    .findFirst()
                    .map(MetricsEndpoint.Sample::getValue)
                    .orElse(0.0);
            }
            return metric.getMeasurements().get(0).getValue();
        }
        return 0.0;
    }
}
