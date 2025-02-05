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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final CacheManager cacheManager;

    /**
     * Retrieves the biography of a veterinarian by their ID.
     * <p>
     * This method fetches the biography from the database and converts it to a {@link VetBiographyDto}.
     * The result is cached under the `veterinarian_biography` cache to improve performance.
     *
     * <b>Cache Usage:</b>
     * <ul>
     *     <li>If the biography is available in the cache, it is returned without querying the database.</li>
     *     <li>If not cached, the biography is retrieved from the database and then cached.</li>
     * </ul>
     *
     * @param vetId the ID of the veterinarian.
     * @return the {@link VetBiographyDto} associated with the veterinarian.
     * @throws ResourceNotFoundException if the biography is not found.
     */
    @Cacheable(value = "veterinarian_biography", key = "#vetId")
    @Override
    public VetBiographyDto getVetBiographyByVetId(Long vetId) {
        VetBiography vetBiography = vetBiographyRepository.getVetBiographyByVeterinarianId(vetId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.VETERINARIAN_INFO_NOT_AVAILABLE));
        return entityConverter.mapEntityToDto(vetBiography, VetBiographyDto.class);
    }

    /**
     * Saves a new biography for a veterinarian.
     * <p>
     * This method associates a new biography with a veterinarian and persists it in the database.
     * If a biography already exists, it should be updated instead of creating a duplicate.
     *
     * <b>Cache Eviction:</b>
     * <ul>
     *     <li>Removes the veterinarian's biography from the cache after saving, ensuring fresh data on the next retrieval.</li>
     * </ul>
     *
     * @param vetBiography the {@link VetBiography} to be saved.
     * @param vetId        the ID of the veterinarian to associate the biography with.
     * @return the saved {@link VetBiographyDto}.
     * @throws ResourceNotFoundException if the veterinarian is not found.
     */
    @CacheEvict(value = "veterinarian_biography", key = "#vetId")
    @Override
    public VetBiographyDto saveVetBiography(VetBiography vetBiography, Long vetId) {
        Veterinarian veterinarian = veterinarianRepository.findById(vetId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.VETERINARIAN_NOT_FOUND));
        vetBiography.setVeterinarian(veterinarian);
        VetBiography savedVetBiography = vetBiographyRepository.save(vetBiography);
        return entityConverter.mapEntityToDto(savedVetBiography, VetBiographyDto.class);
    }

    /**
     * Updates an existing biography for a veterinarian.
     * <p>
     * This method retrieves an existing biography by ID, updates its details, and persists the changes.
     * Additionally, it evicts the cache entry for the updated veterinarian biography to maintain consistency.
     *
     * <b>Cache Eviction:</b>
     * <ul>
     *     <li>Removes the cached biography entry for the veterinarian to ensure the next retrieval gets fresh data.</li>
     * </ul>
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

        Cache cache = cacheManager.getCache("veterinarian_biography");
        if (cache != null) {
            cache.evict(existingVetBiography.getVeterinarian().getId());
        }

        VetBiography updatedVetBiography = vetBiographyRepository.save(existingVetBiography);
        return entityConverter.mapEntityToDto(updatedVetBiography, VetBiographyDto.class);
    }
}
