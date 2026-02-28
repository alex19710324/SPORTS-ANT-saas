package com.sportsant.saas.device.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
@Data
@NoArgsConstructor
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String serialNumber;

    private String name;
    private String type; // VR, GATE, TERMINAL
    private String location; // Zone A, Zone B

    @Column(nullable = false)
    private String status; // ONLINE, OFFLINE, FAULT, MAINTENANCE

    private LocalDateTime lastHeartbeat;
    private String firmwareVersion;

    @Column(columnDefinition = "TEXT")
    private String parametersJson; // {"temp": 45, "rpm": 3000}

    @PrePersist
    protected void onCreate() {
        status = "OFFLINE";
    }
}
