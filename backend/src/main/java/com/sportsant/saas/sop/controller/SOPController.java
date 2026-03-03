package com.sportsant.saas.sop.controller;

import com.sportsant.saas.sop.entity.SOPTask;
import com.sportsant.saas.sop.entity.SOPTemplate;
import com.sportsant.saas.sop.service.SOPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sop")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SOPController {

    @Autowired
    private SOPService sopService;

    @GetMapping("/tasks/my")
    @PreAuthorize("hasRole('USER') or hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public List<SOPTask> getMyTasks() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return sopService.getMyPendingTasks(username);
    }

    @PostMapping("/tasks/{id}/complete")
    @PreAuthorize("hasRole('USER') or hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public SOPTask completeTask(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String executionJson = payload.get("executionJson");
        return sopService.completeTask(id, executionJson);
    }

    @GetMapping("/templates/{role}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public List<SOPTemplate> getTemplates(@PathVariable String role) {
        return sopService.getTemplatesByRole(role);
    }

    @PostMapping("/tasks/assign")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public List<SOPTask> assignTasks(@RequestParam String role, @RequestParam String assignee) {
        return sopService.assignDailyTasks(role, assignee);
    }
}
