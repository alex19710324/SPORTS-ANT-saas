package com.sportsant.saas.maintenance.service;

import com.sportsant.saas.device.entity.Device;
import com.sportsant.saas.device.repository.DeviceRepository;
import com.sportsant.saas.maintenance.entity.MaintenanceOrder;
import com.sportsant.saas.maintenance.repository.MaintenanceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MaintenanceService {

    @Autowired
    private MaintenanceOrderRepository maintenanceOrderRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    public List<MaintenanceOrder> getPendingOrders() {
        return maintenanceOrderRepository.findByStatus("PENDING");
    }

    public List<MaintenanceOrder> getMyOrders(String technicianName) {
        return maintenanceOrderRepository.findByAssignedTo(technicianName);
    }

    @Transactional
    public MaintenanceOrder createOrder(Long deviceId, String type, String description, String priority) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        
        MaintenanceOrder order = new MaintenanceOrder();
        order.setDeviceId(deviceId);
        order.setDeviceName(device.getName());
        order.setType(type);
        order.setDescription(description);
        order.setPriority(priority);
        order.setStatus("PENDING");
        
        return maintenanceOrderRepository.save(order);
    }

    @Transactional
    public MaintenanceOrder completeOrder(Long orderId, String resolution) {
        MaintenanceOrder order = maintenanceOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus("COMPLETED");
        order.setResolution(resolution);
        order.setCompletedAt(LocalDateTime.now());
        
        // Update Device
        Device device = deviceRepository.findById(order.getDeviceId()).orElse(null);
        if (device != null) {
            device.setStatus("ONLINE");
            device.setHealthScore(100); // Reset health after maintenance
            device.setLastMaintenanceDate(LocalDateTime.now());
            // Schedule next preventive maintenance (e.g. 30 days)
            device.setNextMaintenanceDate(LocalDateTime.now().plusDays(30));
            deviceRepository.save(device);
        }
        
        return maintenanceOrderRepository.save(order);
    }

    // Mock AI Prediction
    public Map<String, Object> predictFaults(Long deviceId) {
        Map<String, Object> prediction = new HashMap<>();
        prediction.put("deviceId", deviceId);
        // Randomize for demo
        double risk = Math.random();
        if (risk > 0.7) {
            prediction.put("riskLevel", "HIGH");
            prediction.put("faultType", "Motor Overheating");
            prediction.put("probability", 0.85);
            prediction.put("daysUntilFailure", 5);
        } else {
            prediction.put("riskLevel", "LOW");
            prediction.put("faultType", "None");
        }
        return prediction;
    }
}
