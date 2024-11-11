package com.olegtoropoff.petcareappointment.data;

import com.olegtoropoff.petcareappointment.model.Patient;
import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.repository.PatientRepository;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.repository.VeterinarianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

// TODO FOR TESTS -> CHANGE OR DELETE
@Component
@RequiredArgsConstructor
public class DefaultDataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final PatientRepository patientRepository;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        createDefaultVetIfNotExits();
        createDefaultPatientIfNotExits();
    }

    private void createDefaultVetIfNotExits() {
        for (int i = 1; i <= 30; i++) {
            String defaultEmail = "vet" + i + "@gmail.com";
            if (userRepository.existsByEmail(defaultEmail)) {
                continue;
            }
            Veterinarian vet = new Veterinarian();
            vet.setFirstName("Vet");
            vet.setLastName("Number" + i);
            vet.setGender("Female");
            vet.setPhoneNumber("1234567890");
            vet.setEmail(defaultEmail);
            vet.setPassword("password" + i);
            vet.setUserType("VET");
            vet.setSpecialization("Хирург");
            Veterinarian theVet = veterinarianRepository.save(vet);
            theVet.setEnabled(true);
            System.out.println("Default vet user " + i + " created successfully.");
        }
    }

    private void createDefaultPatientIfNotExits() {
        for (int i = 1; i <= 30; i++) {
            String defaultEmail = "pat" + i + "@gmail.com";
            if (userRepository.existsByEmail(defaultEmail)) {
                continue;
            }
            Patient pat = new Patient();
            pat.setFirstName("Pat");
            pat.setLastName("Patient" + i);
            pat.setGender("Male");
            pat.setPhoneNumber("1234567890");
            pat.setEmail(defaultEmail);
            pat.setPassword("password" + i);
            pat.setUserType("PATIENT");
            Patient thePatient = patientRepository.save(pat);
            thePatient.setEnabled(true);
            System.out.println("Default pat user " + i + " created successfully.");
        }
    }
}