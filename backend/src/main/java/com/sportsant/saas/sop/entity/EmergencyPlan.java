package com.sportsant.saas.sop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_plans")
public class EmergencyPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // "Fire Evacuation"

    @Column(columnDefinition = "TEXT")
    private String procedureSteps; // JSON or HTML

    private String emergencyContact; // "Call 119"

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getProcedureSteps() { return procedureSteps; }
    public void setProcedureSteps(String procedureSteps) { this.procedureSteps = procedureSteps; }
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
}
