package com.justlife.cleaning.domain.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AvailabilityDtos {

  public record TimeWindow(LocalDateTime start, LocalDateTime end) {}

  public record ProfessionalAvailability(Long professionalId, String professionalName, Long vehicleId, List<TimeWindow> availableTimes) {}

  public record ExactSlotRequest(@NotNull LocalDateTime start, @NotNull Integer durationMinutes) {}

  public record AvailableProfessional(Long professionalId, String professionalName, Long vehicleId) {}

  public record ExactSlotResponse(List<AvailableProfessional> availableProfessionals) {}

  public record DateRequest(@NotNull LocalDate date) {}
}
