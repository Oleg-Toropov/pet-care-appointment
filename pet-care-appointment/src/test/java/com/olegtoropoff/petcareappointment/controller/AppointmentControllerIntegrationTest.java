package com.olegtoropoff.petcareappointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olegtoropoff.petcareappointment.config.TestConfig;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.rabbitmq.RabbitMQProducer;
import com.olegtoropoff.petcareappointment.request.AppointmentUpdateRequest;
import com.olegtoropoff.petcareappointment.request.BookAppointmentRequest;
import com.olegtoropoff.petcareappointment.model.Pet;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.JwtTestUtils;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
class AppointmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTestUtils jwtTestUtils;

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void bookAppointment_ReturnsSuccessResponse() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(LocalDate.now().plusDays(1));
        appointment.setAppointmentTime(LocalTime.of(19, 0));

        Pet pet = new Pet();
        pet.setName("Рекс");

        BookAppointmentRequest request = new BookAppointmentRequest();
        request.setAppointment(appointment);
        request.setPets(List.of(pet));

        doNothing().when(rabbitMQProducer).sendMessage(anyString());

        mockMvc.perform(post(APPOINTMENTS + BOOK_APPOINTMENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("senderId", "5")
                        .param("recipientId", "9")
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", jwtTestUtils.generateDefaultToken("maria@gmail.com", 3L, "ROLE_PATIENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.APPOINTMENT_BOOKED_SUCCESS)))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void bookAppointment_ThrowsResourceNotFound() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(LocalDate.now().plusDays(1));
        appointment.setAppointmentTime(LocalTime.of(19, 0));

        Pet pet = new Pet();
        pet.setName("Рекс");

        BookAppointmentRequest request = new BookAppointmentRequest();
        request.setAppointment(appointment);
        request.setPets(List.of(pet));

        doNothing().when(rabbitMQProducer).sendMessage(anyString());

        mockMvc.perform(post(APPOINTMENTS + BOOK_APPOINTMENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("senderId", "3")
                        .param("recipientId", "100")
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", jwtTestUtils.generateDefaultToken("maria@gmail.com", 3L, "ROLE_PATIENT")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.SENDER_RECIPIENT_NOT_FOUND)))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void bookAppointment_ThrowsIllegalStateException() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(LocalDate.now().plusDays(1));
        appointment.setAppointmentTime(LocalTime.of(19, 0));

        Pet pet = new Pet();
        pet.setName("Рекс");

        BookAppointmentRequest request = new BookAppointmentRequest();
        request.setAppointment(appointment);
        request.setPets(List.of(pet));

        doNothing().when(rabbitMQProducer).sendMessage(anyString());

        mockMvc.perform(post(APPOINTMENTS + BOOK_APPOINTMENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("senderId", "2")
                        .param("recipientId", "7")
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", jwtTestUtils.generateDefaultToken("alexey@gmail.com", 2L, "ROLE_PATIENT")))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.TOO_MANY_ACTIVE_APPOINTMENTS)))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void updateAppointment_ReturnsSuccessResponse() throws Exception {
        AppointmentUpdateRequest request = new AppointmentUpdateRequest();
        request.setAppointmentDate(String.valueOf(LocalDate.now().plusDays(1)));
        request.setAppointmentTime("19:00:00");

        mockMvc.perform(put(APPOINTMENTS + UPDATE_APPOINTMENT, 8L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.APPOINTMENT_UPDATE_SUCCESS)))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void updateAppointment_ThrowsIllegalStateException() throws Exception {
        AppointmentUpdateRequest request = new AppointmentUpdateRequest();
        request.setAppointmentDate(String.valueOf(LocalDate.now().plusDays(1)));
        request.setAppointmentTime("19:00:00");

        mockMvc.perform(put(APPOINTMENTS + UPDATE_APPOINTMENT, 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.APPOINTMENT_UPDATE_NOT_ALLOWED)))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void updateAppointment_ThrowsResourceNotFoundException() throws Exception {
        AppointmentUpdateRequest request = new AppointmentUpdateRequest();
        request.setAppointmentDate(String.valueOf(LocalDate.now().plusDays(1)));
        request.setAppointmentTime("19:00:00");

        mockMvc.perform(put(APPOINTMENTS + UPDATE_APPOINTMENT, 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.APPOINTMENT_NOT_FOUND)))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void addPetForAppointment_ReturnsSuccessResponse() throws Exception {
        Pet pet = new Pet();
        pet.setName("Барсик");

        mockMvc.perform(put(APPOINTMENTS + ADD_PET_APPOINTMENT, 8L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.PET_ADDED_SUCCESS)))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void addPetForAppointment_ThrowsResourceNotFoundException() throws Exception {
        Pet pet = new Pet();
        pet.setName("Барсик");

        mockMvc.perform(put(APPOINTMENTS + ADD_PET_APPOINTMENT, 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.APPOINTMENT_NOT_FOUND)))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void addPetForAppointment_ThrowsIllegalStateException() throws Exception {
        Pet pet = new Pet();
        pet.setName("Барсик");

        mockMvc.perform(put(APPOINTMENTS + ADD_PET_APPOINTMENT, 9L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.OPERATION_NOT_ALLOWED)))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getAllAppointments_ReturnsPagedResponse() throws Exception {
        mockMvc.perform(get(APPOINTMENTS + ALL_APPOINTMENT)
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.APPOINTMENTS_FOUND)))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void getAppointmentById_ReturnsSuccessResponse() throws Exception {
        mockMvc.perform(get(APPOINTMENTS + GET_APPOINTMENT_BY_ID, 8L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.APPOINTMENT_FOUND)))
                .andExpect(jsonPath("$.data.id", is(8)));
    }

    @Test
    void getAppointmentById_ThrowsNotFoundException() throws Exception {
        mockMvc.perform(get(APPOINTMENTS + GET_APPOINTMENT_BY_ID, 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.APPOINTMENT_NOT_FOUND)));
    }

    @Test
    void deleteAppointmentById_ReturnsSuccessResponse() throws Exception {
        mockMvc.perform(delete(APPOINTMENTS + DELETE_APPOINTMENT, 4L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.APPOINTMENT_DELETE_SUCCESS)));
    }

    @Test
    void deleteAppointmentById_ThrowsNotFoundException() throws Exception {
        mockMvc.perform(delete(APPOINTMENTS + DELETE_APPOINTMENT, 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.APPOINTMENT_NOT_FOUND)));
    }

    @Test
    void cancelAppointment_ReturnsSuccessResponse() throws Exception {
        mockMvc.perform(put(APPOINTMENTS + CANCEL_APPOINTMENT, 12L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.APPOINTMENT_CANCELLED_SUCCESS)));
    }

    @Test
    void cancelAppointment_ThrowsIllegalStateException() throws Exception {
        mockMvc.perform(put(APPOINTMENTS + CANCEL_APPOINTMENT, 11L))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.APPOINTMENT_UPDATE_NOT_ALLOWED)));
    }

    @Test
    void approveAppointment_ReturnsSuccessResponse() throws Exception {
        mockMvc.perform(put(APPOINTMENTS + APPROVE_APPOINTMENT, 13L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.APPOINTMENT_APPROVED_SUCCESS)));
    }

    @Test
    void approveAppointment_ThrowsIllegalStateException() throws Exception {
        mockMvc.perform(put(APPOINTMENTS + APPROVE_APPOINTMENT, 6L))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.OPERATION_NOT_ALLOWED)));
    }

    @Test
    void declineAppointment_ReturnsSuccessResponse() throws Exception {
        mockMvc.perform(put(APPOINTMENTS + DECLINE_APPOINTMENT, 14L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.APPOINTMENT_DECLINED_SUCCESS)));
    }

    @Test
    void declineAppointment_ThrowsIllegalStateException() throws Exception {
        mockMvc.perform(put(APPOINTMENTS + DECLINE_APPOINTMENT, 6L))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.OPERATION_NOT_ALLOWED)));
    }

    @Test
    void countAppointments_ReturnsCount() throws Exception {
        mockMvc.perform(get(APPOINTMENTS + COUNT_APPOINTMENT))
                .andExpect(status().isOk())
                .andExpect(content().string(anyOf(is("13"), is("14"), is("15"))));
    }

    @Test
    void getAppointmentSummary_ReturnsSummary() throws Exception {
        mockMvc.perform(get(APPOINTMENTS + APPOINTMENT_SUMMARY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.SUCCESS)))
                .andExpect(jsonPath("$.data").isArray());
    }
}
