package com.sportsant.saas.hr.controller;

import com.sportsant.saas.hr.service.HRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hr")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HRController {

    @Autowired
    private HRService hrService;

    @GetMapping("/staff")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('STORE_MANAGER')")
    public List<Map<String, Object>> getStaffList() {
        return hrService.getStaffList();
    }

    @GetMapping("/payroll")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Map<String, Object> getPayroll() {
        return hrService.calculatePayroll();
    }
}
