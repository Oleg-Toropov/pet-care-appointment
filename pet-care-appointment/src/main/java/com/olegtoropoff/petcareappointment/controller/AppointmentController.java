package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.dto.AppointmentDto;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.Pet;
import com.olegtoropoff.petcareappointment.rabbitmq.RabbitMQProducer;
import com.olegtoropoff.petcareappointment.request.AppointmentUpdateRequest;
import com.olegtoropoff.petcareappointment.request.BookAppointmentRequest;
import com.olegtoropoff.petcareappointment.response.ApiResponse;
import com.olegtoropoff.petcareappointment.service.appointment.IAppointmentService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.APPOINTMENTS)
public class AppointmentController {
    private final IAppointmentService appointmentService;
    private final RabbitMQProducer rabbitMQProducer;


    @PostMapping(UrlMapping.BOOK_APPOINTMENT)
    public ResponseEntity<ApiResponse> bookAppointment(
            @RequestBody BookAppointmentRequest request,
            @RequestParam Long senderId,
            @RequestParam Long recipientId) {
        try {
            Appointment appointment = appointmentService.createAppointment(request, senderId, recipientId);
            rabbitMQProducer.sendMessage("AppointmentBookedEvent:" + appointment.getVeterinarian().getId());
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.APPOINTMENT_BOOKED_SUCCESS, appointment));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @PutMapping(UrlMapping.UPDATE_APPOINTMENT)
    public ResponseEntity<ApiResponse> updateAppointment(
            @PathVariable Long id,
            @RequestBody AppointmentUpdateRequest request) {
        try {
            Appointment appointment = appointmentService.updateAppointment(id, request);
            return ResponseEntity.ok((new ApiResponse(FeedBackMessage.APPOINTMENT_UPDATE_SUCCESS, appointment)));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @PutMapping(UrlMapping.ADD_PET_APPOINTMENT)
    public ResponseEntity<ApiResponse> addPetForAppointment(
            @PathVariable Long id,
            @RequestBody Pet pet) {
        try {
            Appointment appointment = appointmentService.addPetForAppointment(id, pet);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.PET_ADDED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @GetMapping(UrlMapping.ALL_APPOINTMENT)
    public ResponseEntity<ApiResponse> getAllAppointments(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appointmentDate", "appointmentTime"));
            Page<AppointmentDto> appointmentPage = appointmentService.getAllAppointments(pageable);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.APPOINTMENTS_FOUND, appointmentPage));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @GetMapping(UrlMapping.GET_APPOINTMENT_BY_ID)
    public ResponseEntity<ApiResponse> getAppointmentById(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.APPOINTMENT_FOUND, appointment));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @DeleteMapping(UrlMapping.DELETE_APPOINTMENT)
    public ResponseEntity<ApiResponse> deleteAppointmentById(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.APPOINTMENT_DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @PutMapping(UrlMapping.CANCEL_APPOINTMENT)
    public ResponseEntity<ApiResponse> cancelAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.cancelAppointment(id);
            rabbitMQProducer.sendMessage("AppointmentCanceledEvent:" +  appointment.getVeterinarian().getId() + "#" + appointment.getAppointmentNo());
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.APPOINTMENT_CANCELLED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @PutMapping(UrlMapping.APPROVE_APPOINTMENT)
    public ResponseEntity<ApiResponse> approveAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.approveAppointment(id);
            rabbitMQProducer.sendMessage("AppointmentApprovedEvent:" + appointment.getPatient().getId());
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.APPOINTMENT_APPROVED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @PutMapping(UrlMapping.DECLINE_APPOINTMENT)
    public ResponseEntity<ApiResponse> declineAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.declineAppointment(id);
            rabbitMQProducer.sendMessage("AppointmentDeclinedEvent:" + appointment.getPatient().getId());
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.APPOINTMENT_DECLINED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @GetMapping(UrlMapping.COUNT_APPOINTMENT)
    public long countAppointments() {
        return appointmentService.countAppointments();
    }

    @GetMapping(UrlMapping.APPOINTMENT_SUMMARY)
    public ResponseEntity<ApiResponse> getAppointmentSummary() {
        try {
            List<Map<String, Object>> summary = appointmentService.getAppointmentSummary();
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.SUCCESS, summary));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(FeedBackMessage.ERROR + e.getMessage(), null));
        }
    }
}
