package com.justlife.cleaning.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.justlife.cleaning.constants.ApplicationConstants.MAX_PROFESSIONAL_COUNT;
import static com.justlife.cleaning.constants.ApplicationConstants.MIN_PROFESSIONAL_COUNT;

public class BookingDtos {

  public record CreateBookingRequest(
      @NotNull LocalDateTime start,
      @NotNull Integer durationMinutes,
      @NotNull @Min(MIN_PROFESSIONAL_COUNT) @Max(MAX_PROFESSIONAL_COUNT) Integer professionalCount,
      Long customerId,
      String preferredProfessionalGender,
      String notes
  ) {}

  public record UpdateBookingRequest(
      @NotNull LocalDateTime start,
      @NotNull Integer durationMinutes,
      String preferredProfessionalGender,
      String notes
  ) {}

  public record AssignedCleaner(Long cleanerId, String cleanerName, Long vehicleId) {}

  public record AssignedProfessional(
      Long professionalId,
      String professionalName,
      String professionalCode,
      Long vehicleId,
      String assignmentStatus
  ) {}

  public record BookingResponse(
      UUID bookingId,
      LocalDateTime start,
      LocalDateTime end,
      Integer durationMinutes,
      Long vehicleId,
      String vehicleCode,
      Integer professionalCount,
      List<AssignedCleaner> cleaners,
      Long regionId,
      String regionCode,
      Long serviceId,
      String serviceName,
      Long customerId,
      String status,
      String preferredProfessionalGender,
      String notes,
      String currencyCode,
      java.math.BigDecimal totalAmount,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      List<AssignedProfessional> professionals
  ) {}

  public record BookingStatusUpdateRequest(
      @NotNull String status,
      String reason
  ) {}
}
