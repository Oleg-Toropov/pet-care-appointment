package com.olegtoropoff.petcareappointment.factory;

import com.olegtoropoff.petcareappointment.model.Patient;
import com.olegtoropoff.petcareappointment.model.Role;
import com.olegtoropoff.petcareappointment.repository.PatientRepository;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.service.role.IRoleService;
import com.olegtoropoff.petcareappointment.service.user.UserAttributesMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PatientFactoryTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserAttributesMapper userAttributesMapper;

    @Mock
    private IRoleService roleService;

    @InjectMocks
    private PatientFactory patientFactory;

    @Test
    void createPatient_ShouldCreateAndSavePatient() {
        RegistrationRequest request = new RegistrationRequest();
        Role patientRole = new Role();
        patientRole.setName("PATIENT");

        when(roleService.setUserRole("PATIENT")).thenReturn(Set.of(patientRole));
        Patient savedPatient = new Patient();
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        Patient result = patientFactory.createPatient(request);

        verify(roleService, times(1)).setUserRole("PATIENT");
        verify(userAttributesMapper, times(1)).setCommonAttributes(eq(request), any(Patient.class));
        ArgumentCaptor<Patient> patientCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository, times(1)).save(patientCaptor.capture());

        assertEquals(savedPatient, result);
    }
}
