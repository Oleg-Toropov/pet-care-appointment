package com.olegtoropoff.petcareappointment.service.veterinarian;

import com.olegtoropoff.petcareappointment.dto.UserDto;

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
     * Retrieves detailed information about a veterinarian, including their photo and reviews.
     * <p>
     * This method:
     * <ul>
     *     <li>Fetches a veterinarian by their ID, ensuring their photo is loaded.</li>
     *     <li>Throws an exception if the veterinarian is not found.</li>
     *     <li>Converts the veterinarian entity into a {@link UserDto}.</li>
     *     <li>Enriches the {@link UserDto} with review details, including average rating and total number of reviews.</li>
     * </ul>
     *
     * @param vetId the ID of the veterinarian.
     * @return a {@link UserDto} containing veterinarian details, photo, and reviews.
     */
    UserDto getVeterinarianWithDetailsAndReview(Long vetId);

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

    /**
     * Retrieves a list of all veterinarians as {@link UserDto} objects.
     *
     * @return a list of {@link UserDto} representing all veterinarians in the system.
     */
    List<UserDto> getVeterinarians();
}
