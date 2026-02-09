package com.justlife.cleaning.repo;

import com.justlife.cleaning.entity.Professional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessionalRepository extends JpaRepository<Professional, Long> {

  List<Professional> findByActiveTrue();
}
