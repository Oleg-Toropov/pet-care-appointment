package com.olegtoropoff.petcareappointment.service.veterinarian;

import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.model.Veterinarian;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Interface for Veterinarian service, providing methods for managing and retrieving veterinarian-related data.
 */
public interface IVeterinarianService {

    /**
     * Retrieves a list of all veterinarians along with their detailed information.
     *
     * @return a list of {@link UserDto} containing detailed information about veterinarians.
     */
    List<UserDto> getAllVeterinariansWithDetails();

    /**
     * Retrieves a list of all available specializations for veterinarians.
     *
     * @return a list of strings representing the available specializations.
     */
    List<String> getSpecializations();

    /**
     * Finds available veterinarians for appointments based on specialization, date, and time.
     *
     * @param specialization the specialization required for the appointment.
     * @param date           the date of the requested appointment.
     * @param time           the time of the requested appointment.
     * @return a list of {@link UserDto} for veterinarians available at the specified date and time.
     */
    List<UserDto> findAvailableVeterinariansForAppointments(String specialization, LocalDate date, LocalTime time);

    /**
     * Retrieves a list of veterinarians based on a specific specialization.
     *
     * @param specialization the specialization to filter veterinarians by.
     * @return a list of {@link Veterinarian} entities matching the specialization.
     */
    List<Veterinarian> getVeterinariansBySpecialization(String specialization);

    /**
     * Aggregates the number of veterinarians by their specializations.
     *
     * @return a list of maps, where each map contains a specialization and the corresponding count of veterinarians.
     */
    List<Map<String, Object>> aggregateVetsBySpecialization();

    /**
     * Retrieves available time slots for booking an appointment with a veterinarian on a specific date.
     *
     * @param vetId the ID of the veterinarian.
     * @param date  the date for which available time slots are requested.
     * @return a list of {@link LocalTime} objects representing the available time slots for the specified veterinarian.
     */
    List<LocalTime> getAvailableTimeForBookAppointment(Long vetId, LocalDate date);
}
