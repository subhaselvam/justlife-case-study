package com.justlife.cleaning.utils;


import com.justlife.cleaning.constants.ApplicationConstants;
import com.justlife.cleaning.domain.dto.Interval;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class WorkingWindowUtils {

  private WorkingWindowUtils() {}

  public static Interval buildWorkingWindowForDate(LocalDate date) {
    LocalDateTime start = LocalDateTime.of(date, ApplicationConstants.DEFAULT_WORKING_START);
    LocalDateTime end = LocalDateTime.of(date, ApplicationConstants.DEFAULT_WORKING_END);
    return new Interval(start, end);
  }
}
