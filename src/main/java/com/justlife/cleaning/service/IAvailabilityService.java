package com.justlife.cleaning.service;

import com.justlife.cleaning.domain.dto.AvailabilityDtos;
import com.justlife.cleaning.domain.dto.Interval;
import com.justlife.cleaning.entity.Professional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IAvailabilityService {

  List<AvailabilityDtos.ProfessionalAvailability> getAvailabilityForDate(LocalDate date);

  List<Professional> getActiveProfessionals();

  Map<Long, List<Interval>> getBookingIntervalsByProfessionalIdForDate(LocalDate date);

  List<AvailabilityDtos.AvailableProfessional> getAvailableProfessionalsForTimeSlot(LocalDateTime slotStart,
                                                                                   int slotDurationMinutes);
}

