package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.response.ApiResponse;
import com.olegtoropoff.petcareappointment.service.patient.IPatientService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class PatientControllerTest {

    @InjectMocks
    private PatientController patientController;

    @Mock
    private IPatientService patientService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    public void getAllPatients_WhenPatientsExist_ReturnsResourceFound() {
        UserDto patient1 = new UserDto();
        patient1.setId(1L);
        patient1.setFirstName("Иван");
        patient1.setLastName("Иванов");

        UserDto patient2 = new UserDto();
        patient2.setId(2L);
        patient2.setFirstName("Петр");
        patient2.setLastName("Петров");

        List<UserDto> patients = Arrays.asList(patient1, patient2);

        when(patientService.getPatients()).thenReturn(patients);

        ResponseEntity<ApiResponse> response = patientController.getAllPatients();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Ресурс найден", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(patients, response.getBody().getData());
    }

    @Test
    public void getAllPatients_WhenNoPatients_ReturnsEmptyList() {
        List<UserDto> emptyPatients = List.of();
        when(patientService.getPatients()).thenReturn(emptyPatients);

        ResponseEntity<ApiResponse> response = patientController.getAllPatients();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.RESOURCE_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(emptyPatients, response.getBody().getData());
    }
}