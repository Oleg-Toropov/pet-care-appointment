package com.olegtoropoff.petcareappointment.service.patient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.olegtoropoff.petcareappointment.dto.EntityConverter;
import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.model.Patient;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

@Tag("unit")
class PatientServiceTest {

    @InjectMocks
    private PatientService patientService;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private EntityConverter<User, UserDto> entityConverter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPatients_Success() {
        Patient patient = new Patient();
        UserDto userDto = new UserDto();

        when(patientRepository.findAll()).thenReturn(List.of(patient));
        when(entityConverter.mapEntityToDto(patient, UserDto.class)).thenReturn(userDto);

        List<UserDto> result = patientService.getPatients();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));

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
