package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.olegtoropoff.petcareappointment.utils.UrlMapping.*;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/clean_database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/test_pet_care_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class VeterinarianControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAllVeterinarians_ReturnsVeterinariansWithDetails() throws Exception {
        mockMvc.perform(get(VETERINARIANS + GET_ALL_VETERINARIANS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.RESOURCE_FOUND))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(4));
    }

    @Test
    public void searchVeterinariansForAppointment_WhenAvailable_ReturnsVeterinarians() throws Exception {
        LocalDate date = LocalDate.of(2024, 12, 20);
        LocalTime time = LocalTime.of(14, 30);

        mockMvc.perform(get(VETERINARIANS + SEARCH_VETERINARIAN_FOR_APPOINTMENT)
                        .param("date", date.toString())
                        .param("time", time.toString())
                        .param("specialization", "Хирург"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.RESOURCE_FOUND))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    public void searchVeterinariansForAppointment_WhenNoneAvailable_ReturnsNotFound() throws Exception {
        LocalDate date = LocalDate.of(2025, 1, 5);
        LocalTime time = LocalTime.of(17, 0);

        mockMvc.perform(get(VETERINARIANS + SEARCH_VETERINARIAN_FOR_APPOINTMENT)
                        .param("date", date.toString())
                        .param("time", time.toString())
                        .param("specialization", "Диагност"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.NO_VETS_AVAILABLE));
    }

    @Test
    public void getAllSpecializations_ReturnsSpecializations() throws Exception {
        mockMvc.perform(get(VETERINARIANS + GET_ALL_SPECIALIZATIONS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.RESOURCE_FOUND))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasItems("Хирург", "Терапевт", "Диагност")));
    }

    @Test
    public void aggregateVetsBySpecialization_ReturnsAggregatedData() throws Exception {
        mockMvc.perform(get(VETERINARIANS + AGGREGATE_VETERINARIANS_BY_SPECIALIZATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].specialization").value("Диагност"))
                .andExpect(jsonPath("$[0].count").value(1))
                .andExpect(jsonPath("$[1].specialization").value("Терапевт"))
                .andExpect(jsonPath("$[1].count").value(2))
                .andExpect(jsonPath("$[2].specialization").value("Хирург"))
                .andExpect(jsonPath("$[2].count").value(2));
    }

    @Test
    public void getAvailableTimeForBookAppointment_ReturnsAvailableTimes() throws Exception {
        LocalDate date = LocalDate.now();
        date = date.plusDays(1);

        mockMvc.perform(get(VETERINARIANS + GET_AVAILABLE_TIME_FOR_BOOK_APPOINTMENT, 9L)
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.AVAILABLE_TIME_FOR_APPOINTMENT_FOUND))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasItems("10:00:00", "11:00:00")));
    }
}