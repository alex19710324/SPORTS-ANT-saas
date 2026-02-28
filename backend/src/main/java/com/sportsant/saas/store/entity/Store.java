package com.sportsant.saas.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "stores")
@Data
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
}
