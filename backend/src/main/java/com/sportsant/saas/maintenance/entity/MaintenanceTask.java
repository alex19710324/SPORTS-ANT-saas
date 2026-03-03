package com.sportsant.saas.maintenance.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance_tasks")
public class MaintenanceTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private MaintenanceDevice device;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate dueDate;

    private String status; // PENDING, IN_PROGRESS, COMPLETED

    private String technicianName; // Assigned to

    private LocalDate completedDate;
    
    private Double cost; // Parts + Labor

    // Getters and Setters
    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public MaintenanceDevice getDevice() { return device; }
    public void setDevice(MaintenanceDevice device) { this.device = device; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTechnicianName() { return technicianName; }
    public void setTechnicianName(String technicianName) { this.technicianName = technicianName; }
    public LocalDate getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }
}
