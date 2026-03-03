package com.sportsant.saas.sop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sop_tasks")
public class SOPTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long templateId;

    private String title; // Snapshot of template title

    private String assignedTo; // Username or Role

    private String status; // PENDING, COMPLETED, SKIPPED

    private LocalDateTime deadline;
    private LocalDateTime completedAt;

    @Column(columnDefinition = "TEXT")
    private String executionJson; // [{"item": "Check lights", "checked": true, "note": "All good"}]

    @PrePersist
    protected void onCreate() {
        if (status == null) status = "PENDING";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public String getExecutionJson() { return executionJson; }
    public void setExecutionJson(String executionJson) { this.executionJson = executionJson; }
}
