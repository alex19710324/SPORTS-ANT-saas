package com.sportsant.saas.communication.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long recipientId; // Member ID or Staff ID (or 0 for Broadcast)

    @Column(nullable = false)
    private String title;

    private String message;

    @Column(nullable = false)
    private String type; // SYSTEM, MARKETING, ALERT, BOOKING

    private String link; // Actionable link

    private String roleTarget; // e.g. "ADMIN", "TECHNICIAN"

    private Boolean isRead = false;

    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        isRead = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }
    public void setRecipient(com.sportsant.saas.entity.User user) { this.recipientId = user.getId(); }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public String getRoleTarget() { return roleTarget; }
    public void setRoleTarget(String roleTarget) { this.roleTarget = roleTarget; }
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
