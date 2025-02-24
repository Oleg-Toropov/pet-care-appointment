package com.olegtoropoff.petcareappointment.service.pet;

import com.olegtoropoff.petcareappointment.dto.EntityConverter;
import com.olegtoropoff.petcareappointment.dto.PetDto;
import com.olegtoropoff.petcareappointment.exception.PetDeletionNotAllowedException;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.Pet;
import com.olegtoropoff.petcareappointment.repository.PetRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PetServiceTest {

    @InjectMocks
    private PetService petService;

    @Mock
    private PetRepository petRepository;

    @Spy
    private EntityConverter<Pet, PetDto> entityConverter = new EntityConverter<>(new ModelMapper());

    private Pet pet;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");
        pet.setType("Dog");
        pet.setColor("Brown");
        pet.setBreed("Labrador");
        pet.setAge(2);
    }

    @Test
    void savePetForAppointment_SinglePet_Success() {
        when(petRepository.save(pet)).thenReturn(pet);

        Pet result = petService.savePetForAppointment(pet);

        assertNotNull(result);
        assertEquals("Buddy", result.getName());
        verify(petRepository, times(1)).save(pet);
    }

    @Test
    void savePetForAppointment_MultiplePets_Success() {
        List<Pet> pets = Arrays.asList(pet, new Pet());
        when(petRepository.saveAll(pets)).thenReturn(pets);

        List<Pet> result = petService.savePetForAppointment(pets);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(petRepository, times(1)).saveAll(pets);
    }

    @Test
    void updatePet_Success() {
        String name = "Max";
        Pet updatedPet = new Pet();
        updatedPet.setName(name);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenReturn(updatedPet);

        PetDto result = petService.updatePet(updatedPet, 1L);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(entityConverter, times(1)).mapEntityToDto(any(Pet.class), eq(PetDto.class));
    }

    @Test
    void deletePet_WhenAllowed_Success() {
        Appointment appointment = new Appointment();
        pet.setAppointment(appointment);
        List<Pet> pets = new ArrayList<>();
        pets.add(pet);
        pets.add(new Pet());
        appointment.setPets(pets);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        petService.deletePet(1L);

        verify(petRepository, times(1)).delete(pet);
    }

    @Test
    void deletePet_WhenNotAllowed_ThrowsException() {
        Appointment appointment = new Appointment();
        appointment.setPets(List.of(pet));

        pet.setAppointment(appointment);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        Exception exception = assertThrows(PetDeletionNotAllowedException.class, () -> petService.deletePet(1L));

        assertEquals(FeedBackMessage.NOT_ALLOWED_TO_DELETE_LAST_PET, exception.getMessage());
        verify(petRepository, never()).delete(pet);
    }

    @Test
    void getPetTypes_Success() {
        List<String> petTypes = List.of("Dog", "Cat");
        when(petRepository.getDistinctPetTypes()).thenReturn(petTypes);

        List<String> result = petService.getPetTypes();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(petRepository, times(1)).getDistinctPetTypes();
    }

    @Test
    void getPetColors_Success() {
        List<String> petColors = List.of("Brown", "Black");
        when(petRepository.getDistinctPetColors()).thenReturn(petColors);

        List<String> result = petService.getPetColors();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(petRepository, times(1)).getDistinctPetColors();
    }

    @Test
    void getPetBreeds_Success() {
        List<String> petBreeds = List.of("Labrador", "Golden Retriever");
        when(petRepository.getDistinctPetBreedsByPetType("Dog")).thenReturn(petBreeds);

        List<String> result = petService.getPetBreeds("Dog");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(petRepository, times(1)).getDistinctPetBreedsByPetType("Dog");
    }
}
