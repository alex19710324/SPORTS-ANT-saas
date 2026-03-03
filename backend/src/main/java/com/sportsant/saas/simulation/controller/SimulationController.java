package com.sportsant.saas.simulation.controller;

import com.sportsant.saas.common.response.Result;
import com.sportsant.saas.simulation.dto.SimulationEvent;
import com.sportsant.saas.simulation.service.BusinessSimulatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/simulation")
@Tag(name = "Business Simulation", description = "全自动业务模拟引擎")
public class SimulationController {

    private final BusinessSimulatorService simulatorService;

    public SimulationController(BusinessSimulatorService simulatorService) {
        this.simulatorService = simulatorService;
    }

    @PostMapping("/start")
    @Operation(summary = "启动全自动模拟")
    public Result<String> start() {
        simulatorService.startSimulation();
        return Result.success("Simulation Engine Started");
    }

    @PostMapping("/stop")
    @Operation(summary = "停止模拟")
    public Result<String> stop() {
        simulatorService.stopSimulation();
        return Result.success("Simulation Engine Stopped");
    }

    @GetMapping("/status")
    @Operation(summary = "获取引擎状态")
    public Result<Map<String, Object>> getStatus() {
        return Result.success(Map.of("running", simulatorService.isRunning()));
    }

    @GetMapping("/events")
    @Operation(summary = "获取实时事件流")
    public Result<List<SimulationEvent>> getEvents() {
        return Result.success(simulatorService.getRecentEvents());
    }
}
