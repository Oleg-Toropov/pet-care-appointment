package com.olegtoropoff.petcareappointment.service.pet;

import com.olegtoropoff.petcareappointment.model.Pet;

import java.util.List;

public interface IPetService {
    List<Pet> savePetForAppointment(List<Pet> pets);
    Pet updatePet(Pet pet, Long petId);
    void deletePet(Long petId);
    Pet getPetById(Long petId);

    List<String> getPetTypes();

    List<String> getPetColors();

    List<String> getPetBreeds(String petType);
}
