package com.olegtoropoff.petcareappointment.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for representing veterinarian biography details.
 * This class is used to transfer veterinarian-related data between different layers of the application,
 * ensuring separation between the persistence and presentation layers.
 */
@Data
public class VetBiographyDto {

    /**
     * Unique identifier for the biography.
     */
    private Long id;

    /**
     * The textual content of the veterinarian's biography.
     * This field stores detailed information such as experience, education, and specialties.
     */
    private String biography;
}
