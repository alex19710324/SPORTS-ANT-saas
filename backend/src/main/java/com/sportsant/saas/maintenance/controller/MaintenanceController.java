package com.sportsant.saas.maintenance.controller;

import com.sportsant.saas.maintenance.entity.Device;
import com.sportsant.saas.maintenance.entity.MaintenanceTask;
import com.sportsant.saas.maintenance.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    @GetMapping("/tasks/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN') or hasRole('STORE_MANAGER')")
    public List<MaintenanceTask> getPendingTasks() {
        return maintenanceService.getPendingTasks();
    }

    @GetMapping("/devices")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN') or hasRole('STORE_MANAGER')")
    public List<Device> getAllDevices() {
        return maintenanceService.getAllDevices();
    }

    @PutMapping("/tasks/{id}/complete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN')")
    public void completeTask(@PathVariable Long id) {
        maintenanceService.completeTask(id);
    }
}
