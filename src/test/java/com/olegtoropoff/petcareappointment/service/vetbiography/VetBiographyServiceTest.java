package com.olegtoropoff.petcareappointment.service.vetbiography;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.VetBiography;
import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.repository.VetBiographyRepository;
import com.olegtoropoff.petcareappointment.repository.VeterinarianRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

@Tag("unit")
class VetBiographyServiceTest {

    @InjectMocks
    private VetBiographyService vetBiographyService;

    @Mock
    private VetBiographyRepository vetBiographyRepository;

    @Mock
    private VeterinarianRepository veterinarianRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getVetBiographyByVetId_Success() {
        Long vetId = 1L;
        VetBiography biography = new VetBiography();
        when(vetBiographyRepository.getVetBiographyByVeterinarianId(vetId)).thenReturn(Optional.of(biography));

        VetBiography result = vetBiographyService.getVetBiographyByVetId(vetId);

        assertNotNull(result);
        assertEquals(biography, result);

        verify(vetBiographyRepository).getVetBiographyByVeterinarianId(vetId);
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
        Veterinarian veterinarian = new Veterinarian();
        VetBiography biography = new VetBiography();
        when(veterinarianRepository.findById(vetId)).thenReturn(Optional.of(veterinarian));
        when(vetBiographyRepository.save(biography)).thenReturn(biography);

        VetBiography result = vetBiographyService.saveVetBiography(biography, vetId);

        assertNotNull(result);
        assertEquals(biography, result);
        assertEquals(veterinarian, biography.getVeterinarian());

        verify(veterinarianRepository).findById(vetId);
        verify(vetBiographyRepository).save(biography);
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
        VetBiography existingBiography = new VetBiography();
        VetBiography updatedBiography = new VetBiography();
        updatedBiography.setBiography("Updated biography");
        when(vetBiographyRepository.findById(bioId)).thenReturn(Optional.of(existingBiography));
        when(vetBiographyRepository.save(existingBiography)).thenReturn(existingBiography);

        VetBiography result = vetBiographyService.updateVetBiography(updatedBiography, bioId);

        assertNotNull(result);
        assertEquals("Updated biography", existingBiography.getBiography());

        verify(vetBiographyRepository).findById(bioId);
        verify(vetBiographyRepository).save(existingBiography);
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
