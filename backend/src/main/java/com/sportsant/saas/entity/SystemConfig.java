package com.sportsant.saas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "system_configs")
public class SystemConfig {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String configKey;

  private String configValue;

  private String description;

  public SystemConfig() {}

  public SystemConfig(String configKey, String configValue, String description) {
    this.configKey = configKey;
    this.configValue = configValue;
    this.description = description;
  }

  // Getters and Setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getConfigKey() { return configKey; }
  public void setConfigKey(String configKey) { this.configKey = configKey; }
  public String getConfigValue() { return configValue; }
  public void setConfigValue(String configValue) { this.configValue = configValue; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
}
