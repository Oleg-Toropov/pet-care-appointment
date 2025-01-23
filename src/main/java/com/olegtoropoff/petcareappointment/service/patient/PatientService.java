package com.olegtoropoff.petcareappointment.service.patient;

import com.olegtoropoff.petcareappointment.dto.EntityConverter;
import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.model.Patient;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for handling patient-related operations.
 * Provides methods to retrieve patient information and transform it into DTOs.
 */
@Service
@RequiredArgsConstructor
public class PatientService implements IPatientService {
    private final PatientRepository patientRepository;
    private final EntityConverter<User, UserDto> entityConverter;

    /**
     * Retrieves all patients and converts them to {@link UserDto}.
     *
     * @return a list of {@link UserDto} objects representing all patients.
     */
    @Override
    public List<UserDto> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
                .map(p -> entityConverter.mapEntityToDto(p, UserDto.class)).toList();
    }
}