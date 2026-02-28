package com.sportsant.saas.iot.service;

import com.sportsant.saas.ai.service.AiBrainService;
import com.sportsant.saas.iot.entity.Zone;
import com.sportsant.saas.iot.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class SmartVenueService {

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private AiBrainService aiBrainService;

    // Simulate IoT Data Ingestion (Sensors sending data)
    @Scheduled(fixedRate = 10000) // Every 10s
    @Transactional
    public void simulateSensorData() {
        List<Zone> zones = zoneRepository.findAll();
        Random rand = new Random();

        if (zones.isEmpty()) {
            // Seed initial zones if empty
            createZone("Court 1", "COURT");
            createZone("Court 2", "COURT");
            createZone("Gym Area", "GYM");
            createZone("Pool", "POOL");
            return;
        }

        for (Zone zone : zones) {
            // Simulate random fluctuations (small changes)
            double tempChange = (rand.nextDouble() - 0.5); // +/- 0.5
            double currentTemp = zone.getTemperature() == null ? 22.0 : zone.getTemperature();
            zone.setTemperature(Math.max(18.0, Math.min(30.0, currentTemp + tempChange)));

            zone.setHumidity(40.0 + rand.nextDouble() * 20);   // 40-60%
            
            // Randomly change occupancy sometimes
            if (rand.nextDouble() > 0.8) {
                 zone.setOccupancy(rand.nextInt(10));
            }
            
            zone.setLastUpdated(LocalDateTime.now());
            
            // AI Automation Logic
            optimizeZone(zone);
            
            zoneRepository.save(zone);
        }
    }

    private void createZone(String name, String type) {
        Zone z = new Zone();
        z.setName(name);
        z.setType(type);
        z.setTemperature(22.0);
        z.setHumidity(50.0);
        z.setLightsOn(false);
        z.setOccupancy(0);
        z.setLastUpdated(LocalDateTime.now());
        zoneRepository.save(z);
    }

    private void optimizeZone(Zone zone) {
        // Rule 1: Auto Lights Off if empty
        if (zone.getOccupancy() == 0 && Boolean.TRUE.equals(zone.getLightsOn())) {
            zone.setLightsOn(false);
            aiBrainService.proposeSuggestion(
                "Energy Saver: " + zone.getName(),
                "Turned OFF lights in " + zone.getName() + " due to zero occupancy.",
                "ENERGY",
                "LOW",
                null // Auto-executed
            );
        }

        // Rule 2: Auto Lights On if occupied
        if (zone.getOccupancy() > 0 && Boolean.FALSE.equals(zone.getLightsOn())) {
            zone.setLightsOn(true);
            // Log this action internally
        }

        // Rule 3: High Temp Alert
        if (zone.getTemperature() > 28.0) {
             aiBrainService.proposeSuggestion(
                "Comfort Alert: " + zone.getName(),
                "Temperature is high (" + String.format("%.1f", zone.getTemperature()) + "°C). Suggest lowering HVAC setpoint.",
                "COMFORT",
                "MEDIUM",
                "/api/iot/hvac/cool/" + zone.getId()
            );
        }
    }

    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }
}
