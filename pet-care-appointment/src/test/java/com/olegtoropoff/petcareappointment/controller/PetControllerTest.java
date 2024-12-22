package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.exception.PetDeletionNotAllowedException;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Pet;
import com.olegtoropoff.petcareappointment.response.ApiResponse;
import com.olegtoropoff.petcareappointment.service.pet.IPetService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.CONFLICT;


class PetControllerTest {
    @InjectMocks
    private PetController petController;

    @Mock
    private IPetService petService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void savePets_Success() {
        List<Pet> petsToSave = Arrays.asList(new Pet(), new Pet());
        when(petService.savePetForAppointment(petsToSave)).thenReturn(petsToSave);

        ResponseEntity<ApiResponse> response = petController.savePets(petsToSave);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.PET_ADDED_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(petsToSave, response.getBody().getData());
        verify(petService, times(1)).savePetForAppointment(petsToSave);
    }

    @Test
    public void savePets_WhenInternalErrorOccurs_ReturnsStatusInternalServerError() throws SQLException {
        List<Pet> petsToSave = Arrays.asList(new Pet(), new Pet());
        when(petService.savePetForAppointment(petsToSave)).thenThrow(new RuntimeException());

        ResponseEntity<ApiResponse> response = petController.savePets(petsToSave);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void getPetById_WhenPetExists_ReturnsPetWithStatusOk() {
        Pet pet1 = new Pet();
        pet1.setId(1L);
        when(petService.getPetById(1L)).thenReturn(pet1);

        ResponseEntity<ApiResponse> response = petController.getPetById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.PET_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(pet1, response.getBody().getData());
        verify(petService, times(1)).getPetById(1L);
    }

    @Test
    void getPetById_WhenPetNotFound_ReturnsStatusNotFound() {
        when(petService.getPetById(100L)).thenThrow(new ResourceNotFoundException(FeedBackMessage.PET_NOT_FOUND));

        ResponseEntity<ApiResponse> response = petController.getPetById(100L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(FeedBackMessage.PET_NOT_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void getPetById_WhenInternalErrorOccurs_ReturnsStatusInternalServerError() throws SQLException {
        when(petService.getPetById(1L)).thenThrow(new RuntimeException());

        ResponseEntity<ApiResponse> response = petController.getPetById(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void deletePetById_WhenValidPetId_ReturnsSuccess() {
        doNothing().when(petService).deletePet(1L);

        ResponseEntity<ApiResponse> response = petController.deletePetById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.PET_DELETE_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(petService, times(1)).deletePet(1L);
    }

    @Test
    void deletePetById_WhenPetNotFound_ReturnsStatusNotFound() {
        doThrow(new ResourceNotFoundException(FeedBackMessage.PET_NOT_FOUND)).when(petService).deletePet(100L);

        ResponseEntity<ApiResponse> response = petController.deletePetById(100L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(FeedBackMessage.PET_NOT_FOUND, response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void deletePetById__WhenPetNotAllowedToDelete_ReturnsConflict() {
        doThrow(new PetDeletionNotAllowedException(FeedBackMessage.NOT_ALLOWED_TO_DELETE_LAST_PET))
                .when(petService).deletePet(1L);

        ResponseEntity<ApiResponse> response = petController.deletePetById(1L);

        assertEquals(CONFLICT, response.getStatusCode());
        assertEquals(FeedBackMessage.NOT_ALLOWED_TO_DELETE_LAST_PET, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void deletePetById_WhenInternalErrorOccurs_ReturnsStatusInternalServerError() {
        doThrow(new RuntimeException(FeedBackMessage.ERROR)).when(petService).deletePet(1L);

        ResponseEntity<ApiResponse> response = petController.deletePetById(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void updatePetById_WhenValidPetId_ReturnsSuccess() {
        Pet updatePet = new Pet();
        updatePet.setName("Мурка");
        when(petService.updatePet(updatePet, 1L)).thenReturn(updatePet);

        ResponseEntity<ApiResponse> response = petController.updatePetById(1L, updatePet);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.PET_UPDATE_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(updatePet, response.getBody().getData());
        verify(petService, times(1)).updatePet(updatePet, 1L);
    }

    @Test
    void updatePetById_WhenPetNotFound_ReturnsStatusNotFound() {
        Pet updatePet = new Pet();
        when(petService.updatePet(updatePet, 100L)).thenThrow(new ResourceNotFoundException(FeedBackMessage.PET_NOT_FOUND));

        ResponseEntity<ApiResponse> response = petController.updatePetById(100L, updatePet);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(FeedBackMessage.PET_NOT_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void updatePetById_WhenInternalErrorOccurs_ReturnsStatusInternalServerError() {
        Pet updatePet = new Pet();
        when(petService.updatePet(updatePet, 1L)).thenThrow(new RuntimeException(FeedBackMessage.ERROR));

        ResponseEntity<ApiResponse> response = petController.updatePetById(1L, updatePet);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void getAllPetTypes_ReturnsFoundStatusAndListOfPetTypes() {
        List<String> petTypes = Arrays.asList("Cat", "Dog", "Parrot");
        when(petService.getPetTypes()).thenReturn(petTypes);

        ResponseEntity<ApiResponse> response = petController.getAllPetTypes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.PET_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(petTypes, response.getBody().getData());
    }

    @Test
    void getAllPetTypes_WhenInternalErrorOccurs_ReturnsStatusInternalServerError() {
        when(petService.getPetTypes()).thenThrow(new RuntimeException(FeedBackMessage.ERROR));

        ResponseEntity<ApiResponse> response = petController.getAllPetTypes();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(FeedBackMessage.ERROR, response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void getAllPetColors_ReturnsFoundStatusAndListOfPetColors() {
        List<String> colors = Arrays.asList("Black", "White", "Brown");
        when(petService.getPetColors()).thenReturn(colors);

        ResponseEntity<ApiResponse> response = petController.getAllPetColors();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(FeedBackMessage.PET_FOUND, response.getBody().getMessage());
        assertEquals(colors, response.getBody().getData());
    }

    @Test
    void getAllPetColors_WhenInternalErrorOccurs_ReturnsStatusInternalServerError() {
        when(petService.getPetColors()).thenThrow(new RuntimeException(FeedBackMessage.ERROR));

        ResponseEntity<ApiResponse> response = petController.getAllPetColors();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void getAllPetBreeds_ReturnsFoundStatusAndListOfPetBreeds() {
        String petType = "Dog";
        List<String> dogBreeds = Arrays.asList("Labrador", "Beagle");
        when(petService.getPetBreeds(petType)).thenReturn(dogBreeds);

        ResponseEntity<ApiResponse> response = petController.getAllPetBreeds(petType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(FeedBackMessage.PET_FOUND, response.getBody().getMessage());
        assertEquals(dogBreeds, response.getBody().getData());
    }

    @Test
    void getAllPetBreeds_WhenInternalErrorOccurs_ReturnsStatusInternalServerError() {
        String petType = "Cat";
        when(petService.getPetBreeds(petType)).thenThrow(new RuntimeException(FeedBackMessage.ERROR));

        ResponseEntity<ApiResponse> response = petController.getAllPetBreeds(petType);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(FeedBackMessage.ERROR, response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
}