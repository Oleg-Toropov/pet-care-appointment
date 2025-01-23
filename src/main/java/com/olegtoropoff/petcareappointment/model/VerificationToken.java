package com.olegtoropoff.petcareappointment.model;

import com.olegtoropoff.petcareappointment.utils.SystemUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Represents a verification token used for user account operations such as email verification or password recovery.
 * <p>
 * This entity associates a unique token with a user and includes an expiration date
 * to ensure the token is valid for a limited period.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
public class VerificationToken {

    /**
     * Unique identifier for the verification token.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique token string.
     * <p>
     * This token is generated and sent to the user for verification purposes.
     */
    private String token;

    /**
     * The expiration date of the token.
     * <p>
     * The token becomes invalid after this date.
     */
    private Date expirationDate;

    /**
     * The user associated with the verification token.
     * <p>
     * Establishes a many-to-one relationship with the {@link User} entity.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Constructs a new `VerificationToken` with the specified token and user.
     * <p>
     * The expiration date is automatically set using {@link SystemUtils#getExpirationTime()}.
     *
     * @param token the unique token string
     * @param user  the user associated with the token
     */
    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expirationDate = SystemUtils.getExpirationTime();
    }
}
