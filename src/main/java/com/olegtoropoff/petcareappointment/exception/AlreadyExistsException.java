package com.olegtoropoff.petcareappointment.exception;

/**
 * Exception thrown when an attempt is made to create or add an entity
 * that already exists in the system.
 * <p>
 * This exception is typically used to indicate conflicts in creation
 * operations, such as attempting to register a user with an existing
 * email address or adding a duplicate entity.
 */
public class AlreadyExistsException extends RuntimeException{

    /**
     * Constructs a new {@code AlreadyExistsException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public AlreadyExistsException(String message){
        super(message);
    }
}
