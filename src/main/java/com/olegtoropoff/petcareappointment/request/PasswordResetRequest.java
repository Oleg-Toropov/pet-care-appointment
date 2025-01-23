package com.olegtoropoff.petcareappointment.request;

import lombok.Data;

/**
 * Request object for resetting a user's password.
 * <p>
 * This class is used to encapsulate the information required for resetting
 * a user's password, including the reset token and the new password.
 */
@Data
public class PasswordResetRequest {

    /**
     * The token used to verify the password reset request.
     * <p>
     * This token is typically sent to the user's email as part of the password
     * reset process.
     */
    private String token;

    /**
     * The new password that the user wants to set.
     * <p>
     * It is recommended to validate this field for complexity and length
     * to ensure security.
     */
    private String newPassword;
}