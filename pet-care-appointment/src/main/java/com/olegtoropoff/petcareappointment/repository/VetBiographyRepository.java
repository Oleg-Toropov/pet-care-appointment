package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.VetBiography;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing veterinarian biographies.
 * Provides methods for accessing and manipulating {@link VetBiography} entities.
 */
public interface VetBiographyRepository extends JpaRepository<VetBiography, Long> {

    /**
     * Retrieves the biography of a veterinarian by their ID.
     *
     * @param veterinarianId the ID of the veterinarian.
     * @return an {@link Optional} containing the {@link VetBiography} if found, or an empty {@link Optional} otherwise.
     */
    Optional<VetBiography> getVetBiographyByVeterinarianId(Long veterinarianId);
}
