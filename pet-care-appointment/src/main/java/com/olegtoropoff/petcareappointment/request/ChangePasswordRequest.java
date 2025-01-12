package com.olegtoropoff.petcareappointment.request;

import lombok.Data;

/**
 * Request object for changing a user's password.
 * <p>
 * This class is used to encapsulate the details required for updating a user's password,
 * including the current password, the new password, and its confirmation.
 */
@Data
public class ChangePasswordRequest {

    /**
     * The user's current password.
     * <p>
     * This is used to validate the user's identity before allowing a password change.
     */
    private String currentPassword;

    /**
     * The new password the user wants to set.
     * <p>
     * Must meet the application's password strength requirements.
     */
    private String newPassword;

    /**
     * Confirmation of the new password.
     * <p>
     * Ensures the user did not make a mistake while entering their new password.
     */
    private String confirmNewPassword;
}
