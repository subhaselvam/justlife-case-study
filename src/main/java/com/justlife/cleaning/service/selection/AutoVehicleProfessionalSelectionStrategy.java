package com.justlife.cleaning.service.selection;

import com.justlife.cleaning.config.SchedulingProperties;
import com.justlife.cleaning.entity.Professional;
import com.justlife.cleaning.exception.BusinessRuleViolationException;
import com.justlife.cleaning.repo.AvailabilityQueryRepository;
import com.justlife.cleaning.repo.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AutoVehicleProfessionalSelectionStrategy implements ProfessionalSelectionStrategy {

  private final AvailabilityQueryRepository availabilityQueryRepository;
  private final ProfessionalRepository professionalRepository;
  private final SchedulingProperties rules;


  @Override
  public Result select(LocalDateTime start, int durationMinutes, int needed) {
    LocalDateTime slotEnd = start.plusMinutes(durationMinutes);

    List<AvailabilityQueryRepository.ProfessionalCandidateRow> chosenRows =
        availabilityQueryRepository.getProfessionalsForSlotAutoVehicle(
            start,
            slotEnd,
            rules.breakMinutes(),
            needed
        );

    if (chosenRows.isEmpty()) {
      throw new BusinessRuleViolationException(
          "No vehicle has " + needed + " available professionals for the requested slot.");
    }

    Long vehicleId = chosenRows.getFirst().getVehicleId();

    List<Long> professionalIds = chosenRows.stream()
        .map(AvailabilityQueryRepository.ProfessionalCandidateRow::getProfessionalId)
        .toList();

    List<Professional> chosenProfessionals = professionalRepository.findAllById(professionalIds);
    if (chosenProfessionals.size() != needed) {
      throw new BusinessRuleViolationException("Professionals became unavailable. Please retry.");
    }

    return new Result(vehicleId, chosenProfessionals);
  }
}

