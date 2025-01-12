package com.olegtoropoff.petcareappointment.data;

import com.olegtoropoff.petcareappointment.model.Admin;
import com.olegtoropoff.petcareappointment.model.Patient;
import com.olegtoropoff.petcareappointment.model.Role;
import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.repository.*;
import com.olegtoropoff.petcareappointment.service.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Initializes default roles and user accounts in the application upon startup.
 * <p>
 * This component listens for the {@link ApplicationReadyEvent} and creates:
 * <ul>
 *     <li>Default roles (e.g., ADMIN, PATIENT, VET)</li>
 *     <li>Default admin account</li>
 *     <li>Sample veterinarian accounts</li>
 *     <li>Sample patient accounts</li>
 * </ul>
 * The initialization is transactional to ensure consistency.
 */
@Component
@Transactional
@RequiredArgsConstructor
public class DefaultDataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final RoleService roleService;

    /**
     * Triggered when the application is fully started.
     * <p>
     * Creates default roles and accounts if they do not already exist.
     *
     * @param event the event indicating the application is ready
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Set<String> defaultRoles = Set.of("ROLE_ADMIN", "ROLE_PATIENT", "ROLE_VET");
        createDefaultRoleIfNotExits(defaultRoles);

//        createDefaultAdminIfNotExists();
//        createDefaultVetIfNotExits();
//        createDefaultPatientIfNotExits();
    }

    /**
     * Creates default veterinarian accounts if they do not already exist.
     * <p>
     * Each veterinarian has a unique email and a predefined password.
     */
    private void createDefaultVetIfNotExits() {
        Role vetRole = roleService.getRoleByName("ROLE_VET");
        for (int i = 1; i <= 4; i++) {
            String defaultEmail = "vet" + i + "@gmail.com";
            if (userRepository.existsByEmail(defaultEmail)) {
                continue;
            }
            Veterinarian vet = new Veterinarian();
            vet.setFirstName("Vet");
            vet.setLastName("Number" + i);
            vet.setGender("Male");
            vet.setPhoneNumber("1234567890");
            vet.setEmail(defaultEmail);
            vet.setPassword(passwordEncoder.encode("password" + i));
            vet.setUserType("VET");
            vet.setRoles(new HashSet<>(Collections.singletonList(vetRole)));
            vet.setSpecialization("Dermatologist");
            Veterinarian theVet = veterinarianRepository.save(vet);
            theVet.setEnabled(true);
            System.out.println("Default vet user " + i + " created successfully.");
        }
    }

    /**
     * Creates default patient accounts if they do not already exist.
     * <p>
     * Each patient has a unique email and a predefined password.
     */
    private void createDefaultPatientIfNotExits() {
        Role patientRole = roleService.getRoleByName("ROLE_PATIENT");
        for (int i = 1; i <= 4; i++) {
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
            pat.setPassword(passwordEncoder.encode("password" + i));
            pat.setUserType("PATIENT");
            pat.setRoles(new HashSet<>(Collections.singletonList(patientRole)));
            Patient thePatient = patientRepository.save(pat);
            thePatient.setEnabled(true);
            System.out.println("Default vet user " + i + " created successfully.");
        }
    }

    /**
     * Creates a default admin account if it does not already exist.
     * <p>
     * The admin account has a predefined email and password.
     */
    private void createDefaultAdminIfNotExists() {
        Role adminRole = roleService.getRoleByName("ROLE_ADMIN");
        final String defaultAdminEmail = "admin@email.com";
        if (userRepository.findByEmail(defaultAdminEmail).isPresent()) {
            return;
        }

        Admin admin = new Admin();
        admin.setFirstName("UPC");
        admin.setLastName("Admin");
        admin.setGender("Female");
        admin.setPhoneNumber("22222222");
        admin.setEmail(defaultAdminEmail);
        admin.setPassword(passwordEncoder.encode("12345"));
        admin.setUserType("ADMIN");
        admin.setRoles(new HashSet<>(Collections.singletonList(adminRole)));
        Admin theAdmin = adminRepository.save(admin);
        theAdmin.setEnabled(true);
        System.out.println("Default admin user created successfully.");
    }

    /**
     * Creates default roles if they do not already exist in the system.
     *
     * @param roles a set of role names to be created
     */
    private void createDefaultRoleIfNotExits(Set<String> roles) {
        roles.stream()
                .filter(role -> roleRepository.findByName(role).isEmpty())
                .map(Role::new).forEach(roleRepository::save);
    }
}