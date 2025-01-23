package com.olegtoropoff.petcareappointment.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request object for user login.
 * <p>
 * This class is used to encapsulate the email and password credentials
 * required for authenticating a user in the system.
 */
@Data
public class LoginRequest  {

    /**
     * The email address of the user.
     * <p>
     * This field is required and must not be blank.
     */
    @NotBlank
    private String email;

    /**
     * The password of the user.
     * <p>
     * This field is required and must not be blank.
     */
    @NotBlank
    private String password;
}
