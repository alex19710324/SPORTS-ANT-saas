package com.sportsant.saas.hr.controller;

import com.sportsant.saas.hr.entity.Shift;
import com.sportsant.saas.hr.service.WorkforceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hr/workforce")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WorkforceController {

    @Autowired
    private WorkforceService workforceService;

    @GetMapping("/shifts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('STORE_MANAGER')")
    public List<Shift> getShifts(
        @RequestParam String start,
        @RequestParam String end
    ) {
        return workforceService.getShifts(LocalDate.parse(start), LocalDate.parse(end));
    }

    @PostMapping("/schedule/generate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public List<Shift> generateSchedule(@RequestParam String weekStart) {
        return workforceService.generateSchedule(LocalDate.parse(weekStart));
    }
}
