package com.sportsant.saas.hr.service;

import com.sportsant.saas.hr.entity.Employee;
import com.sportsant.saas.hr.entity.Schedule;
import com.sportsant.saas.hr.repository.EmployeeRepository;
import com.sportsant.saas.hr.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HRService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Employee> getStoreEmployees(Long storeId) {
        return employeeRepository.findByStoreId(storeId);
    }

    public List<Schedule> getStoreSchedules(Long storeId, LocalDateTime start, LocalDateTime end) {
        return scheduleRepository.findByStoreIdAndStartTimeBetween(storeId, start, end);
    }
    
    public Schedule createSchedule(Schedule schedule) {
        // Basic validation: Check for overlaps (omitted for brevity)
        return scheduleRepository.save(schedule);
    }
}
