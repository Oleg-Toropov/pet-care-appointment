package com.olegtoropoff.petcareappointment.controller;


import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static com.olegtoropoff.petcareappointment.utils.UrlMapping.*;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/clean_database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/test_pet_care_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class VetBiographyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getVetBiographyByVetId_WhenBiographyExists_ReturnsBiography() throws Exception {
        mockMvc.perform(get(BIOGRAPHIES + GET_BIOGRAPHY_BY_VET_ID, 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.BIOGRAPHY_FOUND))
                .andExpect(jsonPath("$.data.biography").value("Валерия Павлова проводит сложные хирургические операции. Опыт работы более 7 лет."));
    }

    @Test
    public void getVetBiographyByVetId_WhenNotFound_ReturnsNotFound() throws Exception {
        mockMvc.perform(get(BIOGRAPHIES + GET_BIOGRAPHY_BY_VET_ID, 11L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.VETERINARIAN_INFO_NOT_AVAILABLE))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void saveVetBiography_WhenSuccess_ReturnsSavedBiography() throws Exception {
        String requestJson = """
                {
                    "biography": "New biography for veterinarian"
                }
                """;
        mockMvc.perform(post(BIOGRAPHIES + SAVE_BIOGRAPHY, 9L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.BIOGRAPHY_SAVED_SUCCESS))
                .andExpect(jsonPath("$.data.biography").value("New biography for veterinarian"));
    }

    @Test
    public void saveVetBiography_WhenVetNotFound_ReturnsNotFound() throws Exception {
        String requestJson = """
                {
                    "biography": "Biography for non-existent veterinarian"
                }
                """;
        mockMvc.perform(post(BIOGRAPHIES + SAVE_BIOGRAPHY, 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.VETERINARIAN_NOT_FOUND))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void updateVetBiography_WhenSuccess_ReturnsUpdatedBiography() throws Exception {
        String requestJson = """
                {
                    "biography": "Updated biography for veterinarian"
                }
                """;
        mockMvc.perform(put(BIOGRAPHIES + UPDATE_BIOGRAPHY, 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.BIOGRAPHY_UPDATED_SUCCESS))
                .andExpect(jsonPath("$.data.biography").value("Updated biography for veterinarian"));
    }

    @Test
    public void updateVetBiography_WhenBiographyNotFound_ReturnsNotFound() throws Exception {
        String requestJson = """
                {
                    "biography": "Updated biography non-existent"
                }
                """;
        mockMvc.perform(put(BIOGRAPHIES + UPDATE_BIOGRAPHY, 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.BIOGRAPHY_NOT_FOUND))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void deleteVetBiography_WhenSuccess_ReturnsSuccessMessage() throws Exception {
        mockMvc.perform(delete(BIOGRAPHIES + DELETE_BIOGRAPHY, 4L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.BIOGRAPHY_DELETED_SUCCESS))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void deleteVetBiography_WhenNotFound_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete(BIOGRAPHIES + DELETE_BIOGRAPHY, 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.BIOGRAPHY_NOT_FOUND))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }
}
