package com.olegtoropoff.petcareappointment.service.vetbiography;

import com.olegtoropoff.petcareappointment.dto.EntityConverter;
import com.olegtoropoff.petcareappointment.dto.VetBiographyDto;
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
    private final EntityConverter<VetBiography, VetBiographyDto> entityConverter;

    /**
     * Retrieves the biography of a veterinarian by their ID.
     *
     * @param vetId the ID of the veterinarian.
     * @return the {@link VetBiographyDto} associated with the veterinarian.
     * @throws ResourceNotFoundException if the biography is not found.
     */
    @Override
    public VetBiographyDto getVetBiographyByVetId(Long vetId) {
        VetBiography vetBiography = vetBiographyRepository.getVetBiographyByVeterinarianId(vetId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.VETERINARIAN_INFO_NOT_AVAILABLE));
        return entityConverter.mapEntityToDto(vetBiography, VetBiographyDto.class);
    }

    /**
     * Saves a new biography for a veterinarian.
     *
     * @param vetBiography the {@link VetBiography} to be saved.
     * @param vetId        the ID of the veterinarian to associate the biography with.
     * @return the saved {@link VetBiographyDto}.
     * @throws ResourceNotFoundException if the veterinarian is not found.
     */
    @Override
    public VetBiographyDto saveVetBiography(VetBiography vetBiography, Long vetId) {
        Veterinarian veterinarian = veterinarianRepository.findById(vetId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.VETERINARIAN_NOT_FOUND));
        vetBiography.setVeterinarian(veterinarian);
        VetBiography savedVetBiography =  vetBiographyRepository.save(vetBiography);
        return entityConverter.mapEntityToDto(savedVetBiography, VetBiographyDto.class);
    }

    /**
     * Updates an existing biography for a veterinarian.
     *
     * @param vetBiography the updated {@link VetBiography} details.
     * @param id           the ID of the biography to update.
     * @return the updated {@link VetBiographyDto}.
     * @throws ResourceNotFoundException if the biography is not found.
     */
    @Override
    public VetBiographyDto updateVetBiography(VetBiography vetBiography, Long id) {
        VetBiography existingVetBiography = vetBiographyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.BIOGRAPHY_NOT_FOUND));
        existingVetBiography.setBiography(vetBiography.getBiography());
        VetBiography updatedVetBiography =  vetBiographyRepository.save(existingVetBiography);
        return entityConverter.mapEntityToDto(updatedVetBiography, VetBiographyDto.class);
        }
}
