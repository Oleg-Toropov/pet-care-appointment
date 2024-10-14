package com.olegtoropoff.petcareappointment.factory;

import com.olegtoropoff.petcareappointment.exception.UserAlreadyExistsException;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpleUserFactory implements UserFactory {
    private final UserRepository userRepository;
    private final AdminFactory adminFactory;
    private final VeterinarianFactory veterinarianFactory;
    private final PatientFactory patientFactory;

    @Override
    public User createUser(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(FeedBackMessage.USER_ALREADY_EXISTS);
        }

        switch (request.getUserType()) {
            case "VET" -> {
                return veterinarianFactory.createVeterinarian(request);
            }
            case "PATIENT" -> {
                return patientFactory.createPatient(request);
            }
            case "ADMIN" -> {
                return adminFactory.createAdmin(request);
            }
            default -> {
                return null;
            }
        }


    }
}
