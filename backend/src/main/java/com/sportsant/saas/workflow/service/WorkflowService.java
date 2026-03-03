package com.sportsant.saas.workflow.service;

import com.sportsant.saas.notification.service.NotificationCenterService;
import com.sportsant.saas.workflow.entity.WorkOrder;
import com.sportsant.saas.workflow.entity.WorkOrderLog;
import com.sportsant.saas.workflow.repository.WorkOrderLogRepository;
import com.sportsant.saas.workflow.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkflowService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private WorkOrderLogRepository logRepository;

    @Autowired
    private NotificationCenterService notificationService;

    public List<WorkOrder> getAllTickets() {
        return workOrderRepository.findAll();
    }

    public List<WorkOrder> getMyTickets(String userId) {
        return workOrderRepository.findByAssigneeId(userId);
    }

    @Transactional
    public WorkOrder createWorkOrder(WorkOrder ticket) {
        ticket.setStatus("NEW");
        // Auto-assign logic (Mock)
        if ("REPAIR".equals(ticket.getType())) {
            ticket.setAssigneeId("TECH-001");
        } else {
            ticket.setAssigneeId("ADMIN-001");
        }
        
        WorkOrder saved = workOrderRepository.save(ticket);
        
        logAction(saved.getId(), ticket.getRequesterId(), "CREATE", "Ticket created");
        
        // Notify Assignee
        Map<String, String> params = new HashMap<>();
        params.put("title", saved.getTitle());
        params.put("id", saved.getId().toString());
        notificationService.sendNotification("TICKET_NEW", saved.getAssigneeId(), params);
        
        return saved;
    }

    @Transactional
    public WorkOrder updateStatus(Long id, String status, String operatorId, String comment) {
        WorkOrder ticket = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        String oldStatus = ticket.getStatus();
        ticket.setStatus(status);
        
        if ("RESOLVED".equals(status)) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        
        WorkOrder saved = workOrderRepository.save(ticket);
        
        logAction(id, operatorId, "STATUS_CHANGE", "Changed from " + oldStatus + " to " + status + ". Comment: " + comment);
        
        return saved;
    }

    private void logAction(Long workOrderId, String operatorId, String action, String details) {
        WorkOrderLog log = new WorkOrderLog();
        log.setWorkOrderId(workOrderId);
        log.setOperatorId(operatorId);
        log.setAction(action);
        log.setDetails(details);
        logRepository.save(log);
    }
    
    public List<WorkOrderLog> getLogs(Long id) {
        return logRepository.findByWorkOrderIdOrderByCreatedAtDesc(id);
    }
}
