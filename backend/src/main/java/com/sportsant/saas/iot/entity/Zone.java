package com.sportsant.saas.iot.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "iot_zones")
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String type;
    private Boolean occupied; // Can be derived from occupancy > 0
    private Integer occupancy; // peopleCount
    private Integer peopleCount; // Keep for compatibility if needed, or map occupancy to it
    private Boolean lightOn;
    private Boolean lightsOn; // Alias for lightOn
    private Long lightId; 
    private Double temperature;
    private Double humidity;
    private LocalDateTime lastUpdated;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Boolean getOccupied() { return occupancy != null && occupancy > 0; }
    public void setOccupied(Boolean occupied) { this.occupied = occupied; }
    public Integer getPeopleCount() { return occupancy; }
    public void setPeopleCount(Integer peopleCount) { this.occupancy = peopleCount; }
    public Integer getOccupancy() { return occupancy; }
    public void setOccupancy(Integer occupancy) { this.occupancy = occupancy; }
    public Boolean getLightOn() { return lightsOn; }
    public void setLightOn(Boolean lightOn) { this.lightsOn = lightOn; }
    public Boolean getLightsOn() { return lightsOn; }
    public void setLightsOn(Boolean lightsOn) { this.lightsOn = lightsOn; }
    public Long getLightId() { return lightId; }
    public void setLightId(Long lightId) { this.lightId = lightId; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
