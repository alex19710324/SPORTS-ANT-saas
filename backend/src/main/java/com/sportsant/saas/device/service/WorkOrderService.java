package com.sportsant.saas.device.service;

import com.sportsant.saas.ai.event.SystemEvent;
import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.device.entity.WorkOrder;
import com.sportsant.saas.device.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WorkOrderService implements AiAware {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public WorkOrder createWorkOrder(Long deviceId, String description, String priority, Long reportedBy) {
        WorkOrder order = new WorkOrder();
        order.setDeviceId(deviceId);
        order.setDescription(description);
        order.setPriority(priority);
        order.setReportedBy(reportedBy);
        order.setType("REPAIR");
        
        WorkOrder saved = workOrderRepository.save(order);
        
        // Notify AI for assignment suggestion
        eventPublisher.publishEvent(createEvent("WORK_ORDER_CREATED", Map.of(
            "orderId", saved.getId(),
            "deviceId", deviceId,
            "priority", priority
        )));
        
        return saved;
    }

    @EventListener
    public void onDeviceFault(SystemEvent event) {
        if ("DEVICE_FAULT".equals(event.getType())) {
            Map<String, Object> payload = event.getPayload();
            Long deviceId = (Long) payload.get("deviceId");
            
            // Auto-create work order from Fault
            createWorkOrder(deviceId, "Auto-detected Fault: " + payload.get("params"), "HIGH", 0L); // 0L = System
        }
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        if ("ASSIGN_TECHNICIAN".equals(suggestionType)) {
            Map<String, Object> data = (Map<String, Object>) payload;
            Long orderId = Long.valueOf(data.get("orderId").toString());
            Long techId = Long.valueOf(data.get("technicianId").toString());
            
            workOrderRepository.findById(orderId).ifPresent(order -> {
                order.setAssignedTo(techId);
                order.setStatus("ASSIGNED");
                workOrderRepository.save(order);
                System.out.println("AI Assigned Order #" + orderId + " to Technician " + techId);
            });
        }
    }
}
