package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.response.CustomApiResponse;
import com.olegtoropoff.petcareappointment.service.veterinarian.IVeterinarianService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Controller for managing veterinarians. Provides endpoints to retrieve, search, and aggregate veterinarian data.
 */
@RestController
@RequestMapping(UrlMapping.VETERINARIANS)
@RequiredArgsConstructor
public class VeterinarianController {
    private final IVeterinarianService veterinarianService;

    /**
     * Retrieves all veterinarians with their detailed information.
     * This endpoint fetches a list of all veterinarians with the user type "VET" who are enabled,
     * and returns their details wrapped in a {@link CustomApiResponse}.
     *
     * @return a {@link ResponseEntity} containing:
     * - {@link CustomApiResponse} with a list of all veterinarians if the operation is successful.
     * - {@link CustomApiResponse} with an error message if an exception occurs.
     */
    @GetMapping(UrlMapping.GET_ALL_VETERINARIANS)
    public ResponseEntity<CustomApiResponse> getAllVeterinariansWithDetails() {
        try {
            List<UserDto> allVeterinariansDtos = veterinarianService.getAllVeterinariansWithDetails();
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.RESOURCE_FOUND, allVeterinariansDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Retrieves all veterinarians.
     *
     * @return a {@link ResponseEntity} containing a list of all veterinarians or an error message
     */
    @GetMapping(UrlMapping.GET_VETERINARIANS)
    public ResponseEntity<CustomApiResponse> getAllVeterinarians() {
        try {
            List<UserDto> veterinarians = veterinarianService.getVeterinarians();
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.RESOURCE_FOUND, veterinarians));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Searches for veterinarians available for an appointment based on specialization, date, and time.
     *
     * @param date           the desired date of the appointment (optional)
     * @param time           the desired time of the appointment (optional)
     * @param specialization the specialization of the veterinarian
     * @return a {@link ResponseEntity} containing a list of available veterinarians or an error message
     */
    @GetMapping(UrlMapping.SEARCH_VETERINARIAN_FOR_APPOINTMENT)
    public ResponseEntity<CustomApiResponse> searchVeterinariansForAppointment(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalTime time,
            @RequestParam String specialization) {
        try {
            List<UserDto> availableVeterinarians = veterinarianService.findAvailableVeterinariansForAppointments(specialization, date, time);
            if (availableVeterinarians.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(FeedBackMessage.NO_VETS_AVAILABLE, null));
            }
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.RESOURCE_FOUND, availableVeterinarians));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        }
    }

    /**
     * Retrieves a list of all specializations of veterinarians.
     *
     * @return a {@link ResponseEntity} containing a list of specializations or an error message
     */
    @GetMapping(UrlMapping.GET_ALL_SPECIALIZATIONS)
    public ResponseEntity<CustomApiResponse> getAllSpecializations() {
        try {
            List<String> specializations = veterinarianService.getSpecializations();
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.RESOURCE_FOUND, specializations));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Aggregates the number of veterinarians by their specializations.
     *
     * @return a {@link ResponseEntity} containing the aggregated data
     */
    @GetMapping(UrlMapping.AGGREGATE_VETERINARIANS_BY_SPECIALIZATION)
    public ResponseEntity<List<Map<String, Object>>> aggregateVetsBySpecialization() {
        List<Map<String, Object>> aggregatedVets = veterinarianService.aggregateVetsBySpecialization();
        return ResponseEntity.ok(aggregatedVets);
    }

    /**
     * Retrieves available appointment times for a veterinarian on a specific date.
     *
     * @param vetId the ID of the veterinarian
     * @param date  the desired date for the appointment
     * @return a {@link ResponseEntity} containing a list of available times or an error message
     */
    @GetMapping(UrlMapping.GET_AVAILABLE_TIME_FOR_BOOK_APPOINTMENT)
    public ResponseEntity<CustomApiResponse> getAvailableTimeForBookAppointment(@PathVariable Long vetId,
                                                                                @RequestParam LocalDate date) {
        try {
            List<LocalTime> availableTimes = veterinarianService.getAvailableTimeForBookAppointment(vetId, date);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.AVAILABLE_TIME_FOR_APPOINTMENT_FOUND, availableTimes));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }
}
