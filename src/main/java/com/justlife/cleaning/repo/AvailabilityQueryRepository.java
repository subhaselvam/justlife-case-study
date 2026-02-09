package com.justlife.cleaning.repo;

import com.justlife.cleaning.entity.Professional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AvailabilityQueryRepository extends Repository<Professional, Long> {

  interface ProfessionalCandidateRow {
    Long getProfessionalId();
    Long getVehicleId();
    String getProfessionalName();
  }

  @Query(value =
      "SELECT p.id AS professional_id, p.vehicle_id AS vehicle_id, p.full_name AS professional_name " +
      "FROM professionals p " +
      "JOIN vehicles v ON v.id = p.vehicle_id " +
      "WHERE p.is_active = true " +
      "  AND p.verification_status = 'VERIFIED' " +
      "  AND v.is_active = true " +
      "  AND NOT EXISTS ( " +
      "    SELECT 1 " +
      "    FROM booking_assignments ba " +
      "    JOIN bookings b ON b.id = ba.booking_id " +
      "    WHERE ba.professional_id = p.id " +
      "      AND NOT ( " +
      "        ((b.start_time + (b.duration_minutes || ' minutes')::interval) + (:breakMins || ' minutes')::interval) <= :startTime " +
      "        OR (b.start_time - (:breakMins || ' minutes')::interval) >= :endTime " +
      "      ) " +
      "  ) " +
      "ORDER BY p.vehicle_id, p.id",
      nativeQuery = true)
  List<ProfessionalCandidateRow> getProfessionalsForSlot(
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime,
      @Param("breakMins") int breakMins
  );

  @Query(value =
      "WITH available AS ( " +
      "  SELECT p.id AS professional_id, p.vehicle_id AS vehicle_id, p.full_name AS professional_name " +
      "  FROM professionals p " +
      "  JOIN vehicles v ON v.id = p.vehicle_id " +
      "  WHERE p.is_active = true " +
      "    AND p.verification_status = 'VERIFIED' " +
      "    AND v.is_active = true " +
      "    AND NOT EXISTS ( " +
      "      SELECT 1 " +
      "      FROM booking_assignments ba " +
      "      JOIN bookings b ON b.id = ba.booking_id " +
      "      WHERE ba.professional_id = p.id " +
      "        AND NOT ( " +
      "          ((b.start_time + (b.duration_minutes || ' minutes')::interval) + (:breakMins || ' minutes')::interval) <= :startTime " +
      "          OR (b.start_time - (:breakMins || ' minutes')::interval) >= :endTime " +
      "        ) " +
      "    ) " +
      "), chosen_vehicle AS ( " +
      "  SELECT vehicle_id " +
      "  FROM available " +
      "  GROUP BY vehicle_id " +
      "  HAVING COUNT(*) >= :needed " +
      "  ORDER BY vehicle_id " +
      "  LIMIT 1 " +
      ") " +
      "SELECT a.professional_id, a.vehicle_id, a.professional_name " +
      "FROM available a " +
      "JOIN chosen_vehicle cv ON cv.vehicle_id = a.vehicle_id " +
      "ORDER BY a.professional_id " +
      "LIMIT :needed",
      nativeQuery = true)
  List<ProfessionalCandidateRow> getProfessionalsForSlotAutoVehicle(
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime,
      @Param("breakMins") int breakMins,
      @Param("needed") int needed
  );

  interface ProfessionalBookingIntervalRow {
    Long getProfessionalId();
    String getProfessionalName();
    LocalDateTime getIntervalStart();
    LocalDateTime getIntervalEnd();
  }

  @Query(value =
      "SELECT p.id AS professional_id, " +
      "       p.full_name AS professional_name, " +
      "       (b.start_time - (:breakMins || ' minutes')::interval) AS interval_start, " +
      "       ((b.start_time + (b.duration_minutes || ' minutes')::interval) + (:breakMins || ' minutes')::interval) AS interval_end " +
      "FROM booking_assignments ba " +
      "JOIN bookings b ON b.id = ba.booking_id " +
      "JOIN professionals p ON p.id = ba.professional_id " +
      "WHERE (b.start_time::date) = :date " +
      "ORDER BY p.id, b.start_time",
      nativeQuery = true)
  List<ProfessionalBookingIntervalRow> bookingIntervalsForDate(
      @Param("date") LocalDate date,
      @Param("breakMins") int breakMins
  );
}
