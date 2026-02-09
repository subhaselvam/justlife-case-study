package com.justlife.cleaning.service.selection;

import com.justlife.cleaning.entity.Professional;

import java.time.LocalDateTime;
import java.util.List;

public interface ProfessionalSelectionStrategy {

  record Result(Long vehicleId, List<Professional> professionals) {}

  Result select(LocalDateTime start, int durationMinutes, int needed);
}

