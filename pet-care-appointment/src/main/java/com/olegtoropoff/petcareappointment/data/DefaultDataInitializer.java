package com.olegtoropoff.petcareappointment.data;

import com.olegtoropoff.petcareappointment.model.*;
import com.olegtoropoff.petcareappointment.repository.*;
import com.olegtoropoff.petcareappointment.service.photo.YandexS3Service;
import com.olegtoropoff.petcareappointment.service.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Initializes default data for the application.
 * This component is executed when the application is ready and handles the initialization
 * of roles, admins, veterinarians, and patients. It also ensures default photos are uploaded to S3 storage.
 */
@Component
@Transactional
@RequiredArgsConstructor
public class DefaultDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDataInitializer.class);

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final RoleRepository roleRepository;
    private final AdminRepository adminRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final VetBiographyRepository vetBiographyRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final PhotoRepository photoRepository;
    private final YandexS3Service yandexS3Service;

    /**
     * Path to the directory containing default veterinarian photos.
     * Used to generate file paths for uploading default veterinarian images.
     */
    private static final String DEFAULT_VET_PHOTO_PATH = "static/images/veterinarians/default_vet_photo_";

    /**
     * Path to the directory containing default patient photos.
     * Used to generate file paths for uploading default patient images.
     */
    private static final String DEFAULT_PAT_PHOTO_PATH = "static/images/patients/default_pat_photo_";

    /**
     * Name of the S3 bucket used for storing user photos.
     * This bucket is configured for the application to upload and retrieve photos.
     */
    private static final String BUCKET_NAME = "bucket-pet-care-appointment";

    /**
     * Event listener that initializes roles and default user data.
     *
     * @param event the application-ready event.
     */
    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        initializeRoles();
        try {
            initializeDefaultData();
        } catch (IOException e) {
            logger.error("Error initializing default data", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes predefined roles if they do not already exist in the database.
     */
    private void initializeRoles() {
        Set<String> roles = Set.of("ROLE_ADMIN", "ROLE_PATIENT", "ROLE_VET");
        roles.stream()
                .filter(roleName -> roleRepository.findByName(roleName).isEmpty())
                .map(roleName -> {
                    Role role = new Role();
                    role.setName(roleName);
                    return role;
                })
                .forEach(roleRepository::save);
    }

    /**
     * Reads and initializes default users (admins, veterinarians, and patients) from JSON data.
     *
     * @throws IOException if there is an error reading the JSON file.
     */
    private void initializeDefaultData() throws IOException {
        DefaultUserData defaultUserData = DefaultDataReader.readDefaultUserData();
        initializeAdmins(defaultUserData);
        initializeVeterinarians(defaultUserData);
        initializePatients(defaultUserData);
    }

    /**
     * Initializes admins based on the provided default user data.
     *
     * @param defaultUserData the default user data.
     */
    private void initializeAdmins(DefaultUserData defaultUserData) {
        defaultUserData.getAdmins().forEach(adminData -> {
            if (!userRepository.existsByEmail(adminData.getEmail())) {
                Admin admin = populateUserFields(new Admin(), adminData);
                adminRepository.save(admin).setEnabled(true);
            }
        });
    }

    /**
     * Initializes veterinarians and uploads their default photos.
     *
     * @param defaultUserData the default user data.
     */
    private void initializeVeterinarians(DefaultUserData defaultUserData) {
        AtomicInteger counter = new AtomicInteger(0);
        defaultUserData.getVeterinarians().forEach(vetData -> {
            if (!userRepository.existsByEmail(vetData.getEmail())) {
                Veterinarian vet = populateUserFields(new Veterinarian(), vetData);
                vet.setSpecialization(vetData.getSpecialization());

                VetBiography vetBiography = new VetBiography();
                vetBiography.setBiography(vetData.getBiography());
                vet.setVetBiography(vetBiographyRepository.save(vetBiography));

                Veterinarian theVet = veterinarianRepository.save(vet);
                theVet.setEnabled(true);

                savePhoto(DEFAULT_VET_PHOTO_PATH + counter.getAndIncrement() + ".jpg", theVet);
            }
        });
    }

    /**
     * Initializes patients and uploads their default photos.
     *
     * @param defaultUserData the default user data.
     */
    private void initializePatients(DefaultUserData defaultUserData) {
        AtomicInteger counter = new AtomicInteger(0);
        defaultUserData.getPatients().forEach(patientData -> {
            if (!userRepository.existsByEmail(patientData.getEmail())) {
                Patient patient = populateUserFields(new Patient(), patientData);
                patientRepository.save(patient).setEnabled(true);

                savePhoto(DEFAULT_PAT_PHOTO_PATH + counter.getAndIncrement() + ".jpg", patient);
            }
        });
    }

    /**
     * Populates common fields for a user.
     *
     * @param user     the user to populate.
     * @param userData the data source.
     * @param <T>      the type of the user.
     * @return the populated user.
     */
    private <T extends User> T populateUserFields(T user, UserData userData) {
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setGender(userData.getGender());
        user.setPhoneNumber(userData.getPhoneNumber());
        user.setEmail(userData.getEmail());
        user.setPassword(passwordEncoder.encode(userData.getPassword()));
        user.setUserType(userData.getUserType());
        user.setRoles(Set.of(roleService.getRoleByName(userData.getRole())));
        return user;
    }

    /**
     * Saves the default photo for a user and uploads it to S3.
     *
     * @param defaultPhotoPath the path to the default photo.
     * @param user             the user to associate the photo with.
     */
    private void savePhoto(String defaultPhotoPath, User user) {
        ClassLoader classLoader = getClass().getClassLoader();
        Path tempFilePath = null;

        try (InputStream inputStream = classLoader.getResourceAsStream(defaultPhotoPath)) {
            if (inputStream == null) {
                logger.warn("Default photo not found at path: {}", defaultPhotoPath);
                return;
            }

            tempFilePath = Files.createTempFile("default-photo", ".tmp");
            try (OutputStream outputStream = Files.newOutputStream(tempFilePath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            try (FileInputStream fileInputStream = new FileInputStream(tempFilePath.toFile())) {
                String s3Url = yandexS3Service.uploadFile(
                        BUCKET_NAME,
                        user.getId() + "/" + System.currentTimeMillis(),
                        fileInputStream,
                        Files.size(tempFilePath),
                        "image/jpeg"
                );

                Photo photo = new Photo();
                photo.setS3Url(s3Url);
                photo.setFileType("jpg");
                photo.setFileName("default-photo.jpg");
                photo.setUser(user);

                user.setPhoto(photoRepository.save(photo));
                userRepository.save(user);
            }
        } catch (IOException e) {
            logger.error("Error saving photo for user: {}", user.getEmail(), e);
        } finally {
            if (tempFilePath != null) {
                try {
                    Files.deleteIfExists(tempFilePath);
                } catch (IOException e) {
                    logger.warn("Failed to delete temporary file: {}", tempFilePath, e);
                }
            }
        }
    }
}
