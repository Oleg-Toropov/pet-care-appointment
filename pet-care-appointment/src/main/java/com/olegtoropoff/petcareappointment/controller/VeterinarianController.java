package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.response.ApiResponse;
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

@RestController
@RequestMapping(UrlMapping.VETERINARIANS)
@RequiredArgsConstructor
public class VeterinarianController {
    private final IVeterinarianService veterinarianService;

    @GetMapping(UrlMapping.GET_ALL_VETERINARIANS)
    public ResponseEntity<ApiResponse> getAllVeterinarians() {
        try {
            List<UserDto> allVeterinariansDtos = veterinarianService.getAllVeterinariansWithDetails();
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.RESOURCE_FOUND, allVeterinariansDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @GetMapping(UrlMapping.SEARCH_VETERINARIAN_FOR_APPOINTMENT)
    public ResponseEntity<ApiResponse> searchVeterinariansForAppointment(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalTime time,
            @RequestParam String specialization) {
        try {
            List<UserDto> availableVeterinarians = veterinarianService.findAvailableVeterinariansForAppointments(specialization, date, time);
            if (availableVeterinarians.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(FeedBackMessage.NO_VETS_AVAILABLE, null));
            }
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.RESOURCE_FOUND, availableVeterinarians));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_ALL_SPECIALIZATIONS)
    public ResponseEntity<ApiResponse> getAllSpecializations() {
        try {
            List<String> specializations = veterinarianService.getSpecializations();
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.RESOURCE_FOUND, specializations));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @GetMapping(UrlMapping.AGGREGATE_VETERINARIANS_BY_SPECIALIZATION)
    public ResponseEntity<List<Map<String, Object>>> aggregateVetsBySpecialization() {
        List<Map<String, Object>> aggregatedVets = veterinarianService.aggregateVetsBySpecialization();
        return ResponseEntity.ok(aggregatedVets);
    }

    @GetMapping(UrlMapping.GET_AVAILABLE_TIME_FOR_BOOK_APPOINTMENT)
    public ResponseEntity<ApiResponse> getAvailableTimeForBookAppointment(@PathVariable Long vetId,
                                                                          @RequestParam LocalDate date) {
        try {
            List<LocalTime> availableTimes = veterinarianService.getAvailableTimeForBookAppointment(vetId, date);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.AVAILABLE_TIME_FOR_APPOINTMENT_FOUND, availableTimes));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }
}
