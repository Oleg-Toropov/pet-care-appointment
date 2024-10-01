package com.olegtoropoff.petcareappointment.factory;

import com.olegtoropoff.petcareappointment.exception.UserAlreadyExistsException;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
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
            throw new UserAlreadyExistsException("Oops! " + request.getEmail() + " already exists!");
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
