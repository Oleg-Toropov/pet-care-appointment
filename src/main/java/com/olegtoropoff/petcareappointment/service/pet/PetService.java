package com.olegtoropoff.petcareappointment.service.pet;

import com.olegtoropoff.petcareappointment.dto.EntityConverter;
import com.olegtoropoff.petcareappointment.dto.PetDto;
import com.olegtoropoff.petcareappointment.exception.PetDeletionNotAllowedException;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.Pet;
import com.olegtoropoff.petcareappointment.repository.PetRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing {@link Pet} entities.
 * Provides functionality to add, update, delete, and retrieve pet details.
 */
@Service
@RequiredArgsConstructor
public class PetService implements IPetService {
    private final PetRepository petRepository;
    private final EntityConverter<Pet, PetDto> entityConverter;

    /**
     * Saves a list of pets for an appointment.
     *
     * @param pets the list of pets to be saved
     * @return the list of saved pets
     */
    @Override
    public List<Pet> savePetForAppointment(List<Pet> pets) {
        return petRepository.saveAll(pets);
    }

    /**
     * Saves a single pet for an appointment.
     *
     * @param pet the pet to be saved
     * @return the saved pet
     */
    @Override
    public Pet savePetForAppointment(Pet pet) {
        return petRepository.save(pet);
    }

    /**
     * Updates an existing pet's details.
     *
     * @param pet   the updated pet details
     * @param petId the ID of the pet to be updated
     * @return the updated pet
     */
    @Override
    public PetDto updatePet(Pet pet, Long petId) {
        Pet existingPet = getPetById(petId);
        existingPet.setName(pet.getName());
        existingPet.setAge(pet.getAge());
        existingPet.setType(pet.getType());
        existingPet.setColor(pet.getColor());
        existingPet.setBreed(pet.getBreed());
        existingPet.setAge(pet.getAge());
        Pet savedPet = petRepository.save(existingPet);
        return entityConverter.mapEntityToDto(savedPet, PetDto.class);
    }

    /**
     * Deletes a pet. Ensures that the last pet of an appointment cannot be deleted.
     *
     * @param petId the ID of the pet to be deleted
     * @throws ResourceNotFoundException if the pet with the given ID is not found
     * @throws PetDeletionNotAllowedException if the last pet of an appointment is being deleted
     */
    @Transactional
    @Override
    public void deletePet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.PET_NOT_FOUND));

        Appointment appointment = pet.getAppointment();

        if (appointment.getPets().size() > 1) {
            appointment.getPets().remove(pet);
            petRepository.delete(pet);
        }
        else {
            throw new PetDeletionNotAllowedException(FeedBackMessage.NOT_ALLOWED_TO_DELETE_LAST_PET);
        }
    }

    /**
     * Retrieves a list of distinct pet types.
     *
     * @return a list of distinct pet types
     */
    @Override
    public List<String> getPetTypes() {
        return petRepository.getDistinctPetTypes();
    }

    /**
     * Retrieves a list of distinct pet colors.
     *
     * @return a list of distinct pet colors
     */
    @Override
    public List<String> getPetColors() {
        return petRepository.getDistinctPetColors();
    }

    /**
     * Retrieves a list of distinct pet breeds for a given pet type.
     *
     * @param petType the type of pet to filter breeds by
     * @return a list of distinct pet breeds for the given pet type
     */
    @Override
    public List<String> getPetBreeds(String petType) {
        return petRepository.getDistinctPetBreedsByPetType(petType);
    }

    /**
     * Retrieves a pet by its ID.
     *
     * @param petId the ID of the pet to retrieve
     * @return the pet with the given ID
     * @throws ResourceNotFoundException if the pet with the given ID is not found
     */
    private Pet getPetById(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.PET_NOT_FOUND));
    }
}
