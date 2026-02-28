package com.sportsant.saas.booking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "resources")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g. "Badminton Court 1"

    private String type; // COURT, ROOM, STUDIO

    private Double hourlyRate; // ¥50.00
    
    private Boolean active = true;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
