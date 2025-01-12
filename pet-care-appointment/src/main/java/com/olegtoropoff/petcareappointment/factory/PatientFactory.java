package com.olegtoropoff.petcareappointment.factory;

import com.olegtoropoff.petcareappointment.model.Patient;
import com.olegtoropoff.petcareappointment.repository.PatientRepository;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.service.role.IRoleService;
import com.olegtoropoff.petcareappointment.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Factory class for creating {@link Patient} instances.
 * <p>
 * This factory handles the creation of patients by mapping attributes from a
 * {@link RegistrationRequest}, assigning a "PATIENT" role, and saving the patient
 * to the database.
 */
@Service
@RequiredArgsConstructor
public class PatientFactory {
    private final PatientRepository patientRepository;
    private final UserAttributesMapper userAttributesMapper;
    private final IRoleService roleService;

    /**
     * Creates a new {@link Patient} instance based on the given registration request.
     *
     * @param request the registration request containing details for the patient
     * @return the created {@link Patient} instance, persisted in the database
     */
    public Patient createPatient(RegistrationRequest request) {
        Patient patient = new Patient();
        patient.setRoles(roleService.setUserRole("PATIENT"));
        userAttributesMapper.setCommonAttributes(request, patient);
        return patientRepository.save(patient);
    }
}
