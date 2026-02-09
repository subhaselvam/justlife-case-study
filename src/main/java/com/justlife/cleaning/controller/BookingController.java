package com.justlife.cleaning.controller;

import com.justlife.cleaning.domain.dto.BookingDtos;
import com.justlife.cleaning.service.IBookingService;
import com.justlife.cleaning.utils.ValidationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("v1/bookings/")
@RequiredArgsConstructor
public class BookingController {

  private final IBookingService bookingService;

  @PostMapping
  public ResponseEntity<BookingDtos.BookingResponse> create(@Valid @RequestBody BookingDtos.CreateBookingRequest req) {
    ValidationUtils.validateCreateBookingRequest(req);
    return ResponseEntity.ok(bookingService.create(req));
  }

  @PutMapping("id/{id}")
  public ResponseEntity<BookingDtos.BookingResponse> update(@PathVariable("id") UUID id,
                                                           @Valid @RequestBody BookingDtos.UpdateBookingRequest req) {
    ValidationUtils.validateTimeSlot(req.start(),req.durationMinutes());
    return ResponseEntity.ok(bookingService.update(id, req));
  }

  @GetMapping("id/{id}")
  public ResponseEntity<BookingDtos.BookingResponse> get(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(bookingService.get(id));
  }
}
