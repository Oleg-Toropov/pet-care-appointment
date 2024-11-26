package com.olegtoropoff.petcareappointment.service.vetbiography;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.VetBiography;
import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.repository.VetBiographyRepository;
import com.olegtoropoff.petcareappointment.repository.VeterinarianRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VetBiographyService implements IVetBiographyService {
    private final VetBiographyRepository vetBiographyRepository;
    private final VeterinarianRepository veterinarianRepository;

    @Override
    public VetBiography getVetBiographyByVetId(Long vetId) {
        return vetBiographyRepository.getVetBiographyByVeterinarianId(vetId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.VETERINARIAN_INFO_NOT_AVAILABLE));
    }

    @Override
    public VetBiography saveVetBiography(VetBiography vetBiography, Long vetId) {
        Veterinarian veterinarian = veterinarianRepository.findById(vetId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.VETERINARIAN_NOT_FOUND));
        vetBiography.setVeterinarian(veterinarian);
        return vetBiographyRepository.save(vetBiography);
    }

    @Override
    public VetBiography updateVetBiography(VetBiography vetBiography, Long id) {
        VetBiography existingVetBiography = vetBiographyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.BIOGRAPHY_NOT_FOUND));
        existingVetBiography.setBiography(vetBiography.getBiography());
        return vetBiographyRepository.save(existingVetBiography);
        }

    @Override
    public void deleteVetBiography(Long id) {
        VetBiography biographyToDelete = vetBiographyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.BIOGRAPHY_NOT_FOUND));
        vetBiographyRepository.delete(biographyToDelete);
    }
}
