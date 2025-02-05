package com.olegtoropoff.petcareappointment.service.token;

import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;

/**
 * Interface for managing operations related to verification tokens.
 * Provides methods to validate, create, update, and delete tokens.
 */
public interface IVerificationTokenService {

    /**
     * Validates the provided token.
     *
     * @param token the token to validate
     * @return a feedback message indicating whether the token is valid, expired, or already verified
     */
    String validateToken(String token);

    /**
     * Saves a verification token for the specified user.
     *
     * @param token the token to be saved
     * @param user  the user associated with the token
     */
    void saveVerificationTokenForUser(String token, User user);

    /**
     * Generates a new verification token to replace an old one.
     *
     * @param oldToken the old token to be replaced
     * @return the updated verification token
     * @throws IllegalArgumentException if the old token is invalid
     */
    VerificationToken generateNewVerificationToken(String oldToken);

    /**
     * Checks if the given token has expired.
     *
     * @param token the token to check
     * @return {@code true} if the token has expired, {@code false} otherwise
     */
    boolean isTokenExpired(String token);

    /**
     * Finds the most recent verification token for the specified user ID.
     *
     * @param userId the ID of the user
     * @return the most recent verification token
     * @throws IllegalStateException if no token is found for the user
     */
    VerificationToken findTokenByUserId(Long userId);
}
