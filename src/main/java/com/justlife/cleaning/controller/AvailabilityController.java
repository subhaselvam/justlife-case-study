package com.justlife.cleaning.controller;

import com.justlife.cleaning.domain.dto.AvailabilityDtos;
import com.justlife.cleaning.service.IAvailabilityService;
import com.justlife.cleaning.utils.ValidationUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("v1/availability")
@Validated
@RequiredArgsConstructor
public class AvailabilityController {

  private final IAvailabilityService availabilityService;

  @GetMapping
  public List<AvailabilityDtos.ProfessionalAvailability> byDate(
      @RequestParam("date") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    ValidationUtils.validateDate(date);
    return availabilityService.getAvailabilityForDate(date);
  }

  @PostMapping
  public AvailabilityDtos.ExactSlotResponse query(@Valid @RequestBody AvailabilityDtos.ExactSlotRequest req) {
    ValidationUtils.validateTimeSlot(req.start(), req.durationMinutes());
    var available = availabilityService.getAvailableProfessionalsForTimeSlot(req.start(), req.durationMinutes());
    return new AvailabilityDtos.ExactSlotResponse(available);
  }


}
