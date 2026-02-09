package com.justlife.cleaning.controller;

import com.justlife.cleaning.domain.dto.AvailabilityDtos;
import com.justlife.cleaning.exception.BusinessRuleViolationException;
import com.justlife.cleaning.service.IAvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AvailabilityControllerTest {

  private IAvailabilityService availabilityService;
  private AvailabilityController availabilityController;

  @BeforeEach
  void setUp() {
    availabilityService = mock(IAvailabilityService.class);
    availabilityController = new AvailabilityController(availabilityService);
  }

  @Test
  void byDate_delegatesToService() {
    LocalDate date = LocalDate.now().plusDays(1);

    var expected = List.of(
        new AvailabilityDtos.ProfessionalAvailability(1L, "Pro 1", 10L, List.of()),
        new AvailabilityDtos.ProfessionalAvailability(2L, "Pro 2", 10L, List.of())
    );

    when(availabilityService.getAvailabilityForDate(date)).thenReturn(expected);

    var actual = availabilityController.byDate(date);

    assertEquals(expected, actual);
    verify(availabilityService, times(1)).getAvailabilityForDate(date);
  }

  @Test
  void byDate_rejectsPastDate() {
    LocalDate past = LocalDate.now().minusDays(1);

    assertThrows(BusinessRuleViolationException.class, () -> availabilityController.byDate(past));
    verifyNoInteractions(availabilityService);
  }

  @Test
  void query_returnsExactSlotResponse() {
    LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
    int durationMinutes = 120;

    var req = new AvailabilityDtos.ExactSlotRequest(start, durationMinutes);

    var available = List.of(
        new AvailabilityDtos.AvailableProfessional(1L, "Pro 1", 10L),
        new AvailabilityDtos.AvailableProfessional(2L, "Pro 2", 10L)
    );

    when(availabilityService.getAvailableProfessionalsForTimeSlot(start, durationMinutes)).thenReturn(available);

    var resp = availabilityController.query(req);

    assertNotNull(resp);
    assertEquals(2, resp.availableProfessionals().size());
    assertEquals(available, resp.availableProfessionals());
    verify(availabilityService, times(1)).getAvailableProfessionalsForTimeSlot(start, durationMinutes);
  }

  @Test
  void query_rejectsInvalidDuration() {
    LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
    var req = new AvailabilityDtos.ExactSlotRequest(start, 60);

    assertThrows(BusinessRuleViolationException.class, () -> availabilityController.query(req));
    verifyNoInteractions(availabilityService);
  }
}

