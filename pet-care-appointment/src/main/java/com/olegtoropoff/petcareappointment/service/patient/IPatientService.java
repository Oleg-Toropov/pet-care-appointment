package com.olegtoropoff.petcareappointment.service.patient;

import com.olegtoropoff.petcareappointment.dto.UserDto;

import java.util.List;

/**
 * Interface for patient service operations.
 * Provides methods to handle patient-related functionality.
 */
public interface IPatientService {

    /**
     * Retrieves a list of all patients as {@link UserDto} objects.
     *
     * @return a list of {@link UserDto} representing all patients in the system.
     */
    List<UserDto> getPatients();
}
