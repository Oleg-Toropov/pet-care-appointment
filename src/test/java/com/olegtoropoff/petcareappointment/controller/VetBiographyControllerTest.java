package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.VetBiography;
import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.response.CustomApiResponse;
import com.olegtoropoff.petcareappointment.service.vetbiography.IVetBiographyService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@Tag("unit")
public class VetBiographyControllerTest {

    @InjectMocks
    private VetBiographyController vetBiographyController;

    @Mock
    private IVetBiographyService vetBiographyService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    public void getVetBiographyByVetId_WhenBiographyExists_ReturnsBiography() {
        Long vetId = 1L;
        VetBiography biography = new VetBiography();
        biography.setId(1L);
        biography.setVeterinarian(new Veterinarian());
        biography.setBiography("Experienced veterinarian");
        when(vetBiographyService.getVetBiographyByVetId(vetId)).thenReturn(biography);

        ResponseEntity<CustomApiResponse> response = vetBiographyController.getVetBiographyByVetId(vetId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.BIOGRAPHY_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(biography, response.getBody().getData());
    }

    @Test
    public void getVetBiographyByVetId_WhenNotFound_ThrowsResourceNotFoundException() {
        Long vetId = 100L;
        String errorMessage = FeedBackMessage.VETERINARIAN_INFO_NOT_AVAILABLE;
        when(vetBiographyService.getVetBiographyByVetId(vetId)).thenThrow(new ResourceNotFoundException(errorMessage));

        ResponseEntity<CustomApiResponse> response = vetBiographyController.getVetBiographyByVetId(vetId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void getVetBiographyByVetId_InternalErrorOccurs_ReturnsInternalServerError() {
        Long vetId = 1L;
        String errorMessage = FeedBackMessage.ERROR;
        doThrow(new RuntimeException(errorMessage))
                .when(vetBiographyService).getVetBiographyByVetId(vetId);

        ResponseEntity<CustomApiResponse> response = vetBiographyController.getVetBiographyByVetId(vetId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void saveVetBiography_WhenSuccess_ReturnsSavedBiography() {
        Long vetId = 1L;
        VetBiography request = new VetBiography();
        request.setBiography("New biography");

        VetBiography savedBiography = new VetBiography();
        savedBiography.setId(1L);
        savedBiography.setVeterinarian(new Veterinarian());
        savedBiography.setBiography("New biography");

        when(vetBiographyService.saveVetBiography(request, vetId)).thenReturn(savedBiography);

        ResponseEntity<CustomApiResponse> response = vetBiographyController.saveVetBiography(vetId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.BIOGRAPHY_SAVED_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(savedBiography, response.getBody().getData());
    }

    @Test
    public void saveVetBiography_WhenNotFound_ThrowsResourceNotFoundException() {
        Long vetId = 1L;
        VetBiography request = new VetBiography();
        request.setBiography("New biography");
        String errorMessage = FeedBackMessage.VET_OR_PATIENT_NOT_FOUND;

        when(vetBiographyService.saveVetBiography(request, vetId)).thenThrow(new ResourceNotFoundException(errorMessage));

        ResponseEntity<CustomApiResponse> response = vetBiographyController.saveVetBiography(vetId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void saveVetBiography_InternalErrorOccurs_ReturnsInternalServerError() {
        Long vetId = 1L;
        VetBiography request = new VetBiography();
        String errorMessage = FeedBackMessage.ERROR;
        doThrow(new RuntimeException(errorMessage))
                .when(vetBiographyService).saveVetBiography(request, vetId);

        ResponseEntity<CustomApiResponse> response = vetBiographyController.saveVetBiography(vetId, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void updateVetBiography_WhenSuccess_ReturnsUpdatedBiography() {
        Long biographyId = 1L;
        VetBiography request = new VetBiography();
        request.setBiography("Updated biography");

        VetBiography updatedBiography = new VetBiography();
        updatedBiography.setId(biographyId);
        updatedBiography.setBiography("Updated biography");

        when(vetBiographyService.updateVetBiography(request, biographyId)).thenReturn(updatedBiography);

        ResponseEntity<CustomApiResponse> response = vetBiographyController.updateVetBiography(biographyId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals( FeedBackMessage.BIOGRAPHY_UPDATED_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(updatedBiography, response.getBody().getData());
    }

    @Test
    public void updateVetBiography_WhenNotFound_ThrowsResourceNotFoundException() {
        Long biographyId = 1L;
        VetBiography request = new VetBiography();
        request.setBiography("Updated biography");
        String errorMessage = FeedBackMessage.BIOGRAPHY_NOT_FOUND;

        when(vetBiographyService.updateVetBiography(request, biographyId)).thenThrow(new ResourceNotFoundException(errorMessage));

        ResponseEntity<CustomApiResponse> response = vetBiographyController.updateVetBiography(biographyId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void updateVetBiography_InternalErrorOccurs_ReturnsInternalServerError() {
        Long biographyId = 1L;
        VetBiography request = new VetBiography();
        String errorMessage = FeedBackMessage.ERROR;
        doThrow(new RuntimeException(errorMessage))
                .when(vetBiographyService).updateVetBiography(request, biographyId);

        ResponseEntity<CustomApiResponse> response = vetBiographyController.updateVetBiography(biographyId, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void deleteVetBiography_WhenSuccess_ReturnsSuccessMessage() {
        Long biographyId = 1L;
        doNothing().when(vetBiographyService).deleteVetBiography(biographyId);

        ResponseEntity<CustomApiResponse> response = vetBiographyController.deleteVetBiography(biographyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.BIOGRAPHY_DELETED_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void deleteVetBiography_WhenNotFound_ThrowsResourceNotFoundException() {
        Long biographyId = 100L;
        doThrow(new ResourceNotFoundException(FeedBackMessage.BIOGRAPHY_NOT_FOUND)).when(vetBiographyService).deleteVetBiography(biographyId);

        ResponseEntity<CustomApiResponse> response = vetBiographyController.deleteVetBiography(biographyId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(FeedBackMessage.BIOGRAPHY_NOT_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void deleteVetBiography_InternalErrorOccurs_ReturnsInternalServerError() {
        Long biographyId = 1L;
        String errorMessage = FeedBackMessage.ERROR;
        doThrow(new RuntimeException(errorMessage))
                .when(vetBiographyService).deleteVetBiography(biographyId);

        ResponseEntity<CustomApiResponse> response = vetBiographyController.deleteVetBiography(biographyId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }
}