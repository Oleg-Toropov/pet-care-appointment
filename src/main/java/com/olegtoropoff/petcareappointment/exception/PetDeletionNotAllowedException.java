package com.olegtoropoff.petcareappointment.exception;

/**
 * Exception thrown when an attempt to delete a pet is not allowed.
 * <p>
 * This exception is typically used to enforce business rules that restrict
 * the deletion of pets under certain conditions, such as when the pet
 * is associated with active or past appointments.
 */
public class PetDeletionNotAllowedException extends RuntimeException {

    /**
     * Constructs a new {@code PetDeletionNotAllowedException} with the specified detail message.
     *
     * @param message the detail message explaining why the pet deletion is not allowed
     */
    public PetDeletionNotAllowedException(String message) {
        super(message);
    }
}