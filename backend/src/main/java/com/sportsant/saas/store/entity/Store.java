package com.sportsant.saas.store.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "stores")
@NoArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String country;
    private String city;
    private Double latitude;
    private Double longitude;

    // Real-time cached metrics (updated by async jobs)
    private BigDecimal todayRevenue;
    private Integer todayVisitors;
    private Integer alertCount;
    private String status; // NORMAL, WARNING, CLOSED

    @PrePersist
    protected void onCreate() {
        todayRevenue = BigDecimal.ZERO;
        todayVisitors = 0;
        alertCount = 0;
        status = "NORMAL";
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public BigDecimal getTodayRevenue() { return todayRevenue; }
    public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }
    public Integer getTodayVisitors() { return todayVisitors; }
    public void setTodayVisitors(Integer todayVisitors) { this.todayVisitors = todayVisitors; }
    public Integer getAlertCount() { return alertCount; }
    public void setAlertCount(Integer alertCount) { this.alertCount = alertCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
