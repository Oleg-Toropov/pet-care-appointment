package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.VetBiography;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VetBiographyRepository extends JpaRepository<VetBiography, Long> {

    Optional<VetBiography> getVetBiographyByVeterinarianId(Long veterinarianId);
}
