package com.sportsant.saas.sop.service;

import com.sportsant.saas.sop.entity.SOPTask;
import com.sportsant.saas.sop.entity.SOPTemplate;
import com.sportsant.saas.sop.repository.SOPTaskRepository;
import com.sportsant.saas.sop.repository.SOPTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.sportsant.saas.notification.service.NotificationCenterService;

@Service
public class SOPService {

    @Autowired
    private SOPTemplateRepository sopTemplateRepository;

    @Autowired
    private SOPTaskRepository sopTaskRepository;

    @Autowired
    private NotificationCenterService notificationCenterService;

    public List<SOPTemplate> getTemplatesByRole(String role) {
        return sopTemplateRepository.findByTargetRole(role);
    }

    @Transactional
    public List<SOPTask> assignDailyTasks(String role, String assignee) {
        List<SOPTemplate> templates = sopTemplateRepository.findByTargetRole(role);
        
        // Simple logic: Create tasks from templates
        List<SOPTask> newTasks = templates.stream().map(template -> {
            SOPTask task = new SOPTask();
            task.setTemplateId(template.getId());
            task.setTitle(template.getTitle());
            task.setAssignedTo(assignee);
            task.setDeadline(LocalDateTime.now().plusHours(8)); // Deadline in 8 hours
            return task;
        }).collect(Collectors.toList());
        
        List<SOPTask> savedTasks = sopTaskRepository.saveAll(newTasks);
        
        // Notify assignee
        if (!savedTasks.isEmpty()) {
            notificationCenterService.sendSystemNotification(
                assignee, 
                "New SOP Tasks Assigned", 
                "You have " + savedTasks.size() + " new tasks for today.", 
                "TASK"
            );
        }
        
        return savedTasks;
    }

    public List<SOPTask> getMyPendingTasks(String username) {
        return sopTaskRepository.findByAssignedToAndStatus(username, "PENDING");
    }

    @Transactional
    public SOPTask completeTask(Long taskId, String executionJson) {
        SOPTask task = sopTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        task.setStatus("COMPLETED");
        task.setExecutionJson(executionJson);
        task.setCompletedAt(LocalDateTime.now());
        return sopTaskRepository.save(task);
    }
}
