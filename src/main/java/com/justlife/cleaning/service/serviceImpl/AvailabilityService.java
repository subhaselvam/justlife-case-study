package com.justlife.cleaning.service.serviceImpl;

import com.justlife.cleaning.domain.dto.AvailabilityDtos;
import com.justlife.cleaning.config.SchedulingProperties;
import com.justlife.cleaning.domain.dto.Interval;
import com.justlife.cleaning.entity.Professional;
import com.justlife.cleaning.repo.ProfessionalRepository;
import com.justlife.cleaning.repo.AvailabilityQueryRepository;
import com.justlife.cleaning.service.IAvailabilityService;
import com.justlife.cleaning.utils.IntervalUtils;
import com.justlife.cleaning.utils.WorkingWindowUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.justlife.cleaning.constants.ApplicationConstants.CACHE_ACTIVE_PROFESSIONALS;

@Service
@RequiredArgsConstructor
public class AvailabilityService implements IAvailabilityService {

  private final ProfessionalRepository professionalRepository;
  private final AvailabilityQueryRepository availabilityQueryRepository;
  private final SchedulingProperties schedulingProperties;


  @Transactional(readOnly = true)
  public List<AvailabilityDtos.ProfessionalAvailability> getAvailabilityForDate(LocalDate date) {
    Interval workingHours = WorkingWindowUtils.buildWorkingWindowForDate(date);
    List<Professional> professionals = getActiveProfessionals();
    Map<Long, List<Interval>> bookingIntervalsByProfessionalId = getBookingIntervalsByProfessionalIdForDate(date);

    return professionals.stream()
        .map(professional -> mapToProfessionalAvailability(
            professional,
            workingHours,
            bookingIntervalsByProfessionalId.getOrDefault(professional.getId(), List.of())
        ))
        .sorted(Comparator
            .comparing(AvailabilityDtos.ProfessionalAvailability::vehicleId, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(AvailabilityDtos.ProfessionalAvailability::professionalId))
        .toList();
  }

  @Cacheable(cacheNames = CACHE_ACTIVE_PROFESSIONALS)
  @Transactional(readOnly = true)
  public List<Professional> getActiveProfessionals() {
    return professionalRepository.findByActiveTrue();
  }

  @Transactional(readOnly = true)
  public Map<Long, List<Interval>> getBookingIntervalsByProfessionalIdForDate(LocalDate date) {
    List<AvailabilityQueryRepository.ProfessionalBookingIntervalRow> bookingIntervalRows =
        availabilityQueryRepository.bookingIntervalsForDate(date, schedulingProperties.breakMinutes());

    Map<Long, List<Interval>> bookingIntervalsByProfessionalId = new HashMap<>();
    for (var bookingIntervalRow : bookingIntervalRows) {
      bookingIntervalsByProfessionalId
          .computeIfAbsent(bookingIntervalRow.getProfessionalId(), ignored -> new ArrayList<>())
          .add(new Interval(bookingIntervalRow.getIntervalStart(), bookingIntervalRow.getIntervalEnd()));
    }

    return bookingIntervalsByProfessionalId;
  }

  private AvailabilityDtos.ProfessionalAvailability mapToProfessionalAvailability(Professional professional,
                                                                                 Interval workdayWindow,
                                                                                 List<Interval> bookingIntervals) {
    List<Interval> mergedBookingIntervals = IntervalUtils.merge(bookingIntervals);
    List<Interval> freeIntervals = IntervalUtils.subtract(workdayWindow, mergedBookingIntervals);

    int minimumBookableDurationMinutes = schedulingProperties.minBookableMinutes();

    List<AvailabilityDtos.TimeWindow> availableTimeWindows = freeIntervals.stream()
        .filter(interval -> java.time.Duration.between(interval.start(), interval.end()).toMinutes() >= minimumBookableDurationMinutes)
        .map(interval -> new AvailabilityDtos.TimeWindow(interval.start(), interval.end()))
        .collect(Collectors.toList());

    return new AvailabilityDtos.ProfessionalAvailability(
        professional.getId(),
        professional.getFullName(),
        professional.getVehicle() != null ? professional.getVehicle().getId() : null,
        availableTimeWindows
    );
  }


  @Transactional(readOnly = true)
  public List<AvailabilityDtos.AvailableProfessional> getAvailableProfessionalsForTimeSlot(
      LocalDateTime slotStart,
      int slotDurationMinutes
  ) {

    LocalDateTime slotEndExclusive = slotStart.plusMinutes(slotDurationMinutes);
    var candidateRows = availabilityQueryRepository.getProfessionalsForSlot(
        slotStart,
        slotEndExclusive,
        schedulingProperties.breakMinutes()
    );

    return candidateRows.stream()
        .map(candidate -> new AvailabilityDtos.AvailableProfessional(
            candidate.getProfessionalId(),
            candidate.getProfessionalName(),
            candidate.getVehicleId()
        ))
        .collect(Collectors.toList());
  }
}
