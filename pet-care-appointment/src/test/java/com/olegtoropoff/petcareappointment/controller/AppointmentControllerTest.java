package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.dto.AppointmentDto;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.Patient;
import com.olegtoropoff.petcareappointment.model.Pet;
import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.rabbitmq.RabbitMQProducer;
import com.olegtoropoff.petcareappointment.request.AppointmentUpdateRequest;
import com.olegtoropoff.petcareappointment.request.BookAppointmentRequest;
import com.olegtoropoff.petcareappointment.response.ApiResponse;
import com.olegtoropoff.petcareappointment.service.appointment.IAppointmentService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@Tag("unit")
class AppointmentControllerTest {

    @InjectMocks
    private AppointmentController appointmentController;

    @Mock
    private IAppointmentService appointmentService;

    @Mock
    private RabbitMQProducer rabbitMQProducer;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void bookAppointment_ReturnsSuccessResponse() {
        BookAppointmentRequest request = new BookAppointmentRequest();
        Appointment appointment = new Appointment();
        Veterinarian vet = new Veterinarian();
        vet.setId(1L);
        appointment.setId(1L);
        appointment.setVeterinarian(vet);
        Long senderId = 1L;
        Long recipientId = 2L;

        when(appointmentService.createAppointment(request, senderId, recipientId)).thenReturn(appointment);
        doNothing().when(rabbitMQProducer).sendMessage(anyString());

        ResponseEntity<ApiResponse> response = appointmentController.bookAppointment(request, senderId, recipientId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.APPOINTMENT_BOOKED_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        verify(rabbitMQProducer, times(1)).sendMessage("AppointmentBookedEvent:1");
    }

    @Test
    void bookAppointment_ThrowsResourceNotFoundException() {
        BookAppointmentRequest request = new BookAppointmentRequest();
        Long senderId = 1L;
        Long recipientId = 2L;
        String errorMessage = FeedBackMessage.SENDER_RECIPIENT_NOT_FOUND;

        when(appointmentService.createAppointment(request, senderId, recipientId))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        ResponseEntity<ApiResponse> response = appointmentController.bookAppointment(request, senderId, recipientId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(rabbitMQProducer, times(0)).sendMessage(anyString());
    }

    @Test
    void bookAppointment_ThrowsIllegalStateException() {
        BookAppointmentRequest request = new BookAppointmentRequest();
        Long senderId = 1L;
        Long recipientId = 1L;
        String errorMessage = FeedBackMessage.VET_APPOINTMENT_NOT_ALLOWED;

        when(appointmentService.createAppointment(request, senderId, recipientId))
                .thenThrow(new IllegalStateException(errorMessage));

        ResponseEntity<ApiResponse> response = appointmentController.bookAppointment(request, senderId, recipientId);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(rabbitMQProducer, times(0)).sendMessage(anyString());
    }

    @Test
    void bookAppointment_ThrowsGenericException() {
        BookAppointmentRequest request = new BookAppointmentRequest();
        Long senderId = 1L;
        Long recipientId = 2L;

        when(appointmentService.createAppointment(request, senderId, recipientId))
                .thenThrow(new RuntimeException(FeedBackMessage.ERROR));

        ResponseEntity<ApiResponse> response = appointmentController.bookAppointment(request, senderId, recipientId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(rabbitMQProducer, times(0)).sendMessage(anyString());
    }


    @Test
    void updateAppointment_ReturnsSuccessResponse() {
        AppointmentUpdateRequest request = new AppointmentUpdateRequest();
        Appointment appointment = new Appointment();
        appointment.setId(1L);

        when(appointmentService.updateAppointment(1L, request)).thenReturn(appointment);

        ResponseEntity<ApiResponse> response = appointmentController.updateAppointment(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.APPOINTMENT_UPDATE_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void updateAppointment_ThrowsIllegalStateException() {
        AppointmentUpdateRequest request = new AppointmentUpdateRequest();
        String errorMessage = FeedBackMessage.APPOINTMENT_UPDATE_NOT_ALLOWED;

        when(appointmentService.updateAppointment(1L, request)).thenThrow(new IllegalStateException(errorMessage));

        ResponseEntity<ApiResponse> response = appointmentController.updateAppointment(1L, request);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void updateAppointment_ThrowsResourceNotFoundException() {
        AppointmentUpdateRequest request = new AppointmentUpdateRequest();
        String errorMessage = FeedBackMessage.APPOINTMENT_NOT_FOUND;

        when(appointmentService.updateAppointment(1L, request)).thenThrow(new ResourceNotFoundException(errorMessage));

        ResponseEntity<ApiResponse> response = appointmentController.updateAppointment(1L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void updateAppointment_ThrowsGenericException() {
        AppointmentUpdateRequest request = new AppointmentUpdateRequest();

        when(appointmentService.updateAppointment(1L, request)).thenThrow(new RuntimeException(FeedBackMessage.ERROR));

        ResponseEntity<ApiResponse> response = appointmentController.updateAppointment(1L, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void getAllAppointments_ReturnsPagedResponse() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<AppointmentDto> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(appointmentService.getAllAppointments(pageable)).thenReturn(page);

        ResponseEntity<ApiResponse> response = appointmentController.getAllAppointments(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.APPOINTMENTS_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void getAllAppointments_ThrowsGenericException() {
        doThrow(new RuntimeException(FeedBackMessage.ERROR))
                .when(appointmentService).getAllAppointments(any(Pageable.class));

        ResponseEntity<ApiResponse> response = appointmentController.getAllAppointments(0, 10);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void getAppointmentById_ReturnsSuccessResponse() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);

        when(appointmentService.getAppointmentById(1L)).thenReturn(appointment);

        ResponseEntity<ApiResponse> response = appointmentController.getAppointmentById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.APPOINTMENT_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void getAppointmentById_ThrowsResourceNotFoundException() {
        Long appointmentId = 1L;
        String errorMessage = FeedBackMessage.APPOINTMENT_NOT_FOUND;

        when(appointmentService.getAppointmentById(appointmentId))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        ResponseEntity<ApiResponse> response = appointmentController.getAppointmentById(appointmentId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void getAppointmentById_ThrowsGenericException() {
        Long appointmentId = 1L;
        String errorMessage = FeedBackMessage.ERROR;

        when(appointmentService.getAppointmentById(appointmentId))
                .thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<ApiResponse> response = appointmentController.getAppointmentById(appointmentId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void deleteAppointmentById_ReturnsSuccessResponse() {
        doNothing().when(appointmentService).deleteAppointment(1L);

        ResponseEntity<ApiResponse> response = appointmentController.deleteAppointmentById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.APPOINTMENT_DELETE_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void deleteAppointmentById_ThrowsResourceNotFoundException() {
        Long appointmentId = 1L;
        String errorMessage = FeedBackMessage.APPOINTMENT_NOT_FOUND;

        doThrow(new ResourceNotFoundException(errorMessage)).when(appointmentService).deleteAppointment(appointmentId);

        ResponseEntity<ApiResponse> response = appointmentController.deleteAppointmentById(appointmentId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void deleteAppointmentById_ThrowsGenericException() {
        Long appointmentId = 1L;
        String errorMessage = FeedBackMessage.ERROR;

        doThrow(new RuntimeException(errorMessage)).when(appointmentService).deleteAppointment(appointmentId);

        ResponseEntity<ApiResponse> response = appointmentController.deleteAppointmentById(appointmentId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void approveAppointment_ReturnsSuccessResponse() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        Patient patient = new Patient();
        patient.setId(1L);
        appointment.setPatient(patient);

        when(appointmentService.approveAppointment(1L)).thenReturn(appointment);
        doNothing().when(rabbitMQProducer).sendMessage(anyString());

        ResponseEntity<ApiResponse> response = appointmentController.approveAppointment(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.APPOINTMENT_APPROVED_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        verify(rabbitMQProducer, times(1)).sendMessage("AppointmentApprovedEvent:1");
    }

    @Test
    void approveAppointment_ThrowsIllegalStateException() {
        Long appointmentId = 1L;
        String errorMessage = FeedBackMessage.OPERATION_NOT_ALLOWED;

        when(appointmentService.approveAppointment(appointmentId)).thenThrow(new IllegalStateException(errorMessage));

        ResponseEntity<ApiResponse> response = appointmentController.approveAppointment(appointmentId);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void approveAppointment_ThrowsGenericException() {
        Long appointmentId = 1L;
        String errorMessage = FeedBackMessage.ERROR;

        when(appointmentService.approveAppointment(appointmentId)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<ApiResponse> response = appointmentController.approveAppointment(appointmentId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }


    @Test
    void countAppointments_ReturnsCount() {
        when(appointmentService.countAppointments()).thenReturn(5L);
        long count = appointmentController.countAppointments();
        assertEquals(5L, count);
    }

    @Test
    void getAppointmentSummary_ReturnsSummary() {
        List<Map<String, Object>> summary = Collections.singletonList(Map.of("key", "value"));

        when(appointmentService.getAppointmentSummary()).thenReturn(summary);

        ResponseEntity<ApiResponse> response = appointmentController.getAppointmentSummary();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(summary, response.getBody().getData());
    }

    @Test
    void getAppointmentSummary_ThrowsGenericException() {
        String errorMessage = FeedBackMessage.ERROR;

        when(appointmentService.getAppointmentSummary()).thenThrow(new RuntimeException(""));

        ResponseEntity<ApiResponse> response = appointmentController.getAppointmentSummary();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void declineAppointment_ReturnsSuccessResponse() {
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        Patient patient = new Patient();
        patient.setId(2L);
        appointment.setPatient(patient);

        when(appointmentService.declineAppointment(appointmentId)).thenReturn(appointment);
        doNothing().when(rabbitMQProducer).sendMessage(anyString());

        ResponseEntity<ApiResponse> response = appointmentController.declineAppointment(appointmentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.APPOINTMENT_DECLINED_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        verify(rabbitMQProducer, times(1)).sendMessage("AppointmentDeclinedEvent:2");
    }

    @Test
    void declineAppointment_ThrowsIllegalStateException() {
        Long appointmentId = 1L;
        String errorMessage = FeedBackMessage.OPERATION_NOT_ALLOWED;

        when(appointmentService.declineAppointment(appointmentId)).thenThrow(new IllegalStateException(errorMessage));

        ResponseEntity<ApiResponse> response = appointmentController.declineAppointment(appointmentId);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void declineAppointment_ThrowsGenericException() {
        Long appointmentId = 1L;
        String errorMessage = FeedBackMessage.ERROR;

        when(appointmentService.declineAppointment(appointmentId)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<ApiResponse> response = appointmentController.declineAppointment(appointmentId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void cancelAppointment_ReturnsSuccessResponse() {
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        Veterinarian vet = new Veterinarian();
        vet.setId(2L);
        appointment.setId(appointmentId);
        appointment.setVeterinarian(vet);
        appointment.setAppointmentNo("12345");

        when(appointmentService.cancelAppointment(appointmentId)).thenReturn(appointment);
        doNothing().when(rabbitMQProducer).sendMessage(anyString());

        ResponseEntity<ApiResponse> response = appointmentController.cancelAppointment(appointmentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.APPOINTMENT_CANCELLED_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        verify(rabbitMQProducer, times(1)).sendMessage("AppointmentCanceledEvent:2#12345");
    }

    @Test
    void cancelAppointment_ThrowsIllegalStateException() {
        Long appointmentId = 1L;
        String errorMessage = FeedBackMessage.APPOINTMENT_UPDATE_NOT_ALLOWED;

        when(appointmentService.cancelAppointment(appointmentId)).thenThrow(new IllegalStateException(errorMessage));

        ResponseEntity<ApiResponse> response = appointmentController.cancelAppointment(appointmentId);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void cancelAppointment_ThrowsGenericException() {
        Long appointmentId = 1L;
        String errorMessage = FeedBackMessage.ERROR;

        when(appointmentService.cancelAppointment(appointmentId)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<ApiResponse> response = appointmentController.cancelAppointment(appointmentId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void addPetForAppointment_ReturnsSuccessResponse() {
        Long appointmentId = 1L;
        Pet pet = new Pet();
        pet.setId(10L);
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);

        when(appointmentService.addPetForAppointment(appointmentId, pet)).thenReturn(appointment);

        ResponseEntity<ApiResponse> response = appointmentController.addPetForAppointment(appointmentId, pet);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.PET_ADDED_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(appointment, response.getBody().getData());
    }

    @Test
    void addPetForAppointment_ThrowsIllegalStateException() {
        Long appointmentId = 1L;
        Pet pet = new Pet();
        String errorMessage = FeedBackMessage.OPERATION_NOT_ALLOWED;

        when(appointmentService.addPetForAppointment(appointmentId, pet)).thenThrow(new IllegalStateException(errorMessage));

        ResponseEntity<ApiResponse> response = appointmentController.addPetForAppointment(appointmentId, pet);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void addPetForAppointment_ThrowsResourceNotFoundException() {
        Long appointmentId = 1L;
        Pet pet = new Pet();
        String errorMessage = FeedBackMessage.APPOINTMENT_NOT_FOUND;

        when(appointmentService.addPetForAppointment(appointmentId, pet)).thenThrow(new ResourceNotFoundException(errorMessage));

        ResponseEntity<ApiResponse> response = appointmentController.addPetForAppointment(appointmentId, pet);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }
    @Test
    void addPetForAppointment_ThrowsGenericException() {
        Long appointmentId = 1L;
        Pet pet = new Pet();
        String errorMessage = FeedBackMessage.ERROR;

        when(appointmentService.addPetForAppointment(appointmentId, pet)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<ApiResponse> response = appointmentController.addPetForAppointment(appointmentId, pet);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }
}