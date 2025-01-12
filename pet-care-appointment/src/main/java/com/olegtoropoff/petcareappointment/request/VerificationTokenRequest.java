package com.olegtoropoff.petcareappointment.request;

import com.olegtoropoff.petcareappointment.model.User;
import lombok.Data;

import java.util.Date;

/**
 * Request object for creating or managing verification tokens.
 * <p>
 * This class is used to encapsulate the details of a verification token,
 * including its token value, expiration time, and associated user.
 */
@Data
public class VerificationTokenRequest {

    /**
     * The unique token string.
     * <p>
     * This is a required field used to identify the verification token.
     */
    private String token;

    /**
     * The expiration time of the token.
     * <p>
     * Indicates when the token becomes invalid. The token should not be accepted after this time.
     */
    private Date expirationTime;

    /**
     * The user associated with the verification token.
     * <p>
     * This field links the token to a specific user in the system.
     */
    private User user;
}