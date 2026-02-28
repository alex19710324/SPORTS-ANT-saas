package com.sportsant.saas.hr.service;

import com.sportsant.saas.hr.entity.Employee;
import com.sportsant.saas.hr.entity.Schedule;
import com.sportsant.saas.hr.repository.EmployeeRepository;
import com.sportsant.saas.hr.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HRServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private HRService hrService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetStoreEmployees() {
        Long storeId = 1L;
        when(employeeRepository.findByStoreId(storeId)).thenReturn(List.of(new Employee(), new Employee()));

        List<Employee> result = hrService.getStoreEmployees(storeId);
        assertEquals(2, result.size());
    }

    @Test
    public void testCreateSchedule() {
        Schedule schedule = new Schedule();
        schedule.setStartTime(LocalDateTime.now());
        schedule.setEndTime(LocalDateTime.now().plusHours(8));
        schedule.setType("REGULAR");

        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(i -> i.getArgument(0));

        Schedule result = hrService.createSchedule(schedule);
        assertNotNull(result);
        assertEquals("REGULAR", result.getType());
        verify(scheduleRepository, times(1)).save(schedule);
    }
}
