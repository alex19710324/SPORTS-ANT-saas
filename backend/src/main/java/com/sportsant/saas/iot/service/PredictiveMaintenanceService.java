package com.sportsant.saas.iot.service;

import com.sportsant.saas.device.entity.Device;
import com.sportsant.saas.device.repository.DeviceRepository;
import com.sportsant.saas.workflow.entity.WorkOrder;
import com.sportsant.saas.workflow.repository.WorkOrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class PredictiveMaintenanceService {

    private final DeviceRepository deviceRepository;
    private final WorkOrderRepository workOrderRepository;
    private final Random random = new Random();

    public PredictiveMaintenanceService(DeviceRepository deviceRepository, WorkOrderRepository workOrderRepository) {
        this.deviceRepository = deviceRepository;
        this.workOrderRepository = workOrderRepository;
    }

    /**
     * 模拟设备遥测数据并预测健康状况
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void analyzeDeviceHealth() {
        List<Device> devices = deviceRepository.findAll();
        
        for (Device device : devices) {
            updateMockTelemetry(device);
            predictFailure(device);
            if (shouldTriggerMaintenance(device)) {
                createMaintenanceOrder(device);
            }
            deviceRepository.save(device);
        }
    }

    private void updateMockTelemetry(Device device) {
        double currentTemp = device.getTemperature() != null ? device.getTemperature() : 40.0;
        double newTemp = currentTemp + (random.nextDouble() * 2 - 1);
        device.setTemperature(newTemp);
        
        if ("ONLINE".equals(device.getStatus())) {
            int hours = device.getUsageHours() != null ? device.getUsageHours() : 0;
            device.setUsageHours(hours + 1);
        }
    }

    private void predictFailure(Device device) {
        int healthScore = 100;
        String potentialFault = null;
        
        if (device.getTemperature() != null && device.getTemperature() > 80.0) {
            healthScore -= 40;
            potentialFault = "OVERHEAT";
        } else if (device.getTemperature() != null && device.getTemperature() > 60.0) {
            healthScore -= 15;
        }
        
        if (device.getUsageHours() != null && device.getUsageHours() > 5000) {
            healthScore -= 30;
            if (potentialFault == null) potentialFault = "WEAR_TEAR";
        }
        
        if (random.nextDouble() < 0.05) {
            healthScore -= 10;
        }

        device.setHealthScore(Math.max(0, healthScore));
        if (healthScore < 60) {
            device.setPredictedFault(potentialFault != null ? potentialFault : "UNKNOWN_ANOMALY");
            device.setFaultProbability(random.nextDouble() * 0.5 + 0.5);
        } else {
            device.setPredictedFault(null);
            device.setFaultProbability(0.0);
        }
    }

    private boolean shouldTriggerMaintenance(Device device) {
        return device.getHealthScore() != null && device.getHealthScore() < 50 && !"MAINTENANCE".equals(device.getStatus());
    }

    private void createMaintenanceOrder(Device device) {
        List<WorkOrder> existingOrders = workOrderRepository.findBySourceTypeAndSourceIdAndStatusIn(
            "DEVICE", String.valueOf(device.getId()), List.of("PENDING", "IN_PROGRESS")
        );
        
        if (!existingOrders.isEmpty()) {
            return;
        }

        WorkOrder order = new WorkOrder();
        order.setTitle("系统自动生成: " + device.getName() + " 预防性维护");
        order.setDescription("检测到设备健康分过低 (" + device.getHealthScore() + 
                           ")。预测故障: " + device.getPredictedFault() + 
                           "。当前温度: " + String.format("%.1f", device.getTemperature()));
        order.setType("MAINTENANCE");
        order.setPriority("HIGH");
        order.setStatus("PENDING");
        order.setSourceType("DEVICE");
        order.setSourceId(String.valueOf(device.getId()));
        order.setRequesterId("AI_SYSTEM");
        order.setCreatedAt(LocalDateTime.now());
        
        workOrderRepository.save(order);
        
        System.out.println("自动生成维修工单: " + order.getTitle());
    }
    
    public Device getDeviceStatus(Long id) {
        return deviceRepository.findById(id).orElse(null);
    }
    
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }
}
