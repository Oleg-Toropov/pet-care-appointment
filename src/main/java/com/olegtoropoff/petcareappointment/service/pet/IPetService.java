package com.olegtoropoff.petcareappointment.service.pet;

import com.olegtoropoff.petcareappointment.dto.PetDto;
import com.olegtoropoff.petcareappointment.model.Pet;

import java.util.List;

/**
 * Interface for managing pet-related operations.
 * Defines methods for saving, updating, deleting, and retrieving pet data.
 */
public interface IPetService {

    /**
     * Saves a list of pets associated with an appointment.
     *
     * @param pets the list of pets to save
     * @return the list of saved pets
     */
    List<Pet> savePetForAppointment(List<Pet> pets);

    /**
     * Saves a single pet associated with an appointment.
     *
     * @param pet the pet to save
     * @return the saved pet
     */
    Pet savePetForAppointment(Pet pet);

    /**
     * Updates the details of an existing pet.
     *
     * @param pet   the updated pet details
     * @param petId the ID of the pet to update
     * @return the updated pet
     */
    PetDto updatePet(Pet pet, Long petId);

    /**
     * Deletes a pet by its ID. Ensures that the last pet of an appointment cannot be deleted.
     *
     * @param petId the ID of the pet to delete
     */
    void deletePet(Long petId);

    /**
     * Retrieves a pet by its ID.
     *
     * @param petId the ID of the pet to retrieve
     * @return the pet with the specified ID
     */
    Pet getPetById(Long petId);

    /**
     * Retrieves a list of distinct pet types.
     *
     * @return a list of distinct pet types
     */
    List<String> getPetTypes();

    /**
     * Retrieves a list of distinct pet colors.
     *
     * @return a list of distinct pet colors
     */
    List<String> getPetColors();

    /**
     * Retrieves a list of distinct pet breeds for a given pet type.
     *
     * @param petType the type of pet to filter breeds by
     * @return a list of distinct pet breeds for the specified pet type
     */
    List<String> getPetBreeds(String petType);
}
