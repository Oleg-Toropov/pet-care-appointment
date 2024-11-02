package com.olegtoropoff.petcareappointment.exception;

public class PetDeletionNotAllowedException extends RuntimeException {
    public PetDeletionNotAllowedException(String message) {
        super(message);
    }
}