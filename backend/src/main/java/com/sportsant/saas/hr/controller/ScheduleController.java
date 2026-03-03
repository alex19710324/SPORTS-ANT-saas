package com.sportsant.saas.hr.controller;

import com.sportsant.saas.hr.entity.Shift;
import com.sportsant.saas.hr.service.WorkforceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hr/schedule")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ScheduleController {

    @Autowired
    private WorkforceService workforceService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public List<Shift> getSchedule(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return workforceService.getShifts(start, end);
    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public List<Shift> generateSchedule(
            @RequestParam("weekStart") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        return workforceService.generateSchedule(weekStart);
    }
}
