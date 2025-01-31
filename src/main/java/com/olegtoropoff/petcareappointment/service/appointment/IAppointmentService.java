package com.olegtoropoff.petcareappointment.service.appointment;

import com.olegtoropoff.petcareappointment.dto.AppointmentDto;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.Pet;
import com.olegtoropoff.petcareappointment.request.AppointmentUpdateRequest;
import com.olegtoropoff.petcareappointment.request.BookAppointmentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing appointments.
 * Provides methods to create, update, delete, and retrieve appointment-related data.
 */
public interface IAppointmentService {

    /**
     * Creates a new appointment and associates it with the sender (patient) and recipient (veterinarian).
     *
     * @param request   the appointment details and associated pets.
     * @param sender    the ID of the patient creating the appointment.
     * @param recipient the ID of the veterinarian receiving the appointment.
     * @return the created Appointment.
     */
    Appointment createAppointment(BookAppointmentRequest request, Long sender, Long recipient);

    /**
     * Updates an existing appointment with new details.
     *
     * @param id      the ID of the appointment to update.
     * @param request the updated details of the appointment.
     * @return the updated Appointment.
     */
    AppointmentDto updateAppointment(Long id, AppointmentUpdateRequest request);

    /**
     * Adds a new pet to an existing appointment.
     *
     * @param id  the ID of the appointment.
     * @param pet the pet to be added to the appointment.
     * @return the updated Appointment.
     */
    AppointmentDto addPetForAppointment(Long id, Pet pet);

    /**
     * Retrieves a paginated list of all appointments.
     *
     * @param pageable the pagination details.
     * @return a paginated list of AppointmentDto objects.
     */
    Page<AppointmentDto> getAllAppointments(Pageable pageable);

    /**
     * Searches for appointments based on a search term with pagination support.
     *
     * @param search   the search term used to filter appointments. It is case-insensitive and may match
     *                 fields such as patient email, veterinarian email, or appointment number
     * @param pageable the pagination and sorting information
     * @return a paginated list of appointments matching the search criteria, mapped to AppointmentDto objects
     */
    Page<AppointmentDto> searchAppointments(String search, Pageable pageable);

    /**
     * Retrieves an appointment by its ID.
     *
     * @param id the ID of the appointment.
     * @return the Appointment with the specified ID.
     */
    Appointment getAppointmentById(Long id);

    /**
     * Retrieves an appointment by its ID and maps it to a DTO.
     *
     * @param id the ID of the appointment.
     * @return the {@code AppointmentDto} containing the appointment details.
     */
    AppointmentDto getAppointmentDtoById(Long id);

    /**
     * Deletes an appointment by its ID.
     *
     * @param id the ID of the appointment to delete.
     */
    void deleteAppointment(Long id);

    /**
     * Retrieves a list of appointments for a specific user.
     *
     * @param userId the ID of the user whose appointments are being retrieved.
     * @return a list of AppointmentDto objects.
     */
    List<AppointmentDto> getUserAppointments(Long userId);

    /**
     * Cancels an appointment with the specified ID.
     * The appointment must be in the "WAITING_FOR_APPROVAL" status to be cancelled.
     *
     * @param appointmentId the ID of the appointment to cancel.
     * @return the updated Appointment with a "CANCELLED" status.
     */
    AppointmentDto cancelAppointment(Long appointmentId);

    /**
     * Approves an appointment with the specified ID.
     * The appointment must be in the "WAITING_FOR_APPROVAL" status to be approved.
     *
     * @param appointmentId the ID of the appointment to approve.
     * @return the updated Appointment with an "APPROVED" status.
     */
    AppointmentDto approveAppointment(Long appointmentId);

    /**
     * Declines an appointment with the specified ID.
     * The appointment must be in the "WAITING_FOR_APPROVAL" status to be declined.
     *
     * @param appointmentId the ID of the appointment to decline.
     * @return the updated Appointment with a "NOT_APPROVED" status.
     */
    AppointmentDto declineAppointment(Long appointmentId);

    /**
     * Counts the total number of appointments in the system.
     *
     * @return the total count of appointments.
     */
    long countAppointments();

    /**
     * Retrieves a summary of appointments grouped by their status.
     *
     * @return a list of maps containing appointment status and their counts.
     */
    List<Map<String, Object>> getAppointmentSummary();

    /**
     * Retrieves a list of all appointment IDs.
     *
     * @return a list of appointment IDs.
     */
    List<Long> getAppointmentIds();

    /**
     * Sets the appropriate status for an appointment based on the current date and time.
     *
     * @param appointmentId the ID of the appointment to update.
     */
    void setAppointmentStatus(Long appointmentId);
}
