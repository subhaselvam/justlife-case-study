package com.justlife.cleaning.utils;

import com.justlife.cleaning.domain.dto.BookingDtos;
import com.justlife.cleaning.exception.BusinessRuleViolationException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.justlife.cleaning.constants.ApplicationConstants.*;


public final class ValidationUtils {

  private ValidationUtils() {}

  public static void validateDate(LocalDate date) {
    if (date == null) {
      throw new BusinessRuleViolationException("DATE_REQUIRED", ERROR_DATE_REQUIRED, "date");
    }

    LocalDate today = LocalDate.now();
    if (date.isBefore(today)) {
      throw new BusinessRuleViolationException("DATE_IN_PAST", ERROR_DATE_IN_PAST, "date");
    }

    if (date.getDayOfWeek() == DEFAULT_OFF_DAY) {
      throw new BusinessRuleViolationException("OFF_DAY_NOT_ALLOWED", ERROR_OFF_DAY + DEFAULT_OFF_DAY + "s", "date");
    }
  }

  public static void validateTimeSlot(LocalDateTime slotStart, Integer slotDurationMinutes) {
    if (slotStart == null) {
      throw new BusinessRuleViolationException("START_TIME_REQUIRED", ERROR_START_REQUIRED, "start");
    }
    if (slotDurationMinutes == null) {
      throw new BusinessRuleViolationException("DURATION_REQUIRED", ERROR_DURATION_REQUIRED, "durationMinutes");
    }

    // Only 2-hour or 4-hour bookings are allowed.
    if (slotDurationMinutes != MIN_BOOKING_DURATION_MINUTES && slotDurationMinutes != MAX_BOOKING_DURATION_MINUTES) {
      throw new BusinessRuleViolationException("INVALID_DURATION", ERROR_DURATION_INVALID, "durationMinutes");
    }

    LocalDateTime now = LocalDateTime.now();
    if (slotStart.isBefore(now)) {
      throw new BusinessRuleViolationException("START_TIME_IN_PAST", ERROR_START_IN_PAST, "start");
    }

    LocalDate date = slotStart.toLocalDate();
    validateDate(date);

    LocalDateTime slotEndExclusive = slotStart.plusMinutes(slotDurationMinutes);
    if (!slotStart.toLocalDate().equals(slotEndExclusive.toLocalDate())) {
      throw new BusinessRuleViolationException("BOOKING_SPANS_MULTIPLE_DAYS", ERROR_BOOKING_SINGLE_DAY, "start");
    }

    LocalDateTime workdayStart = LocalDateTime.of(date, DEFAULT_WORKING_START);
    LocalDateTime workdayEnd = LocalDateTime.of(date, DEFAULT_WORKING_END);

    if (slotStart.isBefore(workdayStart)) {
      throw new BusinessRuleViolationException("START_BEFORE_WORKING_HOURS", ERROR_START_BEFORE_WORKING_HOURS + DEFAULT_WORKING_START, "start");
    }

    if (slotEndExclusive.isAfter(workdayEnd)) {
      throw new BusinessRuleViolationException("END_AFTER_WORKING_HOURS", ERROR_END_AFTER_WORKING_HOURS + DEFAULT_WORKING_END, "start");
    }
  }

  public static void validateCreateBookingRequest(BookingDtos.CreateBookingRequest req) {
    validateTimeSlot(req.start(), req.durationMinutes());
  }
}
