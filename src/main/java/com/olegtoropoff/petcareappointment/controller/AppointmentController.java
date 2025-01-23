package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.dto.AppointmentDto;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.Pet;
import com.olegtoropoff.petcareappointment.rabbitmq.RabbitMQProducer;
import com.olegtoropoff.petcareappointment.request.AppointmentUpdateRequest;
import com.olegtoropoff.petcareappointment.request.BookAppointmentRequest;
import com.olegtoropoff.petcareappointment.response.CustomApiResponse;
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

/**
 * REST controller for managing appointment-related operations.
 * Handles HTTP requests for booking, updating, deleting, and querying appointments.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.APPOINTMENTS)
public class AppointmentController {
    private final IAppointmentService appointmentService;
    private final RabbitMQProducer rabbitMQProducer;

    /**
     * Books a new appointment and sends an event message via RabbitMQ.
     *
     * @param request the appointment request details.
     * @param senderId the ID of the user booking the appointment.
     * @param recipientId the ID of the recipient of the appointment.
     * @return a response indicating the success or failure of the operation.
     */
    @PostMapping(UrlMapping.BOOK_APPOINTMENT)
    public ResponseEntity<CustomApiResponse> bookAppointment(
            @RequestBody BookAppointmentRequest request,
            @RequestParam Long senderId,
            @RequestParam Long recipientId) {
        try {
            Appointment appointment = appointmentService.createAppointment(request, senderId, recipientId);
            rabbitMQProducer.sendMessage("AppointmentBookedEvent:" + appointment.getVeterinarian().getId());
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.APPOINTMENT_BOOKED_SUCCESS, appointment));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Updates an existing appointment.
     *
     * @param id the ID of the appointment to update.
     * @param request the updated appointment details.
     * @return a response indicating the success or failure of the operation.
     */
    @PutMapping(UrlMapping.UPDATE_APPOINTMENT)
    public ResponseEntity<CustomApiResponse> updateAppointment(
            @PathVariable Long id,
            @RequestBody AppointmentUpdateRequest request) {
        try {
            Appointment appointment = appointmentService.updateAppointment(id, request);
            return ResponseEntity.ok((new CustomApiResponse(FeedBackMessage.APPOINTMENT_UPDATE_SUCCESS, appointment)));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new CustomApiResponse(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Adds a new pet to an existing appointment.
     *
     * @param id the ID of the appointment.
     * @param pet the pet details to add.
     * @return a response indicating the success or failure of the operation.
     */
    @PutMapping(UrlMapping.ADD_PET_APPOINTMENT)
    public ResponseEntity<CustomApiResponse> addPetForAppointment(
            @PathVariable Long id,
            @RequestBody Pet pet) {
        try {
            Appointment appointment = appointmentService.addPetForAppointment(id, pet);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.PET_ADDED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new CustomApiResponse(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Retrieves all appointments with pagination and optional search functionality.
     *
     * @param page   the page number (default: 0)
     * @param size   the number of records per page (default: 10)
     * @param search an optional search term to filter appointments by patient email, veterinarian email,
     *               or appointment number (default: empty string for no filtering)
     * @return a paginated list of appointments or filtered results based on the search term
     */
    @GetMapping(UrlMapping.ALL_APPOINTMENT)
    public ResponseEntity<CustomApiResponse> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appointmentDate", "appointmentTime"));
            Page<AppointmentDto> appointmentPage;
            if (search.isEmpty()) {
                appointmentPage = appointmentService.getAllAppointments(pageable);
            } else {
                appointmentPage = appointmentService.searchAppointments(search, pageable);
            }
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.APPOINTMENTS_FOUND, appointmentPage));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                    new CustomApiResponse(FeedBackMessage.ERROR, null)
            );
        }
    }

    /**
     * Retrieves an appointment by its ID.
     *
     * @param id the ID of the appointment.
     * @return the appointment details or an error message.
     */
    @GetMapping(UrlMapping.GET_APPOINTMENT_BY_ID)
    public ResponseEntity<CustomApiResponse> getAppointmentById(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.APPOINTMENT_FOUND, appointment));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Deletes an appointment by its ID.
     *
     * @param id the ID of the appointment.
     * @return a response indicating the success or failure of the operation.
     */
    @DeleteMapping(UrlMapping.DELETE_APPOINTMENT)
    public ResponseEntity<CustomApiResponse> deleteAppointmentById(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.APPOINTMENT_DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Cancels an appointment and sends an event message via RabbitMQ.
     *
     * @param id the ID of the appointment to cancel.
     * @return the updated appointment details or an error message.
     */
    @PutMapping(UrlMapping.CANCEL_APPOINTMENT)
    public ResponseEntity<CustomApiResponse> cancelAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.cancelAppointment(id);
            rabbitMQProducer.sendMessage("AppointmentCanceledEvent:" +  appointment.getVeterinarian().getId() + "#" + appointment.getAppointmentNo());
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.APPOINTMENT_CANCELLED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Approves an appointment and sends an event message via RabbitMQ.
     *
     * @param id the ID of the appointment to approve.
     * @return the updated appointment details or an error message.
     */
    @PutMapping(UrlMapping.APPROVE_APPOINTMENT)
    public ResponseEntity<CustomApiResponse> approveAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.approveAppointment(id);
            rabbitMQProducer.sendMessage("AppointmentApprovedEvent:" + appointment.getPatient().getId());
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.APPOINTMENT_APPROVED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Declines an appointment and sends an event message via RabbitMQ.
     *
     * @param id the ID of the appointment to decline.
     * @return the updated appointment details or an error message.
     */
    @PutMapping(UrlMapping.DECLINE_APPOINTMENT)
    public ResponseEntity<CustomApiResponse> declineAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.declineAppointment(id);
            rabbitMQProducer.sendMessage("AppointmentDeclinedEvent:" + appointment.getPatient().getId());
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.APPOINTMENT_DECLINED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Counts the total number of appointments in the system.
     *
     * @return the count of appointments.
     */
    @GetMapping(UrlMapping.COUNT_APPOINTMENT)
    public long countAppointments() {
        return appointmentService.countAppointments();
    }

    /**
     * Retrieves a summary of appointments grouped by status.
     *
     * @return a list of appointment summaries.
     */
    @GetMapping(UrlMapping.APPOINTMENT_SUMMARY)
    public ResponseEntity<CustomApiResponse> getAppointmentSummary() {
        try {
            List<Map<String, Object>> summary = appointmentService.getAppointmentSummary();
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.SUCCESS, summary));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new CustomApiResponse(FeedBackMessage.ERROR + e.getMessage(), null));
        }
    }
}
