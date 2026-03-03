package com.sportsant.saas.maintenance.controller;

import com.sportsant.saas.maintenance.entity.MaintenanceOrder;
import com.sportsant.saas.maintenance.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    @GetMapping("/orders/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN')")
    public List<MaintenanceOrder> getPendingOrders() {
        return maintenanceService.getPendingOrders();
    }

    @PostMapping("/orders")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN') or hasRole('MANAGER')")
    public MaintenanceOrder createOrder(@RequestBody Map<String, Object> payload) {
        Long deviceId = Long.valueOf(payload.get("deviceId").toString());
        String type = (String) payload.get("type");
        String description = (String) payload.get("description");
        String priority = (String) payload.get("priority");
        return maintenanceService.createOrder(deviceId, type, description, priority);
    }

    @PostMapping("/orders/{id}/complete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN')")
    public MaintenanceOrder completeOrder(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String resolution = payload.get("resolution");
        return maintenanceService.completeOrder(id, resolution);
    }

    @GetMapping("/predict/{deviceId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN')")
    public Map<String, Object> predictFaults(@PathVariable Long deviceId) {
        return maintenanceService.predictFaults(deviceId);
    }
}
