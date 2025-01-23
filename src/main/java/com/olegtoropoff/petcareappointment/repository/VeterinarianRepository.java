package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for managing {@link Veterinarian} entities.
 * Provides methods for accessing veterinarian-related data from the database.
 */
public interface VeterinarianRepository extends JpaRepository<Veterinarian, Long> {

    /**
     * Retrieves a list of veterinarians filtered by user type and enabled status.
     * This method fetches all records where the user type matches the specified value
     * and the `enabled` field matches the given status.
     *
     * @param userType the type of user to filter by (e.g., "VET").
     * @param isEnabled the status indicating whether the user is enabled (true for enabled, false for disabled).
     * @return a list of {@link Veterinarian} objects matching the specified criteria.
     */
    List<Veterinarian> findAllByUserTypeAndIsEnabled(String userType, boolean isEnabled);

    /**
     * Finds all veterinarians with a specific specialization.
     *
     * @param specialization the specialization to filter veterinarians by.
     * @return a list of {@link Veterinarian} entities matching the specialization.
     */
    List<Veterinarian> findBySpecialization(String specialization);

    /**
     * Checks if any veterinarians exist with the specified specialization.
     *
     * @param specialization the specialization to check.
     * @return {@code true} if a veterinarian with the specialization exists, otherwise {@code false}.
     */
    boolean existsBySpecialization(String specialization);

    /**
     * Retrieves a distinct list of all available veterinarian specializations.
     *
     * @return a list of strings representing the available specializations.
     */
    @Query("SELECT DISTINCT v.specialization FROM Veterinarian v")
    List<String> getSpecializations();

    /**
     * Aggregates the number of veterinarians by their specialization.
     *
     * @return a list of objects, where each object contains the specialization and the corresponding count of veterinarians.
     */
    @Query("SELECT v.specialization AS specialization, COUNT(v) AS count FROM Veterinarian  v GROUP BY v.specialization")
    List<Object[]> countVetsBySpecialization();
}
