package com.justlife.cleaning.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "vehicles")
public class Vehicle {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String code;

  @Column(name = "driver_name", nullable = false)
  private String driverName;

  @Column(name = "is_active", nullable = false)
  private boolean active = true;

  protected Vehicle() {}

  public Vehicle(String code, String driverName) {
    this.code = code;
    this.driverName = driverName;
  }
}
