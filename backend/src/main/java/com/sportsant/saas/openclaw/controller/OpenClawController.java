package com.sportsant.saas.openclaw.controller;

import com.sportsant.saas.common.response.ApiResponse;
import com.sportsant.saas.openclaw.dto.OpenClawDashboardDTO;
import com.sportsant.saas.openclaw.service.OpenClawService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/openclaw")
@Tag(name = "OpenClaw Command Center", description = "龙虾哥的指挥中心")
public class OpenClawController {

    private final OpenClawService openClawService;

    public OpenClawController(OpenClawService openClawService) {
        this.openClawService = openClawService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "获取全能指挥官数据")
    public ResponseEntity<ApiResponse<OpenClawDashboardDTO>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(openClawService.getDashboardData()));
    }

    @GetMapping("/handshake")
    @Operation(summary = "建立握手连接")
    public ResponseEntity<ApiResponse<String>> handshake() {
        return ResponseEntity.ok(ApiResponse.success(openClawService.performHandshake()));
    }
    
    @org.springframework.web.bind.annotation.PostMapping("/trigger")
    @Operation(summary = "触发紧急事件")
    public ResponseEntity<ApiResponse<String>> triggerEvent(@org.springframework.web.bind.annotation.RequestParam String type) {
        return ResponseEntity.ok(ApiResponse.success(openClawService.triggerEvent(type)));
    }
}
