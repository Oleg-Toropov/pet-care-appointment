package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.dto.PetDto;
import com.olegtoropoff.petcareappointment.exception.PetDeletionNotAllowedException;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Pet;
import com.olegtoropoff.petcareappointment.response.CustomApiResponse;
import com.olegtoropoff.petcareappointment.service.pet.IPetService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.CONFLICT;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PetControllerTest {
    @InjectMocks
    private PetController petController;

    @Mock
    private IPetService petService;

    @Test
    void deletePetById_WhenValidPetId_ReturnsSuccess() {
        doNothing().when(petService).deletePet(1L);

        ResponseEntity<CustomApiResponse> response = petController.deletePetById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.PET_DELETE_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(petService, times(1)).deletePet(1L);
    }

    @Test
    void deletePetById_WhenPetNotFound_ReturnsStatusNotFound() {
        doThrow(new ResourceNotFoundException(FeedBackMessage.PET_NOT_FOUND)).when(petService).deletePet(100L);

        ResponseEntity<CustomApiResponse> response = petController.deletePetById(100L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(FeedBackMessage.PET_NOT_FOUND, response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void deletePetById__WhenPetNotAllowedToDelete_ReturnsConflict() {
        doThrow(new PetDeletionNotAllowedException(FeedBackMessage.NOT_ALLOWED_TO_DELETE_LAST_PET))
                .when(petService).deletePet(1L);

        ResponseEntity<CustomApiResponse> response = petController.deletePetById(1L);

        assertEquals(CONFLICT, response.getStatusCode());
        assertEquals(FeedBackMessage.NOT_ALLOWED_TO_DELETE_LAST_PET, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void deletePetById_WhenInternalErrorOccurs_ReturnsStatusInternalServerError() {
        doThrow(new RuntimeException(FeedBackMessage.ERROR)).when(petService).deletePet(1L);

        ResponseEntity<CustomApiResponse> response = petController.deletePetById(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void updatePetById_WhenValidPetId_ReturnsSuccess() {
        Long id = 1L;
        String name = "Мурка";

        Pet updatePet = new Pet();
        updatePet.setName(name);

        PetDto updatedPetDto = new PetDto();
        updatedPetDto.setName(name);

        when(petService.updatePet(updatePet, id)).thenReturn(updatedPetDto);

        ResponseEntity<CustomApiResponse> response = petController.updatePetById(id, updatePet);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.PET_UPDATE_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(updatedPetDto, response.getBody().getData());
        verify(petService, times(1)).updatePet(updatePet, id);
    }

    @Test
    void updatePetById_WhenPetNotFound_ReturnsStatusNotFound() {
        Pet updatePet = new Pet();
        when(petService.updatePet(updatePet, 100L)).thenThrow(new ResourceNotFoundException(FeedBackMessage.PET_NOT_FOUND));

        ResponseEntity<CustomApiResponse> response = petController.updatePetById(100L, updatePet);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(FeedBackMessage.PET_NOT_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void updatePetById_WhenInternalErrorOccurs_ReturnsStatusInternalServerError() {
        Pet updatePet = new Pet();
        when(petService.updatePet(updatePet, 1L)).thenThrow(new RuntimeException(FeedBackMessage.ERROR));

        ResponseEntity<CustomApiResponse> response = petController.updatePetById(1L, updatePet);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void getAllPetTypes_ReturnsFoundStatusAndListOfPetTypes() {
        List<String> petTypes = Arrays.asList("Cat", "Dog", "Parrot");
        when(petService.getPetTypes()).thenReturn(petTypes);

        ResponseEntity<CustomApiResponse> response = petController.getAllPetTypes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.PET_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(petTypes, response.getBody().getData());
    }

    @Test
    void getAllPetTypes_WhenInternalErrorOccurs_ReturnsStatusInternalServerError() {
        when(petService.getPetTypes()).thenThrow(new RuntimeException(FeedBackMessage.ERROR));

        ResponseEntity<CustomApiResponse> response = petController.getAllPetTypes();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(FeedBackMessage.ERROR, response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void getAllPetColors_ReturnsFoundStatusAndListOfPetColors() {
        List<String> colors = Arrays.asList("Black", "White", "Brown");
        when(petService.getPetColors()).thenReturn(colors);

        ResponseEntity<CustomApiResponse> response = petController.getAllPetColors();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(FeedBackMessage.PET_FOUND, response.getBody().getMessage());
        assertEquals(colors, response.getBody().getData());
    }

    @Test
    void getAllPetColors_WhenInternalErrorOccurs_ReturnsStatusInternalServerError() {
        when(petService.getPetColors()).thenThrow(new RuntimeException(FeedBackMessage.ERROR));

        ResponseEntity<CustomApiResponse> response = petController.getAllPetColors();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void getAllPetBreeds_ReturnsFoundStatusAndListOfPetBreeds() {
        String petType = "Dog";
        List<String> dogBreeds = Arrays.asList("Labrador", "Beagle");
        when(petService.getPetBreeds(petType)).thenReturn(dogBreeds);

        ResponseEntity<CustomApiResponse> response = petController.getAllPetBreeds(petType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(FeedBackMessage.PET_FOUND, response.getBody().getMessage());
        assertEquals(dogBreeds, response.getBody().getData());
    }

    @Test
    void getAllPetBreeds_WhenInternalErrorOccurs_ReturnsStatusInternalServerError() {
        String petType = "Cat";
        when(petService.getPetBreeds(petType)).thenThrow(new RuntimeException(FeedBackMessage.ERROR));

        ResponseEntity<CustomApiResponse> response = petController.getAllPetBreeds(petType);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(FeedBackMessage.ERROR, response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
}