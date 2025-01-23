package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link VerificationToken} entities.
 * Provides methods for database operations related to verification tokens.
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    /**
     * Finds a verification token by its token string.
     *
     * @param token the token string
     * @return an {@link Optional} containing the verification token if found, or empty if not found
     */
    Optional<VerificationToken> findByToken(String token);

    /**
     * Retrieves all verification tokens associated with a specific user ID.
     *
     * @param userId the ID of the user
     * @return a list of verification tokens for the specified user
     */
    List<VerificationToken> findAllByUserId(Long userId);
}
