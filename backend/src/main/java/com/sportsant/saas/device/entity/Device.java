package com.sportsant.saas.device.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serialNumber;
    private String name;
    private String type; // TREADMILL, BIKE
    private String status; // ONLINE, OFFLINE, FAULT
    private String location;

    private LocalDateTime lastHeartbeat;
    
    @Column(columnDefinition = "TEXT")
    private String parametersJson; // Speed, Incline, etc.

    private Integer healthScore; // 0-100
    private LocalDateTime lastMaintenanceDate;
    private LocalDateTime nextMaintenanceDate;
    
    // New fields for Predictive Maintenance
    private Double temperature;
    private Integer usageHours;
    private String predictedFault; // "MOTOR_OVERHEAT", "belt_wear", etc.
    private Double faultProbability; // 0-1
    private Integer batteryLevel;

    @PrePersist
    protected void onCreate() {
        status = "OFFLINE";
        healthScore = 100;
    }
    
    // Getters and Setters
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Integer getUsageHours() { return usageHours; }
    public void setUsageHours(Integer usageHours) { this.usageHours = usageHours; }
    public String getPredictedFault() { return predictedFault; }
    public void setPredictedFault(String predictedFault) { this.predictedFault = predictedFault; }
    public Double getFaultProbability() { return faultProbability; }
    public void setFaultProbability(Double faultProbability) { this.faultProbability = faultProbability; }
    public Integer getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(Integer batteryLevel) { this.batteryLevel = batteryLevel; }
    
    public Integer getHealthScore() { return healthScore; }
    public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }
    public LocalDateTime getLastMaintenanceDate() { return lastMaintenanceDate; }
    public void setLastMaintenanceDate(LocalDateTime lastMaintenanceDate) { this.lastMaintenanceDate = lastMaintenanceDate; }
    public LocalDateTime getNextMaintenanceDate() { return nextMaintenanceDate; }
    public void setNextMaintenanceDate(LocalDateTime nextMaintenanceDate) { this.nextMaintenanceDate = nextMaintenanceDate; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
    public String getParametersJson() { return parametersJson; }
    public void setParametersJson(String parametersJson) { this.parametersJson = parametersJson; }
}
