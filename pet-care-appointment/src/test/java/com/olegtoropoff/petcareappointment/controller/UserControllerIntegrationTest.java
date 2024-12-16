package com.olegtoropoff.petcareappointment.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/test_pet_care_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetById_ValidUserId_ReturnsUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/user/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Пользователь найден"))
                .andExpect(jsonPath("$.data.id").value(4));
    }

    @Test
    public void testGetById_InvalidUserId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/user/100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Извините, пользователь не найден"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void testDeleteById_ValidUserId_ReturnsSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/users/user/5/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Учетная запись пользователя успешно удалена"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }


    @Test
    public void testDeleteById_InvalidUserId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/users/user/100/delete"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Извините, пользователь не найден"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }
}
