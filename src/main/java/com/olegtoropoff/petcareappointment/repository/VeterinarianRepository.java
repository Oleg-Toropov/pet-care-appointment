package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
     * @param userType  the type of user to filter by (e.g., "VET").
     * @param isEnabled the status indicating whether the user is enabled (true for enabled, false for disabled).
     * @return a list of {@link Veterinarian} objects matching the specified criteria.
     */
    List<Veterinarian> findAllByUserTypeAndIsEnabled(String userType, boolean isEnabled);

    /**
     * Finds all veterinarians by their specialization and enabled status.
     *
     * @param specialization the specialization of the veterinarian (e.g., "Dentistry", "Surgery").
     * @param isEnabled a flag indicating whether the veterinarian is active (`true`) or disabled (`false`).
     * @return a {@link List} of veterinarians matching the specialization and enabled status.
     */
    List<Veterinarian> findBySpecializationAndIsEnabled(String specialization, boolean isEnabled);

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

    /**
     * Retrieves a veterinarian by their ID along with their associated photo.
     * Uses a **LEFT JOIN FETCH** to eagerly fetch the `photo` entity and avoid the **N+1 query problem**.
     *
     * @param vetId the ID of the veterinarian to retrieve.
     * @return an {@link Optional} containing the veterinarian with their photo if found, otherwise empty.
     */
    @Query("SELECT v FROM Veterinarian v LEFT JOIN FETCH v.photo WHERE v.id = :vetId")
    Optional<Veterinarian> findVeterinarianWithPhotoById(@Param("vetId") Long vetId);
}
