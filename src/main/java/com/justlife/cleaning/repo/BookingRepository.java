package com.justlife.cleaning.repo;

import com.justlife.cleaning.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {}
