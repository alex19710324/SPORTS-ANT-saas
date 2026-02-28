package com.sportsant.saas.device.service;

import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.device.entity.Device;
import com.sportsant.saas.device.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class DeviceService implements AiAware {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public Device registerDevice(String serialNumber, String name, String type, String location) {
        Device device = new Device();
        device.setSerialNumber(serialNumber);
        device.setName(name);
        device.setType(type);
        device.setLocation(location);
        return deviceRepository.save(device);
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    /**
     * Handles heartbeat from IoT Gateway
     */
    public void updateDeviceStatus(String serialNumber, String status, Map<String, Object> params) {
        deviceRepository.findBySerialNumber(serialNumber).ifPresent(device -> {
            device.setStatus(status);
            device.setLastHeartbeat(LocalDateTime.now());
            // Mock updating params JSON
            device.setParametersJson(params.toString());
            deviceRepository.save(device);

            // Notify AI Brain if FAULT
            if ("FAULT".equals(status)) {
                eventPublisher.publishEvent(createEvent("DEVICE_FAULT", Map.of(
                    "deviceId", device.getId(),
                    "serial", device.getSerialNumber(),
                    "params", params
                )));
            }
        });
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        if ("RESTART_DEVICE".equals(suggestionType)) {
            // Mock remote restart logic
            System.out.println("AI Action: Restarting Device...");
        }
    }
}
