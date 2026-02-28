package com.sportsant.saas.hr.controller;

import com.sportsant.saas.hr.entity.Employee;
import com.sportsant.saas.hr.entity.Schedule;
import com.sportsant.saas.hr.service.HRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/hr")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HRController {

    @Autowired
    private HRService hrService;

    @GetMapping("/employees")
    @PreAuthorize("hasRole('STORE_MANAGER') or hasRole('HR') or hasRole('ADMIN')")
    public List<Employee> getEmployees(@RequestParam Long storeId) {
        return hrService.getStoreEmployees(storeId);
    }

    @GetMapping("/schedules")
    @PreAuthorize("hasRole('STORE_MANAGER') or hasRole('HR') or hasRole('ADMIN')")
    public List<Schedule> getSchedules(
            @RequestParam Long storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return hrService.getStoreSchedules(storeId, start, end);
    }

    @PostMapping("/schedules")
    @PreAuthorize("hasRole('STORE_MANAGER') or hasRole('HR') or hasRole('ADMIN')")
    public Schedule createSchedule(@RequestBody Schedule schedule) {
        return hrService.createSchedule(schedule);
    }
}
