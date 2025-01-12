package com.olegtoropoff.petcareappointment.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the response containing a JWT (JSON Web Token) and user ID.
 * <p>
 * This class is used to encapsulate the token and associated user information
 * when a user successfully authenticates.
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * JwtResponse jwtResponse = new JwtResponse(1L, "eyJhbGciOiJIUzI1NiIsInR5cCI...");
 * }</pre>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    /**
     * The unique identifier of the authenticated user.
     * <p>
     * Example: {@code 1}, {@code 100}, etc.
     */
    private Long id;

    /**
     * The JWT (JSON Web Token) issued for the authenticated user.
     * <p>
     * This token is used to authorize subsequent API requests.
     * Example: {@code "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."}.
     */
    private String token;
}
