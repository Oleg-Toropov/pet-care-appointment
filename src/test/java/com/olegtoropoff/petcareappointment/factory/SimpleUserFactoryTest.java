package com.olegtoropoff.petcareappointment.factory;

import com.olegtoropoff.petcareappointment.exception.UserAlreadyExistsException;
import com.olegtoropoff.petcareappointment.model.Admin;
import com.olegtoropoff.petcareappointment.model.Patient;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@Tag("unit")
class SimpleUserFactoryTest {

    @InjectMocks
    private SimpleUserFactory simpleUserFactory;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminFactory adminFactory;

    @Mock
    private VeterinarianFactory veterinarianFactory;

    @Mock
    private PatientFactory patientFactory;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    public void createUser_WhenUserTypeIsVet_ReturnsVeterinarian() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUserType("VET");
        request.setEmail("vet@example.com");
        request.setPassword("password123");

        Veterinarian veterinarian = new Veterinarian();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(veterinarianFactory.createVeterinarian(request)).thenReturn(veterinarian);

        User result = simpleUserFactory.createUser(request);

        assertEquals(veterinarian, result);
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(veterinarianFactory, times(1)).createVeterinarian(request);
    }

    @Test
    public void createUser_WhenUserTypeIsPatient_ReturnsPatient() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUserType("PATIENT");
        request.setEmail("patient@example.com");
        request.setPassword("password123");

        Patient patient = new Patient();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(patientFactory.createPatient(request)).thenReturn(patient);

        User result = simpleUserFactory.createUser(request);

        assertEquals(patient, result);
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(patientFactory, times(1)).createPatient(request);
    }

    @Test
    void createUser_WhenUserTypeIsAdmin_ReturnsAdmin() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUserType("ADMIN");
        request.setEmail("admin@example.com");
        request.setPassword("password123");

        Admin admin = new Admin();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(adminFactory.createAdmin(request)).thenReturn(admin);

        User result = simpleUserFactory.createUser(request);

        assertEquals(admin, result);
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(adminFactory, times(1)).createAdmin(request);
    }

    @Test
    void createUser_WhenUserAlreadyExists_ThrowsException() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUserType("PATIENT");
        request.setEmail("patient@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () ->
                simpleUserFactory.createUser(request)
        );

        assertEquals(FeedBackMessage.USER_ALREADY_EXISTS, exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verifyNoInteractions(passwordEncoder, patientFactory, adminFactory, veterinarianFactory);
    }
}
