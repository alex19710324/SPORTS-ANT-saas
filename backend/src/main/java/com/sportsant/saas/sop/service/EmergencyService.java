package com.sportsant.saas.sop.service;

import com.sportsant.saas.sop.entity.EmergencyLog;
import com.sportsant.saas.sop.entity.EmergencyPlan;
import com.sportsant.saas.sop.repository.EmergencyLogRepository;
import com.sportsant.saas.sop.repository.EmergencyPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.sportsant.saas.notification.service.NotificationCenterService;

@Service
public class EmergencyService {

    @Autowired
    private EmergencyPlanRepository emergencyPlanRepository;

    @Autowired
    private EmergencyLogRepository emergencyLogRepository;

    @Autowired
    private NotificationCenterService notificationCenterService;

    public List<EmergencyPlan> getAllPlans() {
        return emergencyPlanRepository.findAll();
    }

    public EmergencyPlan getPlan(String title) {
        return emergencyPlanRepository.findByTitle(title).orElse(null);
    }

    @Transactional
    public EmergencyLog reportIncident(String type, String description, String reporter) {
        // Find if plan exists
        Optional<EmergencyPlan> plan = emergencyPlanRepository.findByTitle(type);
        
        EmergencyLog log = new EmergencyLog();
        log.setType(type);
        log.setDescription(description);
        log.setReporter(reporter);
        if (plan.isPresent()) {
            log.setPlanId(plan.get().getId());
        }
        
        // Push notification to managers
        String title = "EMERGENCY ALERT: " + type;
        String content = "Reporter: " + reporter + ". Details: " + description;
        // In real app, we would query all admins/managers. For MVP, we send to a fixed role or user.
        notificationCenterService.sendSystemNotification("admin", title, content, "ALERT");
        
        return emergencyLogRepository.save(log);
    }

    @Transactional
    public EmergencyLog resolveIncident(Long id) {
        EmergencyLog log = emergencyLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));
        
        log.setStatus("RESOLVED");
        log.setResolvedAt(LocalDateTime.now());
        return emergencyLogRepository.save(log);
    }

    public List<EmergencyLog> getActiveIncidents() {
        return emergencyLogRepository.findByStatus("OPEN");
    }
}
