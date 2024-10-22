package com.olegtoropoff.petcareappointment.service.pet;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Pet;
import com.olegtoropoff.petcareappointment.repository.PetRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService implements IPetService {
    private final PetRepository petRepository;

    @Override
    public List<Pet> savePetForAppointment(List<Pet> pets) {
        return petRepository.saveAll(pets);
    }

    @Override
    public Pet updatePet(Pet pet, Long petId) {
        Pet existingPet = getPetById(petId);
        existingPet.setName(pet.getName());
        existingPet.setAge(pet.getAge());
        existingPet.setType(pet.getType());
        existingPet.setColor(pet.getColor());
        existingPet.setBreed(pet.getBreed());
        existingPet.setAge(pet.getAge());
        return petRepository.save(existingPet);
    }

    @Override
    public void deletePet(Long petId) {
        petRepository.findById(petId)
                .ifPresentOrElse(petRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND);
                        });
    }

    @Override
    public Pet getPetById(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND));
    }

    @Override
    public List<String> getPetTypes() {
        return petRepository.getDistinctPetTypes();
    }

    @Override
    public List<String> getPetColors() {
        return petRepository.getDistinctPetColors();
    }

    @Override
    public List<String> getPetBreeds(String petType) {
        return petRepository.getDistinctPetBreedsByPetType(petType);
    }
}
