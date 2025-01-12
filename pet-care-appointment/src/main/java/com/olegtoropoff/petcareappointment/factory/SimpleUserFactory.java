package com.olegtoropoff.petcareappointment.factory;

import com.olegtoropoff.petcareappointment.exception.UserAlreadyExistsException;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Factory class for creating {@link User} instances based on the user type.
 * <p>
 * This factory supports the creation of different user roles such as Admins,
 * Veterinarians, and Patients. It validates user uniqueness by email before
 * delegating creation to specific factories for each user type.
 */
@Component
@RequiredArgsConstructor
public class SimpleUserFactory implements UserFactory {
    private final UserRepository userRepository;
    private final AdminFactory adminFactory;
    private final VeterinarianFactory veterinarianFactory;
    private final PatientFactory patientFactory;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new {@link User} instance based on the user type provided in the
     * {@link RegistrationRequest}.
     *
     * @param registrationRequest the registration details including user type, email, and password
     * @return a new {@link User} instance corresponding to the user type
     * @throws UserAlreadyExistsException if a user with the same email already exists
     */
    @Override
    public User createUser(RegistrationRequest registrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new UserAlreadyExistsException(FeedBackMessage.USER_ALREADY_EXISTS);
        }

        registrationRequest.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        switch (registrationRequest.getUserType()) {
            case "VET" -> {
                return veterinarianFactory.createVeterinarian(registrationRequest);
            }
            case "PATIENT" -> {
                return patientFactory.createPatient(registrationRequest);
            }
            case "ADMIN" -> {
                return adminFactory.createAdmin(registrationRequest);
            }
            default -> {
                return null;
            }
        }
    }
}
