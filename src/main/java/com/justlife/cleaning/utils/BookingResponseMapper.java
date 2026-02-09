package com.justlife.cleaning.utils;

import com.justlife.cleaning.domain.dto.BookingDtos;
import com.justlife.cleaning.entity.Booking;
import com.justlife.cleaning.entity.Professional;

import java.util.List;
import java.util.stream.Collectors;

public final class BookingResponseMapper {

  private BookingResponseMapper() {}

  public static BookingDtos.BookingResponse toResponse(
      Booking booking,
      List<Professional> professionals,
      Long fixedRegionId,
      Long fixedServiceId
  ) {
    var assigned = professionals.stream()
        .map(p -> new BookingDtos.AssignedProfessional(
            p.getId(),
            p.getFullName(),
            p.getCode(),
            p.getVehicle() != null ? p.getVehicle().getId() : null,
            "ASSIGNED"
        ))
        .collect(Collectors.toList());

    return new BookingDtos.BookingResponse(
        booking.getId(),
        booking.getStart(),
        booking.getEnd(),
        booking.getDurationMinutes(),
        booking.getVehicle() != null ? booking.getVehicle().getId() : null,
        booking.getVehicle() != null ? booking.getVehicle().getCode() : null,
        booking.getProfessionalCount(),
        fixedRegionId,
        "1",
        fixedServiceId,
        "CLEANING",
        booking.getCustomerId(),
        booking.getStatus().name(),
        booking.getPreferredProfessionalGender() != null ? booking.getPreferredProfessionalGender().name() : null,
        booking.getNotes(),
        booking.getCurrencyCode(),
        booking.getTotalAmount(),
        booking.getCreatedAt(),
        booking.getUpdatedAt(),
        assigned
    );
  }
}

