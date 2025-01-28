package com.olegtoropoff.petcareappointment.service.vetbiography;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.VetBiography;
import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.repository.VetBiographyRepository;
import com.olegtoropoff.petcareappointment.repository.VeterinarianRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing veterinarian biographies.
 * Provides operations to retrieve, save, update, and delete veterinarian biographies.
 */
@Service
@RequiredArgsConstructor
public class VetBiographyService implements IVetBiographyService {
    private final VetBiographyRepository vetBiographyRepository;
    private final VeterinarianRepository veterinarianRepository;

    /**
     * Retrieves the biography of a veterinarian by their ID.
     *
     * @param vetId the ID of the veterinarian.
     * @return the {@link VetBiography} associated with the veterinarian.
     * @throws ResourceNotFoundException if the biography is not found.
     */
    @Override
    public VetBiography getVetBiographyByVetId(Long vetId) {
        return vetBiographyRepository.getVetBiographyByVeterinarianId(vetId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.VETERINARIAN_INFO_NOT_AVAILABLE));
    }

    /**
     * Saves a new biography for a veterinarian.
     *
     * @param vetBiography the {@link VetBiography} to be saved.
     * @param vetId        the ID of the veterinarian to associate the biography with.
     * @return the saved {@link VetBiography}.
     * @throws ResourceNotFoundException if the veterinarian is not found.
     */
    @Override
    public VetBiography saveVetBiography(VetBiography vetBiography, Long vetId) {
        Veterinarian veterinarian = veterinarianRepository.findById(vetId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.VETERINARIAN_NOT_FOUND));
        vetBiography.setVeterinarian(veterinarian);
        return vetBiographyRepository.save(vetBiography);
    }

    /**
     * Updates an existing biography for a veterinarian.
     *
     * @param vetBiography the updated {@link VetBiography} details.
     * @param id           the ID of the biography to update.
     * @return the updated {@link VetBiography}.
     * @throws ResourceNotFoundException if the biography is not found.
     */
    @Override
    public VetBiography updateVetBiography(VetBiography vetBiography, Long id) {
        VetBiography existingVetBiography = vetBiographyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.BIOGRAPHY_NOT_FOUND));
        existingVetBiography.setBiography(vetBiography.getBiography());
        return vetBiographyRepository.save(existingVetBiography);
        }
}
