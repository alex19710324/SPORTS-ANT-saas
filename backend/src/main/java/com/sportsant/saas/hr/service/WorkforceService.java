package com.sportsant.saas.hr.service;

import com.sportsant.saas.booking.entity.Booking;
import com.sportsant.saas.data.service.AnalyticsService;
import com.sportsant.saas.hr.entity.Shift;
import com.sportsant.saas.hr.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WorkforceService {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private AnalyticsService analyticsService;

    @Transactional
    public void autoAssignTrainer(Booking booking) {
        // Find a Trainer shift that covers the booking time
        List<Shift> availableShifts = shiftRepository.findByStartTimeBetween(booking.getStartTime().minusHours(1), booking.getEndTime().plusHours(1));
        
        Shift trainerShift = availableShifts.stream()
            .filter(s -> "TRAINER".equals(s.getRole()) && 
                         (s.getStartTime().isBefore(booking.getStartTime()) || s.getStartTime().isEqual(booking.getStartTime())) &&
                         (s.getEndTime().isAfter(booking.getEndTime()) || s.getEndTime().isEqual(booking.getEndTime())))
            .findFirst()
            .orElse(null);

        if (trainerShift != null) {
            // Assign (In a real system, we'd link booking to employee, here we just log or update shift note)
            // Ideally Booking entity should have 'assignedStaffId'
            System.out.println("Assigned Trainer: " + trainerShift.getEmployeeName() + " to Booking " + booking.getId());
        } else {
            // Create Ad-hoc Shift
            Shift adhoc = createShift("Ad-hoc Trainer", "TRAINER", booking.getStartTime().toLocalDate(), booking.getStartTime().getHour(), booking.getEndTime().getHour());
            adhoc.setEmployeeName("Auto-Assigned Trainer");
            shiftRepository.save(adhoc);
            System.out.println("Created Ad-hoc Shift for Booking " + booking.getId());
        }
    }

    public List<Shift> getShifts(LocalDate start, LocalDate end) {
        return shiftRepository.findByStartTimeBetween(start.atStartOfDay(), end.atTime(23, 59, 59));
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public List<Shift> generateSchedule(LocalDate weekStart) {
        // Simple AI Logic:
        // 1. Get Peak Hours from Analytics
        // 2. Map demand to roles (High occupancy -> More Trainers/FrontDesk)
        
        List<Shift> newShifts = new ArrayList<>();
        Map<String, Object> data = analyticsService.getDashboardData();
        Map<String, Integer> peakHours = (Map<String, Integer>) data.get("peakHours"); // e.g. "18:00" -> 85

        // Iterate 7 days
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            
            // Analyze each hour block (simplified to Morning/Afternoon/Evening)
            // Morning: 08:00 - 12:00
            int morningDemand = getDemand(peakHours, "08:00", "10:00");
            createShiftsForBlock(newShifts, date, 8, 12, morningDemand);

            // Afternoon: 12:00 - 17:00
            int afternoonDemand = getDemand(peakHours, "12:00", "14:00", "16:00");
            createShiftsForBlock(newShifts, date, 12, 17, afternoonDemand);

            // Evening: 17:00 - 22:00 (Peak usually)
            int eveningDemand = getDemand(peakHours, "18:00", "20:00");
            createShiftsForBlock(newShifts, date, 17, 22, eveningDemand);
        }

        return shiftRepository.saveAll(newShifts);
    }

    private int getDemand(Map<String, Integer> peakHours, String... times) {
        int max = 0;
        for (String time : times) {
            max = Math.max(max, peakHours.getOrDefault(time, 0));
        }
        return max;
    }

    private void createShiftsForBlock(List<Shift> shifts, LocalDate date, int startH, int endH, int demand) {
        // Rules:
        // Demand > 60: 3 Trainers, 2 Front Desk
        // Demand > 30: 2 Trainers, 1 Front Desk
        // Else: 1 Trainer, 1 Front Desk
        
        int trainers = 1;
        int frontDesk = 1;

        if (demand > 60) {
            trainers = 3;
            frontDesk = 2;
        } else if (demand > 30) {
            trainers = 2;
            frontDesk = 1;
        }

        for (int i = 0; i < trainers; i++) {
            shifts.add(createShift("Trainer " + (i + 1), "TRAINER", date, startH, endH));
        }
        for (int i = 0; i < frontDesk; i++) {
            shifts.add(createShift("Staff " + (i + 1), "FRONT_DESK", date, startH, endH));
        }
    }

    private Shift createShift(String name, String role, LocalDate date, int startH, int endH) {
        Shift s = new Shift();
        s.setEmployeeName(name); // In real app, pick from available employees
        s.setRole(role);
        s.setStartTime(LocalDateTime.of(date, LocalTime.of(startH, 0)));
        s.setEndTime(LocalDateTime.of(date, LocalTime.of(endH, 0)));
        s.setStatus("SCHEDULED");
        return s;
    }
}
