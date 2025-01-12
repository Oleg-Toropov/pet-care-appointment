package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 * Provides methods for database operations related to users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks if a user exists with the specified email.
     *
     * @param email the email to check.
     * @return {@code true} if a user with the given email exists, {@code false} otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Counts the number of users of a specific type.
     *
     * @param type the user type (e.g., "VET", "PATIENT").
     * @return the count of users with the specified type.
     */
    long countByUserType(String type);

    /**
     * Updates the enabled status of a user.
     *
     * @param userId  the ID of the user whose status is to be updated.
     * @param enabled the new enabled status to set.
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isEnabled = :enabled WHERE u.id = :userId")
    void updateUserEnabledStatus(@Param("userId") Long userId, @Param("enabled") boolean enabled);

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user to retrieve.
     * @return an {@link Optional} containing the {@link User} if found, or empty if not found.
     */
    Optional<User> findByEmail(String email);
}

