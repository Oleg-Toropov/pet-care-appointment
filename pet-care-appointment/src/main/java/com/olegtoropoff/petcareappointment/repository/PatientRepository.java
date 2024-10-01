package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
