package com.sportsant.saas.iot.controller;

import com.sportsant.saas.common.response.Result;
import com.sportsant.saas.device.entity.Device;
import com.sportsant.saas.iot.service.PredictiveMaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/iot/predictive")
@Tag(name = "IoT Predictive Maintenance", description = "设备故障预测与智能维护")
public class PredictiveMaintenanceController {

    private final PredictiveMaintenanceService maintenanceService;

    public PredictiveMaintenanceController(PredictiveMaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping("/analyze")
    @Operation(summary = "手动触发全量设备健康分析")
    public Result<String> triggerAnalysis() {
        maintenanceService.analyzeDeviceHealth();
        return Result.success("Analysis triggered successfully");
    }

    @GetMapping("/devices")
    @Operation(summary = "获取所有设备状态")
    public Result<List<Device>> getAllDevices() {
        return Result.success(maintenanceService.getAllDevices());
    }

    @GetMapping("/devices/{id}")
    @Operation(summary = "获取单个设备详情")
    public Result<Device> getDevice(@PathVariable Long id) {
        Device device = maintenanceService.getDeviceStatus(id);
        if (device != null) {
            return Result.success(device);
        }
        return Result.error("Device not found");
    }

    @GetMapping("/dashboard/stats")
    @Operation(summary = "获取仪表盘统计数据")
    public Result<Map<String, Object>> getDashboardStats() {
        List<Device> allDevices = maintenanceService.getAllDevices();
        
        long total = allDevices.size();
        long online = allDevices.stream().filter(d -> "ONLINE".equals(d.getStatus())).count();
        long highRisk = allDevices.stream().filter(d -> d.getHealthScore() != null && d.getHealthScore() < 60).count();
        long critical = allDevices.stream().filter(d -> d.getHealthScore() != null && d.getHealthScore() < 40).count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDevices", total);
        stats.put("onlineDevices", online);
        stats.put("highRiskDevices", highRisk);
        stats.put("criticalDevices", critical);
        stats.put("predictionAccuracy", 92.5); // Mock
        stats.put("costSaving", 125000); // Mock
        
        return Result.success(stats);
    }
    
    @GetMapping("/risky-devices")
    @Operation(summary = "获取高风险设备列表")
    public Result<List<Device>> getRiskyDevices() {
        List<Device> risky = maintenanceService.getAllDevices().stream()
                .filter(d -> d.getHealthScore() != null && d.getHealthScore() < 70)
                .collect(Collectors.toList());
        return Result.success(risky);
    }
}
