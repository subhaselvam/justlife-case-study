package com.justlife.cleaning.constants;

import java.time.DayOfWeek;
import java.time.LocalTime;

public final class ApplicationConstants {

    private ApplicationConstants() {
    }

    public static final LocalTime DEFAULT_WORKING_START = LocalTime.of(8, 0);
    public static final LocalTime DEFAULT_WORKING_END = LocalTime.of(22, 0);
    public static final DayOfWeek DEFAULT_OFF_DAY = DayOfWeek.FRIDAY;
    public static final int MIN_BOOKING_DURATION_MINUTES = 120;
    public static final int MAX_BOOKING_DURATION_MINUTES = 240;
    public static final int DEFAULT_BREAK_MINUTES = 30;
    public static final int MIN_PROFESSIONAL_COUNT = 1;
    public static final int MAX_PROFESSIONAL_COUNT = 3;
    public static final Long DEFAULT_REGION_ID = 1L;
    public static final Long DEFAULT_SERVICE_ID = 1L;
    public static final int PROFESSIONALS_PER_VEHICLE = 5;
    public static final int TOTAL_VEHICLES = 5;
    public static final String ERROR_DATE_REQUIRED = "date is required";
    public static final String ERROR_DATE_IN_PAST = "date cannot be in the past";
    public static final String ERROR_START_REQUIRED = "start is required";
    public static final String ERROR_DURATION_REQUIRED = "durationMinutes is required";
    public static final String ERROR_DURATION_INVALID = "durationMinutes must be either 120 or 240 minutes";
    public static final String ERROR_START_IN_PAST = "start cannot be in the past";
    public static final String ERROR_BOOKING_SINGLE_DAY = "booking must be within a single local day";
    public static final String ERROR_START_BEFORE_WORKING_HOURS = "first appointment cannot start before ";
    public static final String ERROR_END_AFTER_WORKING_HOURS = "last appointment must finish before ";
    public static final String ERROR_OFF_DAY = "professionals do not work on ";
    public static final String CACHE_ACTIVE_PROFESSIONALS = "activeProfessionals";
    public static final String API_BASE_PATH = "/v1";
    public static final String API_AVAILABILITY_PATH = "/availability";
    public static final String API_BOOKINGS_PATH = "/bookings";
}

