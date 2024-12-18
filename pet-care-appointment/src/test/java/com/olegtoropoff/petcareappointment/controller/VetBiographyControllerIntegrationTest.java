package com.olegtoropoff.petcareappointment.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

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
        mockMvc.perform(get("/api/v1/biographies/biography/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Биография найдена"))
                .andExpect(jsonPath("$.data.biography").value("Валерия Павлова проводит сложные хирургические операции. Опыт работы более 7 лет."));
    }

    @Test
    public void getVetBiographyByVetId_WhenNotFound_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/biographies/biography/11"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Информация о ветеринаре пока отсутствует, но вскоре появится!"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void saveVetBiography_WhenSuccess_ReturnsSavedBiography() throws Exception {
        String requestJson = """
                {
                    "biography": "New biography for veterinarian"
                }
                """;
        mockMvc.perform(post("/api/v1/biographies/biography/9/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Биография успешно сохранена"))
                .andExpect(jsonPath("$.data.biography").value("New biography for veterinarian"));
    }

    @Test
    public void saveVetBiography_WhenVetNotFound_ReturnsNotFound() throws Exception {
        String requestJson = """
                {
                    "biography": "Biography for non-existent veterinarian"
                }
                """;
        mockMvc.perform(post("/api/v1/biographies/biography/100/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ветеринар не найден"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void updateVetBiography_WhenSuccess_ReturnsUpdatedBiography() throws Exception {
        String requestJson = """
                {
                    "biography": "Updated biography for veterinarian"
                }
                """;
        mockMvc.perform(put("/api/v1/biographies/biography/3/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Биография успешно обновлена"))
                .andExpect(jsonPath("$.data.biography").value("Updated biography for veterinarian"));
    }

    @Test
    public void updateVetBiography_WhenBiographyNotFound_ReturnsNotFound() throws Exception {
        String requestJson = """
                {
                    "biography": "Updated biography non-existent"
                }
                """;
        mockMvc.perform(put("/api/v1/biographies/biography/100/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Биография не найдена"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void deleteVetBiography_WhenSuccess_ReturnsSuccessMessage() throws Exception {
        mockMvc.perform(delete("/api/v1/biographies/biography/4/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Биография успешно удалена"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void deleteVetBiography_WhenNotFound_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/biographies/biography/100/delete"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Биография не найдена"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }
}
