package com.olegtoropoff.petcareappointment.exception;

/**
 * Exception thrown when an attempt is made to register a user
 * that already exists in the system.
 * <p>
 * This exception is typically used during user registration
 * to prevent duplication of user accounts with the same email.
 */
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new {@code UserAlreadyExistsException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
