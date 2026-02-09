package com.justlife.cleaning.repo;

import com.justlife.cleaning.entity.BookingAssignment;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookingAssignmentRepository extends JpaRepository<BookingAssignment, Long> {

  @Query("select ba from BookingAssignment ba join fetch ba.professional p left join fetch p.vehicle v where ba.booking.id = :bookingId")
  List<BookingAssignment> findByBookingIdFetchAll(@Param("bookingId") UUID bookingId);

  @Modifying
  @Query("delete from BookingAssignment ba where ba.booking.id = :bookingId")
  void deleteByBookingId(@Param("bookingId") UUID bookingId);
}
