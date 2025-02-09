package com.olegtoropoff.petcareappointment.service.vetbiography;

import com.olegtoropoff.petcareappointment.dto.EntityConverter;
import com.olegtoropoff.petcareappointment.dto.VetBiographyDto;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.VetBiography;
import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.repository.VetBiographyRepository;
import com.olegtoropoff.petcareappointment.repository.VeterinarianRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class VetBiographyServiceTest {

    @InjectMocks
    private VetBiographyService vetBiographyService;

    @Mock
    private VetBiographyRepository vetBiographyRepository;

    @Mock
    private VeterinarianRepository veterinarianRepository;

    @Spy
    private EntityConverter<VetBiography, VetBiographyDto> entityConverter = new EntityConverter<>(new ModelMapper());

    @Test
    void getVetBiographyByVetId_Success() {
        Long vetId = 1L;
        String biography = "Test biography";
        VetBiography vetBiography = new VetBiography();
        vetBiography.setBiography(biography);

        when(vetBiographyRepository.getVetBiographyByVeterinarianId(vetId)).thenReturn(Optional.of(vetBiography));

        VetBiographyDto result = vetBiographyService.getVetBiographyByVetId(vetId);

        assertNotNull(result);
        assertEquals(vetBiography.getBiography(), result.getBiography());
        verify(vetBiographyRepository).getVetBiographyByVeterinarianId(vetId);
        verify(entityConverter).mapEntityToDto(vetBiography, VetBiographyDto.class);
    }

    @Test
    void getVetBiographyByVetId_ThrowsException_WhenNotFound() {
        Long vetId = 1L;
        when(vetBiographyRepository.getVetBiographyByVeterinarianId(vetId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> vetBiographyService.getVetBiographyByVetId(vetId));

        assertEquals(FeedBackMessage.VETERINARIAN_INFO_NOT_AVAILABLE, exception.getMessage());

        verify(vetBiographyRepository).getVetBiographyByVeterinarianId(vetId);
    }

    @Test
    void saveVetBiography_Success() {
        Long vetId = 1L;
        String biography = "Test biography";
        Veterinarian veterinarian = new Veterinarian();
        VetBiography vetBiography = new VetBiography();
        vetBiography.setBiography(biography);
        when(veterinarianRepository.findById(vetId)).thenReturn(Optional.of(veterinarian));
        when(vetBiographyRepository.save(vetBiography)).thenReturn(vetBiography);

        VetBiographyDto result = vetBiographyService.saveVetBiography(vetBiography, vetId);

        assertNotNull(result);
        assertEquals(vetBiography.getBiography(), result.getBiography());

        verify(veterinarianRepository).findById(vetId);
        verify(vetBiographyRepository).save(vetBiography);
        verify(entityConverter).mapEntityToDto(vetBiography, VetBiographyDto.class);
    }

    @Test
    void saveVetBiography_ThrowsException_WhenVeterinarianNotFound() {
        Long vetId = 1L;
        VetBiography biography = new VetBiography();
        when(veterinarianRepository.findById(vetId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> vetBiographyService.saveVetBiography(biography, vetId));

        assertEquals(FeedBackMessage.VETERINARIAN_NOT_FOUND, exception.getMessage());

        verify(veterinarianRepository).findById(vetId);
        verifyNoInteractions(vetBiographyRepository);
    }

    @Test
    void updateVetBiography_Success() {
        Long bioId = 1L;
        String biography = "Updated biography";

        VetBiography existingBiography = new VetBiography();
        existingBiography.setBiography("Old biography");

        VetBiography updatedBiography = new VetBiography();
        updatedBiography.setBiography(biography);

        VetBiographyDto expectedDto = new VetBiographyDto();
        expectedDto.setBiography(biography);

        Veterinarian vet = new Veterinarian();
        vet.setId(42L);
        existingBiography.setVeterinarian(vet);

        when(vetBiographyRepository.findById(bioId)).thenReturn(Optional.of(existingBiography));
        when(vetBiographyRepository.save(any(VetBiography.class))).thenReturn(updatedBiography);

        VetBiographyDto result = vetBiographyService.updateVetBiography(updatedBiography, bioId);

        assertNotNull(result);
        assertEquals(biography, result.getBiography());

        verify(vetBiographyRepository).findById(bioId);
        verify(vetBiographyRepository).save(any(VetBiography.class));
        verify(entityConverter).mapEntityToDto(updatedBiography, VetBiographyDto.class);
    }

    @Test
    void updateVetBiography_ThrowsException_WhenBiographyNotFound() {
        Long bioId = 1L;
        VetBiography updatedBiography = new VetBiography();
        when(vetBiographyRepository.findById(bioId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> vetBiographyService.updateVetBiography(updatedBiography, bioId));

        assertEquals(FeedBackMessage.BIOGRAPHY_NOT_FOUND, exception.getMessage());

        verify(vetBiographyRepository).findById(bioId);
        verifyNoMoreInteractions(vetBiographyRepository);
    }
}
