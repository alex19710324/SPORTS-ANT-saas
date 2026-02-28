package com.sportsant.saas.hr.entity;

import com.sportsant.saas.entity.User;
import com.sportsant.saas.store.entity.Store;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    private String position; // MANAGER, RECEPTIONIST, TRAINER, CLEANER
    
    private Double hourlyRate;
    
    private LocalDate hireDate;
    
    private String status; // ACTIVE, ON_LEAVE, TERMINATED

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
