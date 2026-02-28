package com.sportsant.saas.device.iot;

import com.sportsant.saas.device.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

/**
 * Mock IoT Gateway.
 * Simulates receiving MQTT messages from devices.
 */
@Component
public class MockIoTGateway {
    private static final Logger logger = LoggerFactory.getLogger(MockIoTGateway.class);

    @Autowired
    private DeviceService deviceService;

    private final Random random = new Random();

    @Scheduled(fixedRate = 10000) // Every 10 seconds
    public void simulateHeartbeats() {
        logger.debug("IoT Gateway: Processing Device Heartbeats...");
        
        // Mock Device 1 (VR Headset)
        deviceService.updateDeviceStatus("VR-001", "ONLINE", Map.of("temp", 45 + random.nextInt(5), "fps", 90));

        // Mock Device 2 (Gate) - Randomly Faulty
        String gateStatus = random.nextInt(20) == 0 ? "FAULT" : "ONLINE"; // 5% chance of fault
        deviceService.updateDeviceStatus("GATE-001", gateStatus, Map.of("pass_count", 1200, "error_code", gateStatus.equals("FAULT") ? "E01" : "NONE"));
        
        if ("FAULT".equals(gateStatus)) {
            logger.warn("IoT Gateway: Received FAULT from GATE-001");
        }
    }
}
