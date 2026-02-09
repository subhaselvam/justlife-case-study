package com.justlife.cleaning.service;

import com.justlife.cleaning.domain.dto.BookingDtos;

import java.util.UUID;

public interface IBookingService {
  BookingDtos.BookingResponse create(BookingDtos.CreateBookingRequest req);

  BookingDtos.BookingResponse update(UUID bookingId, BookingDtos.UpdateBookingRequest req);

  BookingDtos.BookingResponse get(UUID bookingId);
}

