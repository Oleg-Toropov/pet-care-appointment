package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.response.ApiResponse;
import com.olegtoropoff.petcareappointment.service.veterinarian.IVeterinarianService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class VeterinarianControllerTest {

    @InjectMocks
    private VeterinarianController veterinarianController;

    @Mock
    private IVeterinarianService veterinarianService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    public void getAllVeterinarians_WhenSuccess_ReturnsVeterinarians() {
        UserDto vet1 = new UserDto();
        vet1.setId(1L);
        vet1.setFirstName("Иван");
        vet1.setLastName("Иванов");
        vet1.setSpecialization("VET");

        UserDto vet2 = new UserDto();
        vet2.setId(2L);
        vet2.setFirstName("Петр");
        vet2.setLastName("Петров");
        vet2.setSpecialization("VET");
        List<UserDto> veterinarians = List.of(vet1, vet2);

        when(veterinarianService.getAllVeterinariansWithDetails()).thenReturn(veterinarians);

        ResponseEntity<ApiResponse> response = veterinarianController.getAllVeterinarians();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.RESOURCE_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(veterinarians, response.getBody().getData());
    }

    @Test
    public void getAllVeterinarians_InternalErrorOccurs_ReturnsInternalServerError() {
        String errorMessage = FeedBackMessage.ERROR;
        doThrow(new RuntimeException(errorMessage))
                .when(veterinarianService).getAllVeterinariansWithDetails();

        ResponseEntity<ApiResponse> response = veterinarianController.getAllVeterinarians();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void searchVeterinariansForAppointment_WhenAvailable_ReturnsVeterinarians() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(10, 0);
        String specialization = "Хирург";
        UserDto vet = new UserDto();
        vet.setId(1L);
        vet.setFirstName("Иван");
        vet.setLastName("Иванов");
        vet.setSpecialization("VET");
        List<UserDto> availableVets = List.of(vet);

        when(veterinarianService.findAvailableVeterinariansForAppointments(specialization, date, time))
                .thenReturn(availableVets);

        ResponseEntity<ApiResponse> response = veterinarianController.searchVeterinariansForAppointment(date, time, specialization);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.RESOURCE_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(availableVets, response.getBody().getData());
    }

    @Test
    public void searchVeterinariansForAppointment_WhenNoneAvailable_ReturnsNotFound() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(10, 0);
        String specialization = "Хирург";

        when(veterinarianService.findAvailableVeterinariansForAppointments(specialization, date, time))
                .thenReturn(Collections.emptyList());

        ResponseEntity<ApiResponse> response = veterinarianController.searchVeterinariansForAppointment(date, time, specialization);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(FeedBackMessage.NO_VETS_AVAILABLE, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void searchVeterinariansForAppointment_WhenSpecializationNotFound_ReturnsNotFound() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(10, 0);
        String specialization = "Хирург";
        String errorMessage =  String.format(FeedBackMessage.SPECIALIZATION_NOT_FOUND, specialization);
        doThrow(new ResourceNotFoundException(errorMessage))
                .when(veterinarianService).findAvailableVeterinariansForAppointments(specialization, date, time);

        ResponseEntity<ApiResponse> response = veterinarianController.searchVeterinariansForAppointment(date, time, specialization);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void getAllSpecializations_WhenSuccess_ReturnsSpecializations() {
        List<String> specializations = List.of("Хирург", "Терапевт");
        when(veterinarianService.getSpecializations()).thenReturn(specializations);

        ResponseEntity<ApiResponse> response = veterinarianController.getAllSpecializations();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.RESOURCE_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(specializations, response.getBody().getData());
    }

    @Test
    public void getAllSpecializations_InternalErrorOccurs_ReturnsInternalServerError() {
        String errorMessage = FeedBackMessage.ERROR;
        doThrow(new RuntimeException(errorMessage))
                .when(veterinarianService).getSpecializations();

        ResponseEntity<ApiResponse> response = veterinarianController.getAllSpecializations();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void aggregateVetsBySpecialization_WhenSuccess_ReturnsAggregatedData() {
        Map<String, Object> aggregatedData = Map.of("Хирург", 5, "Терапевт", 3);
        List<Map<String, Object>> result = List.of(aggregatedData);
        when(veterinarianService.aggregateVetsBySpecialization()).thenReturn(result);

        ResponseEntity<List<Map<String, Object>>> response = veterinarianController.aggregateVetsBySpecialization();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(result, response.getBody());
    }

    @Test
    public void getAvailableTimeForBookAppointment_WhenSuccess_ReturnsAvailableTimes() {
        Long vetId = 1L;
        LocalDate date = LocalDate.now();
        List<LocalTime> availableTimes = List.of(LocalTime.of(10, 0), LocalTime.of(11, 0));
        when(veterinarianService.getAvailableTimeForBookAppointment(vetId, date)).thenReturn(availableTimes);

        ResponseEntity<ApiResponse> response = veterinarianController.getAvailableTimeForBookAppointment(vetId, date);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.AVAILABLE_TIME_FOR_APPOINTMENT_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(availableTimes, response.getBody().getData());
    }

    @Test
    public void getAvailableTimeForBookAppointment_WhenExceptionOccurs_ReturnsInternalServerError() {
        Long vetId = 1L;
        LocalDate date = LocalDate.now();
        String errorMessage = FeedBackMessage.ERROR;
        doThrow(new RuntimeException(errorMessage))
                .when(veterinarianService).getAvailableTimeForBookAppointment(vetId, date);

        ResponseEntity<ApiResponse> response = veterinarianController.getAvailableTimeForBookAppointment(vetId, date);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }
}