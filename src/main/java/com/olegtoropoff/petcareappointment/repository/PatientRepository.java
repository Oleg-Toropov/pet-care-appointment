package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Patient} entities.
 * Provides standard CRUD operations and supports JPA query methods.
 */
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
