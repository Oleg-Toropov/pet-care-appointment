package com.olegtoropoff.petcareappointment.factory;

import com.olegtoropoff.petcareappointment.model.Patient;
import com.olegtoropoff.petcareappointment.repository.PatientRepository;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.service.role.IRoleService;
import com.olegtoropoff.petcareappointment.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientFactory {
    private final PatientRepository patientRepository;
    private final UserAttributesMapper userAttributesMapper;
    private IRoleService roleService;

    public Patient createPatient(RegistrationRequest request) {
        Patient patient = new Patient();
        patient.setRoles(roleService.setUserRole("PATIENT"));
        userAttributesMapper.setCommonAttributes(request, patient);
        return patientRepository.save(patient);
    }
}
