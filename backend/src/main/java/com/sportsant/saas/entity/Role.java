package com.sportsant.saas.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private ERole name;

  private String description;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "role_permissions",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id"))
  private Set<Permission> permissions = new HashSet<>();

  public Role() {}

  public Role(ERole name) {
    this.name = name;
  }
  
  // Getters and Setters
  public Integer getId() { return id; }
  public void setId(Integer id) { this.id = id; }
  public ERole getName() { return name; }
  public void setName(ERole name) { this.name = name; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public Set<Permission> getPermissions() { return permissions; }
  public void setPermissions(Set<Permission> permissions) { this.permissions = permissions; }
}
