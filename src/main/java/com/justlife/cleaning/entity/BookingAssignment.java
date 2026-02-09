package com.justlife.cleaning.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.justlife.cleaning.domain.dto.constant.AssignmentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "booking_assignments",
  uniqueConstraints = {
    @UniqueConstraint(name = "uq_booking_professional", columnNames = {"booking_id","professional_id"})
  },
  indexes = {
    @Index(name = "idx_assignment_booking", columnList = "booking_id"),
    @Index(name = "idx_booking_assignments_professional", columnList = "professional_id"),
    @Index(name = "idx_booking_assignments_status", columnList = "assignment_status")
  }
)
public class BookingAssignment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "booking_id", nullable = false)
  private Booking booking;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "professional_id", nullable = false)
  private Professional professional;

  @Column(name = "assignment_status", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private AssignmentStatus assignmentStatus = AssignmentStatus.ASSIGNED;

  @CreatedDate
  @JsonProperty("assignedAt")
  @Column(name = "assigned_at", nullable = false, updatable = false)
  private LocalDateTime assignedAt;

  @LastModifiedDate
  @JsonProperty("updatedAt")
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @JsonProperty("acceptedAt")
  @Column(name = "accepted_at")
  private LocalDateTime acceptedAt;

  @JsonProperty("rejectedAt")
  @Column(name = "rejected_at")
  private LocalDateTime rejectedAt;

  @JsonProperty("rejectionReason")
  @Column(name = "rejection_reason", length = 255)
  private String rejectionReason;

  protected BookingAssignment() {}

  public BookingAssignment(Booking booking, Professional professional) {
    this.booking = booking;
    this.professional = professional;
  }

  public void accept() {
    this.assignmentStatus = AssignmentStatus.ACCEPTED;
    this.acceptedAt = LocalDateTime.now();
  }

  public void reject(String reason) {
    this.assignmentStatus = AssignmentStatus.REJECTED;
    this.rejectedAt = LocalDateTime.now();
    this.rejectionReason = reason;
  }

  public void complete() {
    this.assignmentStatus = AssignmentStatus.COMPLETED;
  }
}
