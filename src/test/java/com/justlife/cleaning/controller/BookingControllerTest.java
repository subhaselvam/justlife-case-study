package com.justlife.cleaning.controller;

import com.justlife.cleaning.domain.dto.BookingDtos;
import com.justlife.cleaning.service.IBookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingControllerTest {

  private IBookingService bookingService;
  private BookingController bookingController;

  @BeforeEach
  void setUp() {
    bookingService = mock(IBookingService.class);
    bookingController = new BookingController(bookingService);
  }

  @Test
  void create_returnsResponse() {
    var req = new BookingDtos.CreateBookingRequest(
        LocalDateTime.of(2026, 2, 10, 10, 0),
        120,
        2,
        123L,
        "FEMALE",
        "note"
    );

    var bookingId = UUID.randomUUID();
    var expected = new BookingDtos.BookingResponse(
        bookingId,
        req.start(),
        req.start().plusMinutes(req.durationMinutes()),
        req.durationMinutes(),
        1L,
        "VAN-1",
        req.professionalCount(),
        1L,
        null,
        1L,
        null,
        req.customerId(),
        "PENDING",
        req.preferredProfessionalGender(),
        req.notes(),
        "AED",
        null,
        null,
        null,
        java.util.List.of()
    );

    when(bookingService.create(req)).thenReturn(expected);

    var resp = bookingController.create(req);

    assertNotNull(resp);
    assertNotNull(resp.getBody());
    assertEquals(bookingId, resp.getBody().bookingId());
    verify(bookingService, times(1)).create(req);
  }

  @Test
  void get_delegatesToService() {
    var id = UUID.randomUUID();

    var expected = new BookingDtos.BookingResponse(
        id,
        LocalDateTime.of(2026, 2, 10, 10, 0),
        LocalDateTime.of(2026, 2, 10, 12, 0),
        120,
        1L,
        "VAN-1",
        2,
        1L,
        null,
        1L,
        null,
        123L,
        "PENDING",
        null,
        null,
        "AED",
        null,
        null,
        null,
        java.util.List.of()
    );

    when(bookingService.get(id)).thenReturn(expected);

    var resp = bookingController.get(id);

    assertNotNull(resp.getBody());
    assertEquals(id, resp.getBody().bookingId());
    verify(bookingService, times(1)).get(id);
  }
}

