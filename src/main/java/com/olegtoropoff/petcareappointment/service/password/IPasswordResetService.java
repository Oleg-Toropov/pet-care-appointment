package com.olegtoropoff.petcareappointment.service.password;

import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.exception.*;

/**
 * Interface defining the contract for password reset operations.
 */
public interface IPasswordResetService {

    /**
     * Finds the user associated with a given password reset token.
     * Validates the token and ensures the new password meets required criteria.
     *
     * @param token    the password reset token to validate
     * @param password the new password to set
     * @return the {@link User} associated with the valid token
     * @throws IllegalArgumentException if the token is invalid or the password does not meet criteria
     */
    User findUserByPasswordResetToken(String token, String password);

    /**
     * Initiates a password reset request for the user with the specified email address.
     * Generates a reset token and sends a notification.
     *
     * @param email the email address of the user requesting the password reset
     * @throws IllegalArgumentException if the email is invalid
     * @throws ResourceNotFoundException
     *         if no user is found with the provided email
     */
    void requestPasswordReset(String email);

    /**
     * Resets the password for the specified user.
     *
     * @param password the new password to set
     * @param user     the user whose password will be reset
     * @return a success message indicating the password was reset successfully
     * @throws IllegalArgumentException if an error occurs during the password update process
     */
    String resetPassword(String password, User user);
}
