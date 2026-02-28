package com.sportsant.saas.maintenance.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "devices")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g. "Treadmill X1"

    private String type; // CARDIO, STRENGTH, HVAC, POOL_PUMP

    private String serialNumber;

    private LocalDate installDate;

    private Integer maintenanceIntervalDays; // e.g. 30 days

    private String status; // ONLINE, OFFLINE, MAINTENANCE_NEEDED, ERROR

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public LocalDate getInstallDate() { return installDate; }
    public void setInstallDate(LocalDate installDate) { this.installDate = installDate; }
    public Integer getMaintenanceIntervalDays() { return maintenanceIntervalDays; }
    public void setMaintenanceIntervalDays(Integer maintenanceIntervalDays) { this.maintenanceIntervalDays = maintenanceIntervalDays; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
