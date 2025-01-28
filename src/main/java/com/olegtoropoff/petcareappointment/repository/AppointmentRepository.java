package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.enums.AppointmentStatus;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Repository interface for managing {@link Appointment} entities.
 * Provides methods for querying appointments based on various criteria.
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Finds all appointments associated with a specific user, either as a patient or a veterinarian.
     *
     * @param userId the ID of the user.
     * @return a list of appointments involving the specified user.
     */
    @Query("SELECT a FROM Appointment a WHERE a.patient.id =:userId OR a.veterinarian.id =:userId")
    List<Appointment> findAllByUserId(@Param("userId") Long userId);

    /**
     * Finds appointments for a specific veterinarian on a given date.
     *
     * @param veterinarian  the veterinarian entity.
     * @param requestedDate the date for which appointments are being queried.
     * @return a list of appointments for the specified veterinarian on the given date.
     */
    List<Appointment> findByVeterinarianAndAppointmentDate(User veterinarian, LocalDate requestedDate);

    /**
     * Finds appointments for a veterinarian by their ID and a specific date.
     *
     * @param veterinarianId the ID of the veterinarian.
     * @param requestedDate  the date for which appointments are being queried.
     * @return a list of appointments for the specified veterinarian on the given date.
     */
    List<Appointment> findByVeterinarianIdAndAppointmentDate(Long veterinarianId, LocalDate requestedDate);

    /**
     * Retrieves all appointments with pagination support.
     *
     * @param pageable the pagination information.
     * @return a paginated list of appointments.
     */
    @NonNull
    Page<Appointment> findAll(@NonNull Pageable pageable);

    /**
     * Searches for appointments based on a search term with pagination support.
     * The search term is matched against the patient email, veterinarian email, and appointment number fields.
     *
     * @param search   the search term to filter appointments (case-insensitive).
     *                 Matches substrings within patient email, veterinarian email, or appointment number.
     * @param pageable the pagination and sorting information.
     * @return a paginated list of appointments that match the search criteria.
     */
    @Query("SELECT a FROM Appointment a " +
           "WHERE LOWER(a.patient.email) LIKE %:search% " +
           "OR LOWER(a.veterinarian.email) LIKE %:search% " +
           "OR LOWER(a.appointmentNo) LIKE %:search%")
    Page<Appointment> searchAppointments(@Param("search") String search, Pageable pageable);

    /**
     * Counts the number of active appointments for a specific patient, excluding certain statuses.
     *
     * @param senderId the ID of the patient.
     * @param excludedStatuses the statuses to exclude from the count.
     * @return the count of active appointments for the patient.
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :senderId AND a.status NOT IN :excludedStatuses")
    int countByPatientIdAndStatusNotIn(@Param("senderId") Long senderId, @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses);

    /**
     * Checks if an appointment exists between a veterinarian and a patient with a specific status.
     *
     * @param veterinarianId the ID of the veterinarian.
     * @param reviewerId     the ID of the patient.
     * @param appointmentStatus the status of the appointment.
     * @return {@code true} if such an appointment exists, otherwise {@code false}.
     */
    boolean existsByVeterinarianIdAndPatientIdAndStatus(Long veterinarianId, Long reviewerId, AppointmentStatus appointmentStatus);

    boolean existsByAppointmentNo(String appointmentNo);

    /**
     * Retrieves the IDs of all appointments from the database.
     *
     * @return a list of appointment IDs.
     */
    @Query("SELECT a.id FROM Appointment a")
    List<Long> findAllIds();


    /**
     * Retrieves a summary of appointments grouped by their status.
     * The summary includes the count of appointments for each status.
     *
     * @return a list of maps, where each map contains the status and the count.
     */
    @Query("SELECT a.status AS name, COUNT(a) AS value FROM Appointment a GROUP BY a.status")
    List<Map<String, Object>> getAppointmentSummary();
}
