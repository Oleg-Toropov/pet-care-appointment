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
}
