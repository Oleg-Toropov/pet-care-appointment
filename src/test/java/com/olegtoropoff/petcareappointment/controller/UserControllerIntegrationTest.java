package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.config.TestConfig;
import com.olegtoropoff.petcareappointment.rabbitmq.RabbitMQProducer;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static com.olegtoropoff.petcareappointment.utils.UrlMapping.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/clean_database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/test_pet_care_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Import(TestConfig.class)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @Test
    public void testGetById_ValidUserId_ReturnsUser() throws Exception {
        mockMvc.perform(get(USERS + GET_USER_BY_ID, 4L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.USER_FOUND))
                .andExpect(jsonPath("$.data.id").value(4));
    }

    @Test
    public void testGetById_InvalidUserId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get(USERS + GET_USER_BY_ID, 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.USER_NOT_FOUND))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void testDeleteById_ValidUserId_ReturnsSuccess() throws Exception {
        mockMvc.perform(delete(USERS + DELETE_USER_BY_ID, 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.DELETE_USER_SUCCESS))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void testDeleteById_InvalidUserId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete(USERS + DELETE_USER_BY_ID, 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.USER_NOT_FOUND))
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
        mockMvc.perform(put(USERS + UPDATE_USER, 4L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.USER_UPDATE_SUCCESS))
                .andExpect(jsonPath("$.data.id").value(4))
                .andExpect(jsonPath("$.data.firstName").value("Updatedname"))
                .andExpect(jsonPath("$.data.lastName").value("Updatedlastname"))
                .andExpect(jsonPath("$.data.phoneNumber").value("89124000000"));
    }

    @Test
    public void testUpdate_InvalidData_ReturnsBadRequest() throws Exception {
        String updateRequestJson = """
                {
                    "firstName": "UpdatedName",
                    "lastName": "UpdatedLastName",
                    "phoneNumber": "891000000"
                }
                """;
        mockMvc.perform(put(USERS + UPDATE_USER, 4L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.INVALID_PHONE_FORMAT))
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
        mockMvc.perform(put(USERS + UPDATE_USER, 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.USER_NOT_FOUND))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void testRegister_ValidRequest_ReturnsSuccess() throws Exception {
        String registrationRequestJson = """
                {
                    "firstName": "Иван",
                    "lastName": "Иванов",
                    "gender": "Male",
                    "phoneNumber": "89124000000",
                    "email": "test@gmail.com",
                    "password": "TestPassword123",
                    "userType": "VET",
                    "specialization": "Хирург"
                }
                """;
        doNothing().when(rabbitMQProducer).sendMessage(anyString());
        mockMvc.perform(post(USERS + REGISTER_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.CREATE_USER_SUCCESS))
                .andExpect(jsonPath("$.data.firstName").value("Иван"))
                .andExpect(jsonPath("$.data.lastName").value("Иванов"))
                .andExpect(jsonPath("$.data.gender").value("Male"))
                .andExpect(jsonPath("$.data.phoneNumber").value("89124000000"))
                .andExpect(jsonPath("$.data.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.data.userType").value("VET"))
                .andExpect(jsonPath("$.data.specialization").value("Хирург"));
    }

    @Test
    public void testRegister_UserAlreadyExists_ReturnsConflict() throws Exception {
        String registrationRequestJson = """
                {
                    "firstName": "Иван",
                    "lastName": "Иванов",
                    "gender": "Male",
                    "phoneNumber": "89124000000",
                    "email": "dmitry@gmail.com",
                    "password": "TestPassword123",
                    "userType": "VET",
                    "specialization": "Хирург"
                }
                """;
        mockMvc.perform(post(USERS + REGISTER_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationRequestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.USER_ALREADY_EXISTS))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void testRegister_InvalidData_ReturnsBadRequest() throws Exception {
        String registrationRequestJson = """
                {
                    "firstName": "Иван",
                    "lastName": "Иванов",
                    "gender": "Male",
                    "phoneNumber": "89124000000",
                    "email": "test@gmail.com",
                    "password": "TestPassword",
                    "userType": "VET",
                    "specialization": "Хирург"
                }
                """;
        mockMvc.perform(post(USERS + REGISTER_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.INVALID_PASSWORD_FORMAT))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void testChangePassword_ValidRequest_ReturnsSuccess() throws Exception {
        String changePasswordRequestJson = """
                {
                    "currentPassword": "Password12345",
                    "newPassword": "NewPassword123",
                    "confirmNewPassword": "NewPassword123"
                }
                """;
        mockMvc.perform(put(USERS + CHANGE_PASSWORD, 4L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePasswordRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.PASSWORD_CHANGE_SUCCESS))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void testChangePassword_CurrentPassword_Wrong_ReturnsBadRequest() throws Exception {
        String changePasswordRequestJson = """
                {
                    "currentPassword": "Password77444",
                    "newPassword": "NewPassword123",
                    "confirmNewPassword": "NewPassword123"
                }
                """;
        mockMvc.perform(put(USERS + CHANGE_PASSWORD, 4L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePasswordRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.CURRENT_PASSWORD_WRONG))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void testChangePassword_UserNotFound_ReturnsNotFound() throws Exception {
        String changePasswordRequestJson = """
                {
                    "currentPassword": "Password12345",
                    "newPassword": "NewPassword123",
                    "confirmNewPassword": "NewPassword321"
                }
                """;
        mockMvc.perform(put(USERS + CHANGE_PASSWORD, 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePasswordRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.USER_NOT_FOUND))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void testCountVeterinarians_ReturnsCorrectCount() throws Exception {
        mockMvc.perform(get(USERS + COUNT_ALL_VETERINARIANS))
                .andExpect(status().isOk())
                .andExpect(content().string(anyOf(is("5"), is("6"))));
    }

    @Test
    public void testCountPatients_ReturnsCorrectCount() throws Exception {
        mockMvc.perform(get(USERS + COUNT_ALL_PATIENTS))
                .andExpect(status().isOk())
                .andExpect(content().string(anyOf(is("4"), is("5"))));
    }

    @Test
    public void testCountUsers_ReturnsCorrectCount() throws Exception {
        mockMvc.perform(get(USERS + COUNT_ALL_USERS))
                .andExpect(status().isOk())
                .andExpect(content().string(anyOf(is("10"), is("11"), is("12"))));
    }

    @Test
    public void testAggregateUserByMonthAndType_ReturnsAggregatedData() throws Exception {
        mockMvc.perform(get(USERS + AGGREGATE_USERS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.RESOURCE_FOUND))
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.January").exists())
                .andExpect(jsonPath("$.data.January.VET").isNumber())
                .andExpect(jsonPath("$.data.January.PATIENT").isNumber());
    }

    @Test
    public void testGetAggregateUsersByEnabledStatus_ReturnsAggregatedData() throws Exception {
        mockMvc.perform(get(USERS + AGGREGATE_USERS_BY_STATUS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.RESOURCE_FOUND))
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.Enabled").exists())
                .andExpect(jsonPath("$.data.Non-Enabled").exists())
                .andExpect(jsonPath("$.data.Enabled.VET").isNumber())
                .andExpect(jsonPath("$.data.Enabled.PATIENT").isNumber())
                .andExpect(jsonPath("$.data.Non-Enabled.VET").isNumber())
                .andExpect(jsonPath("$.data.Non-Enabled.PATIENT").isNumber());
    }

    @Test
    public void testLockUserAccount_ReturnsSuccess() throws Exception {
        mockMvc.perform(put(USERS + LOCK_USER_ACCOUNT, 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.LOCKED_ACCOUNT_SUCCESS))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    public void testUnLockUserAccount_ReturnsSuccess() throws Exception {
        mockMvc.perform(put(USERS + UNLOCK_USER_ACCOUNT, 6L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.UNLOCKED_ACCOUNT_SUCCESS))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }
}
