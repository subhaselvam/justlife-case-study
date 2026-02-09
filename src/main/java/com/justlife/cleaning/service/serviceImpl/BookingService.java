package com.justlife.cleaning.service.serviceImpl;

import com.justlife.cleaning.domain.dto.BookingDtos;
import com.justlife.cleaning.domain.dto.constant.ProfessionalGender;
import com.justlife.cleaning.entity.Booking;
import com.justlife.cleaning.entity.BookingAssignment;
import com.justlife.cleaning.entity.Professional;
import com.justlife.cleaning.entity.Vehicle;
import com.justlife.cleaning.exception.NotFoundException;
import com.justlife.cleaning.repo.BookingAssignmentRepository;
import com.justlife.cleaning.repo.BookingRepository;
import com.justlife.cleaning.repo.VehicleRepository;
import com.justlife.cleaning.service.IBookingService;
import com.justlife.cleaning.service.selection.ProfessionalSelectionStrategy;
import com.justlife.cleaning.utils.BookingFactory;
import com.justlife.cleaning.utils.BookingResponseMapper;
import com.justlife.cleaning.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.justlife.cleaning.constants.ApplicationConstants.DEFAULT_REGION_ID;
import static com.justlife.cleaning.constants.ApplicationConstants.DEFAULT_SERVICE_ID;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {

  private final BookingRepository bookingRepository;
  private final BookingAssignmentRepository assignmentRepository;
  private final VehicleRepository vehicleRepository;
  private final ProfessionalSelectionStrategy professionalSelectionStrategy;


  @Transactional
  public BookingDtos.BookingResponse create(BookingDtos.CreateBookingRequest req) {
    // Validate the booking request before processing
    ValidationUtils.validateCreateBookingRequest(req);

    var sel = professionalSelectionStrategy.select(req.start(), req.durationMinutes(), req.professionalCount());
    Vehicle vehicle = vehicleRepository.findById(sel.vehicleId())
        .orElseThrow(() -> new NotFoundException("Vehicle not found: " + sel.vehicleId()));

    Booking booking = BookingFactory.fromCreateRequest(req);
    booking.updateVehicle(vehicle);

    booking = bookingRepository.save(booking);

    for (Professional professional : sel.professionals()) {
      assignmentRepository.save(new BookingAssignment(booking, professional));
    }

    return BookingResponseMapper.toResponse(booking, sel.professionals(), DEFAULT_REGION_ID, DEFAULT_SERVICE_ID);
  }

  @Transactional
  public BookingDtos.BookingResponse update(UUID bookingId, BookingDtos.UpdateBookingRequest req) {
    // Validate the update request before processing
    ValidationUtils.validateTimeSlot(req.start(), req.durationMinutes());

    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
    int needed = booking.getProfessionalCount();
    assignmentRepository.deleteByBookingId(bookingId);
    var sel = professionalSelectionStrategy.select(req.start(), req.durationMinutes(), needed);
    Vehicle vehicle = vehicleRepository.findById(sel.vehicleId())
        .orElseThrow(() -> new NotFoundException("Vehicle not found: " + sel.vehicleId()));

    booking.updateVehicle(vehicle);
    booking.updateSchedule(req.start(), req.durationMinutes());

    if (req.preferredProfessionalGender() != null) {
      booking.setPreferredProfessionalGender(
          ProfessionalGender.valueOf(req.preferredProfessionalGender().toUpperCase()));
    }

    if (req.notes() != null) {
      booking.setNotes(req.notes());
    }
    bookingRepository.save(booking);
    for (Professional professional : sel.professionals()) {
      assignmentRepository.save(new BookingAssignment(booking, professional));
    }
    return BookingResponseMapper.toResponse(booking, sel.professionals(), DEFAULT_REGION_ID, DEFAULT_SERVICE_ID);
  }

  @Transactional(readOnly = true)
  public BookingDtos.BookingResponse get(UUID bookingId) {
    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

    List<Professional> professionals = assignmentRepository.findByBookingIdFetchAll(bookingId)
        .stream()
        .map(BookingAssignment::getProfessional)
        .collect(Collectors.toList());

    return BookingResponseMapper.toResponse(booking, professionals, DEFAULT_REGION_ID, DEFAULT_SERVICE_ID);
  }
}
