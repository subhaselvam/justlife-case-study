package com.justlife.cleaning.utils;

import com.justlife.cleaning.domain.dto.BookingDtos;
import com.justlife.cleaning.entity.Booking;
import com.justlife.cleaning.domain.dto.constant.ProfessionalGender;

public final class BookingFactory {

  private BookingFactory() {}

  public static Booking fromCreateRequest(BookingDtos.CreateBookingRequest req) {
    return Booking.builder()
        .start(req.start())
        .durationMinutes(req.durationMinutes())
        .professionalCount(req.professionalCount())
        .currencyCode("AED")
        .customerId(req.customerId())
        .preferredProfessionalGender(req.preferredProfessionalGender() != null
            ? ProfessionalGender.valueOf(req.preferredProfessionalGender().toUpperCase())
            : null)
        .notes(req.notes())
        .build();
  }
}
