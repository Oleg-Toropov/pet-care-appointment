package com.olegtoropoff.petcareappointment.service.vetbiography;

import com.olegtoropoff.petcareappointment.model.VetBiography;

/**
 * Interface for veterinarian biography management.
 * Provides methods for retrieving, saving, updating, and deleting veterinarian biographies.
 */
public interface IVetBiographyService {

    /**
     * Retrieves the biography of a veterinarian by their ID.
     *
     * @param veterinarianId the ID of the veterinarian.
     * @return the {@link VetBiography} associated with the specified veterinarian.
     */
    VetBiography getVetBiographyByVetId(Long veterinarianId);

    /**
     * Saves a new biography for a veterinarian.
     *
     * @param veterinarianBio the {@link VetBiography} to be saved.
     * @param vetId           the ID of the veterinarian to associate the biography with.
     * @return the saved {@link VetBiography}.
     */
    VetBiography saveVetBiography(VetBiography veterinarianBio, Long vetId);

    /**
     * Updates an existing biography for a veterinarian.
     *
     * @param vetBiography the updated {@link VetBiography} details.
     * @param id           the ID of the biography to update.
     * @return the updated {@link VetBiography}.
     */
    VetBiography updateVetBiography(VetBiography vetBiography, Long id);
}
