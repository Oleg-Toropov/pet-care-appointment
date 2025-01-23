package com.olegtoropoff.petcareappointment.exception;

/**
 * Exception thrown when a requested resource is not found.
 * <p>
 * This exception is typically used in cases where a resource,
 * such as a user, appointment, or pet, cannot be found in the database.
 */
public class ResourceNotFoundException extends RuntimeException{

    /**
     * Constructs a new {@code ResourceNotFoundException} with the specified detail message.
     *
     * @param message the detail message explaining which resource could not be found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
