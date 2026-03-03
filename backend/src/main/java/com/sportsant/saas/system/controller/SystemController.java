package com.sportsant.saas.system.controller;

import com.sportsant.saas.system.entity.SystemAuditLog;
import com.sportsant.saas.system.service.SystemAuditService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
@Tag(name = "系统管理", description = "系统状态、JVM 信息")
public class SystemController {

    @Autowired
    private SystemAuditService auditService;

    @GetMapping("/logs")
    public List<SystemAuditLog> getLogs() {
        return auditService.getAllLogs();
    }

    @GetMapping("/health")
    public Map<String, Object> getHealth() {
        Map<String, Object> health = new HashMap<>();
        
        // JVM Memory
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        
        health.put("status", "UP");
        health.put("jvm_heap_used", heapUsage.getUsed() / 1024 / 1024 + " MB");
        health.put("jvm_heap_max", heapUsage.getMax() / 1024 / 1024 + " MB");
        health.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime() / 1000 + " seconds");
        
        // Mock DB Status
        health.put("db_status", "CONNECTED");
        health.put("db_latency", "5ms");

        return health;
    }
}
