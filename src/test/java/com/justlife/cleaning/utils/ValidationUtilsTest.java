package com.justlife.cleaning.utils;

import com.justlife.cleaning.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

  @Test
  void validateTimeSlot_rejectsInvalidDuration() {
    var start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
    var ex = assertThrows(BusinessRuleViolationException.class,
        () -> ValidationUtils.validateTimeSlot(start, 60));
    assertEquals("durationMinutes must be either 120 or 240 minutes", ex.getMessage());
  }

  @Test
  void validateTimeSlot_accepts120() {
    var start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
    assertDoesNotThrow(() -> ValidationUtils.validateTimeSlot(start, 120));
  }
}

