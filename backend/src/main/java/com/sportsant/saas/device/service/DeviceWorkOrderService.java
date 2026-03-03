package com.sportsant.saas.device.service;

import com.sportsant.saas.ai.event.SystemEvent;
import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.device.entity.DeviceWorkOrder;
import com.sportsant.saas.device.repository.DeviceWorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class DeviceWorkOrderService implements AiAware {

    @Autowired
    private DeviceWorkOrderRepository workOrderRepository;

    @EventListener
    public void handleAiEvent(SystemEvent event) {
        if ("DEVICE_FAILURE".equals(event.getType())) {
            // Auto-create Work Order
            DeviceWorkOrder wo = new DeviceWorkOrder();
            // wo.setDeviceId(Long.valueOf(event.getSourceId())); // Simplified
            wo.setStatus("NEW");
            wo.setDescription("AI Detected Failure: " + event.getPayload());
            workOrderRepository.save(wo);
        }
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        // Implement AI insight logic
    }
}
