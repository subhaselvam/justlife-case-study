package com.justlife.cleaning.entity;

import com.justlife.cleaning.domain.dto.constant.BookingStatus;
import com.justlife.cleaning.domain.dto.constant.ProfessionalGender;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "bookings",
  indexes = {
    @Index(name = "idx_bookings_start", columnList = "start_time"),
    @Index(name = "idx_bookings_vehicle_nullable", columnList = "vehicle_id"),
    @Index(name = "idx_bookings_region", columnList = "region_id"),
    @Index(name = "idx_bookings_service", columnList = "service_id"),
    @Index(name = "idx_bookings_status", columnList = "status"),
    @Index(name = "idx_bookings_customer", columnList = "customer_id"),
    @Index(name = "idx_bookings_created_at", columnList = "created_at"),
    @Index(name = "idx_bookings_currency", columnList = "currency_code")
  }
)
public class Booking {
  @Id
  @GeneratedValue
  @Column(columnDefinition = "uuid")
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "region_id")
  private Region region;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "service_id")
  private Service service;

  @Column(name = "customer_id")
  private Long customerId;

  @Column(name = "start_time", nullable = false)
  private LocalDateTime start;

  @Column(name = "duration_minutes", nullable = false)
  private int durationMinutes;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vehicle_id")
  private Vehicle vehicle;

  @Column(name = "professional_count", nullable = false)
  @Builder.Default
  private int professionalCount = 1;

  @Column(name = "preferred_professional_gender", length = 20)
  @Enumerated(EnumType.STRING)
  private ProfessionalGender preferredProfessionalGender;

  @Column(name = "status", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private BookingStatus status = BookingStatus.CONFIRMED;

  @Column(columnDefinition = "TEXT")
  private String notes;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @Column(name = "cancelled_at")
  private LocalDateTime cancelledAt;

  @Column(name = "cancellation_reason", length = 255)
  private String cancellationReason;

  @Column(name = "currency_code", length = 3)
  private String currencyCode;

  @Column(name = "total_amount")
  private java.math.BigDecimal totalAmount;

  public LocalDateTime getEnd() { return start.plusMinutes(durationMinutes); }

  public void updateVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
  public void updateSchedule(LocalDateTime newStart, int newDurationMinutes) {
    this.start = newStart;
    this.durationMinutes = newDurationMinutes;
  }

  public void confirm() {
    this.status = BookingStatus.CONFIRMED;
  }

  public void startProgress() {
    this.status = BookingStatus.IN_PROGRESS;
  }

  public void complete() {
    this.status = BookingStatus.COMPLETED;
    this.completedAt = LocalDateTime.now();
  }

  public void cancel(String reason) {
    this.status = BookingStatus.CANCELLED;
    this.cancelledAt = LocalDateTime.now();
    this.cancellationReason = reason;
  }
}
