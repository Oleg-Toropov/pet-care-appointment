package com.olegtoropoff.petcareappointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olegtoropoff.petcareappointment.model.Pet;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static com.olegtoropoff.petcareappointment.utils.UrlMapping.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/clean_database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/test_pet_care_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class PetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void savePets_ReturnsSuccessResponse() throws Exception {
        Pet pet1 = new Pet();
        pet1.setName("Мурка");
        pet1.setType("Кошка");
        pet1.setBreed("Сибирская");
        pet1.setColor("Черный");

        Pet pet2 = new Pet();
        pet2.setName("Рекс");
        pet1.setType("Собака");
        pet1.setBreed("Лабрадор");
        pet1.setColor("Белый");

        List<Pet> petsToSave = Arrays.asList(pet1, pet2);

        mockMvc.perform(post(PETS + SAVE_PETS_FOR_APPOINTMENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petsToSave)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.PET_ADDED_SUCCESS)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name", is("Мурка")))
                .andExpect(jsonPath("$.data[1].name", is("Рекс")));
    }

    @Test
    void getPetById_ReturnsSuccessResponse() throws Exception {
        mockMvc.perform(get(PETS + GET_PET_BY_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.PET_FOUND)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is("Барсик")));
    }

    @Test
    void getPetById_ThrowsNotFoundException() throws Exception {
        mockMvc.perform(get(PETS + GET_PET_BY_ID, 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.PET_NOT_FOUND)));
    }

    @Test
    void deletePetById_ReturnsSuccessResponse() throws Exception {
        mockMvc.perform(delete(PETS + DELETE_PET_BY_ID, 8L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.PET_DELETE_SUCCESS)));
    }

    @Test
    void deletePetById_NotFound() throws Exception {
        mockMvc.perform(delete(PETS + DELETE_PET_BY_ID, 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.PET_NOT_FOUND)));
    }

    @Test
    void deletePetById_DeletionNotAllowed() throws Exception {
        mockMvc.perform(delete(PETS + DELETE_PET_BY_ID, 2L))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.NOT_ALLOWED_TO_DELETE_LAST_PET)));
    }

    @Test
    void updatePetById_ReturnsSuccessResponse() throws Exception {
        Pet updatePet = new Pet();
        updatePet.setName("NewName");
        updatePet.setType("Собака");
        updatePet.setBreed("Лабрадор");
        updatePet.setColor("Белый");

        mockMvc.perform(put(PETS + UPDATE_PET, 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.PET_UPDATE_SUCCESS)))
                .andExpect(jsonPath("$.data.name", is("NewName")));
    }

    @Test
    void updatePetById_NotFound() throws Exception {
        Pet updatePet = new Pet();
        updatePet.setName("NewName");
        updatePet.setType("Собака");
        updatePet.setBreed("Лабрадор");
        updatePet.setColor("Белый");

        mockMvc.perform(put(PETS + UPDATE_PET, 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePet)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.PET_NOT_FOUND)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getAllPetTypes_ReturnsSuccessResponse() throws Exception {
        mockMvc.perform(get(PETS + GET_PET_TYPES))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.PET_FOUND)))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0]", anyOf(is("Собака"), is("Кошка"))));
    }

    @Test
    void getAllPetColors_ReturnsSuccessResponse() throws Exception {
        mockMvc.perform(get(PETS + GET_PET_COLORS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.PET_FOUND)))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0]", anyOf(is("Белый"), is("Черный"))));
    }

    @Test
    void getAllPetBreeds_ReturnsSuccessResponse() throws Exception {
        String petType = "Кошка";
        mockMvc.perform(get(PETS + GET_PET_BREEDS)
                        .param("petType", petType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.PET_FOUND)))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0]", anyOf(is("Сибирская"), is("Мейн-кун"))));
    }
}