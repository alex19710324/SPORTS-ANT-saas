package com.sportsant.saas.workbench.controller;

import com.sportsant.saas.device.entity.WorkOrder;
import com.sportsant.saas.workbench.service.TechnicianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workbench/technician")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TechnicianController {

    @Autowired
    private TechnicianService technicianService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('TECHNICIAN') or hasRole('ADMIN')")
    public Map<String, Object> getOverview() {
        Long mockTechId = 101L; 
        return technicianService.getTechnicianOverview(mockTechId);
    }

    @PutMapping("/work-orders/{id}/status")
    @PreAuthorize("hasRole('TECHNICIAN') or hasRole('ADMIN')")
    public WorkOrder updateWorkOrderStatus(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        String newStatus = (String) payload.get("status");
        Long mockTechId = 101L; // In real app, get from SecurityContext
        List<Map<String, Object>> partsUsed = (List<Map<String, Object>>) payload.get("partsUsed");
        return technicianService.updateWorkOrderStatus(id, newStatus, mockTechId, partsUsed);
    }
}
