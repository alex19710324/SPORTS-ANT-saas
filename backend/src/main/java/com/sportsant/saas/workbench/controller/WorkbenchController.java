package com.sportsant.saas.workbench.controller;

import com.sportsant.saas.workbench.service.WorkbenchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workbench")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WorkbenchController {

    @Autowired
    private WorkbenchService workbenchService;

    // --- P0: Store Manager Dashboard ---
    @GetMapping("/manager/overview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STORE_MANAGER')")
    public Map<String, Object> getManagerOverview(@RequestParam(defaultValue = "1") Long storeId) {
        return workbenchService.getManagerOverview(storeId);
    }

    // --- P0: Front Desk Workbench ---
    @GetMapping("/frontdesk/tasks")
    @PreAuthorize("hasRole('FRONT_DESK') or hasRole('ADMIN')")
    public Map<String, Object> getFrontDeskTasks() {
        return workbenchService.getFrontDeskTasks();
    }
    
    // --- P0: Technician Workbench ---
    @GetMapping("/technician/tasks")
    @PreAuthorize("hasRole('TECHNICIAN') or hasRole('ADMIN')")
    public Map<String, Object> getTechnicianTasks() {
        return workbenchService.getTechnicianTasks();
    }
}
