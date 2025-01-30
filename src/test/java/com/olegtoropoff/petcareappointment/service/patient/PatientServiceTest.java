package com.olegtoropoff.petcareappointment.service.patient;

import com.olegtoropoff.petcareappointment.dto.EntityConverter;
import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.model.Patient;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.PatientRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PatientServiceTest {

    @InjectMocks
    private PatientService patientService;

    @Mock
    private PatientRepository patientRepository;

    @Spy
    private EntityConverter<User, UserDto> entityConverter = new EntityConverter<>(new ModelMapper());

    @Test
    void getPatients_Success() {
        Patient patient = new Patient();
        patient.setFirstName("Bob");

        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<UserDto> result = patientService.getPatients();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(patient.getFirstName(), result.get(0).getFirstName());
        verify(patientRepository).findAll();
        verify(entityConverter).mapEntityToDto(patient, UserDto.class);
    }

    @Test
    void getPatients_ReturnsEmptyList_WhenNoPatientsExist() {
        when(patientRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = patientService.getPatients();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(patientRepository).findAll();
        verifyNoInteractions(entityConverter);
    }
}
