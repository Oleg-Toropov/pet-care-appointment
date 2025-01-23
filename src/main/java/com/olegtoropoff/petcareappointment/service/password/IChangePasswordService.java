package com.olegtoropoff.petcareappointment.service.password;

import com.olegtoropoff.petcareappointment.request.ChangePasswordRequest;
import com.olegtoropoff.petcareappointment.exception.*;

/**
 * Interface for the service handling user password changes.
 * Provides a contract for validating and updating user passwords.
 */
public interface IChangePasswordService {

    /**
     * Changes the password for the user with the specified ID.
     *
     * @param userId  the ID of the user whose password is to be changed
     * @param request the {@link ChangePasswordRequest} containing:
     *                <ul>
     *                    <li>Current password</li>
     *                    <li>New password</li>
     *                    <li>Confirmation of the new password</li>
     *                </ul>
     * @throws IllegalArgumentException if any validation of the request fails, such as:
     *                                  <ul>
     *                                      <li>The current password is incorrect</li>
     *                                      <li>The new password does not meet security requirements</li>
     *                                      <li>The new password and confirmation do not match</li>
     *                                  </ul>
     * @throws ResourceNotFoundException
     *         if the user with the specified ID does not exist
     */
    void changePassword(Long userId, ChangePasswordRequest request);
}
