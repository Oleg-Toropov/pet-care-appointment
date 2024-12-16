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

    @Test
    public void testUpdate_ValidUserId_ReturnsUpdatedUser() throws Exception {
        String updateRequestJson = """
                {
                    "firstName": "UpdatedName",
                    "lastName": "UpdatedLastName",
                    "phoneNumber": "89124000000"
                }
                """;

        mockMvc.perform(put("/api/v1/users/user/4/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Пользователь успешно обновлен"))
                .andExpect(jsonPath("$.data.id").value(4))
                .andExpect(jsonPath("$.data.firstName").value("Updatedname"))
                .andExpect(jsonPath("$.data.lastName").value("Updatedlastname"))
                .andExpect(jsonPath("$.data.phoneNumber").value("89124000000"));
    }

    @Test
    public void testUpdate_InvalidFirstName_ReturnsBadRequest() throws Exception {
        String updateRequestJson = """
                {
                    "firstName": "Updated+Name",
                    "lastName": "UpdatedLastName",
                    "phoneNumber": "89124000000"
                }
                """;

        mockMvc.perform(put("/api/v1/users/user/4/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Упс! Кажется, в имени или фамилии ошибка. Проверьте, что данные введены правильно"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void testUpdate_InvalidPhoneNumber_ReturnsBadRequest() throws Exception {
        String updateRequestJson = """
                {
                    "firstName": "UpdatedName",
                    "lastName": "UpdatedLastName",
                    "phoneNumber": "891000000"
                }
                """;

        mockMvc.perform(put("/api/v1/users/user/4/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Упс! Кажется, в номере телефона ошибка. Проверьте, что номер телефона введён правильно."))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void testUpdate_InvalidUserId_ReturnsNotFound() throws Exception {
        String updateRequestJson = """
                {
                    "firstName": "UpdatedName",
                    "lastName": "UpdatedLastName",
                    "phoneNumber": "89124000000"
                }
                """;

        mockMvc.perform(put("/api/v1/users/user/100/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Извините, пользователь не найден"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

}
