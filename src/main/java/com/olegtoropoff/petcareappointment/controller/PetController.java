package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.exception.PetDeletionNotAllowedException;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Pet;
import com.olegtoropoff.petcareappointment.response.CustomApiResponse;
import com.olegtoropoff.petcareappointment.service.pet.IPetService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

/**
 * REST controller for managing pets.
 * Provides endpoints for CRUD operations and fetching pet attributes like types, colors, and breeds.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.PETS)
public class PetController {
    private final IPetService petService;

    /**
     * Saves a list of pets associated with an appointment.
     *
     * @param pets the list of pets to save.
     * @return a response containing the saved pets.
     */
    @PostMapping(UrlMapping.SAVE_PETS_FOR_APPOINTMENT)
    public ResponseEntity<CustomApiResponse> savePets(@RequestBody List<Pet> pets) {
        try {
            List<Pet> savedPets = petService.savePetForAppointment(pets);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.PET_ADDED_SUCCESS, savedPets));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Fetches a pet by its ID.
     *
     * @param petId the ID of the pet.
     * @return a response containing the pet details.
     */
    @GetMapping(UrlMapping.GET_PET_BY_ID)
    public ResponseEntity<CustomApiResponse> getPetById(@PathVariable Long petId) {
        try {
            Pet pet = petService.getPetById(petId);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.PET_FOUND, pet));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Deletes a pet by its ID.
     *
     * @param petId the ID of the pet to delete.
     * @return a response indicating the result of the operation.
     */
    @DeleteMapping(UrlMapping.DELETE_PET_BY_ID)
    public ResponseEntity<CustomApiResponse> deletePetById(@PathVariable Long petId) {
        try {
            petService.deletePet(petId);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.PET_DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        } catch (PetDeletionNotAllowedException e) {
            return ResponseEntity.status(CONFLICT).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Updates the details of a pet.
     *
     * @param petId the ID of the pet to update.
     * @param pet   the updated pet details.
     * @return a response containing the updated pet.
     */
    @PutMapping(UrlMapping.UPDATE_PET)
    public ResponseEntity<CustomApiResponse> updatePetById(@PathVariable Long petId, @RequestBody Pet pet) {
        try {
            Pet thePet = petService.updatePet(pet, petId);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.PET_UPDATE_SUCCESS, thePet));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Fetches all distinct pet types.
     *
     * @return a response containing the list of pet types.
     */
    @GetMapping(UrlMapping.GET_PET_TYPES)
    public ResponseEntity<CustomApiResponse> getAllPetTypes() {
        try {
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.PET_FOUND, petService.getPetTypes()));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Fetches all distinct pet colors.
     *
     * @return a response containing the list of pet colors.
     */
    @GetMapping(UrlMapping.GET_PET_COLORS)
    public ResponseEntity<CustomApiResponse> getAllPetColors() {
        try {
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.PET_FOUND, petService.getPetColors()));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Fetches all distinct breeds for a given pet type.
     *
     * @param petType the type of pet.
     * @return a response containing the list of pet breeds for the specified type.
     */
    @GetMapping(UrlMapping.GET_PET_BREEDS)
    public ResponseEntity<CustomApiResponse> getAllPetBreeds(@RequestParam String petType) {
        try {
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.PET_FOUND, petService.getPetBreeds(petType)));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }
}
