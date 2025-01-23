package com.olegtoropoff.petcareappointment.factory;

import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;

/**
 * Factory interface for creating instances of {@link User}.
 * <p>
 * This interface provides a method for creating user instances based on the provided
 * {@link RegistrationRequest}. Implementations of this interface can create specific
 * user types such as Admin, Patient, or Veterinarian.
 */
public interface UserFactory {

    /**
     * Creates a new {@link User} instance based on the provided registration request.
     *
     * @param request the registration request containing details for the user
     * @return the created {@link User} instance
     */
    User createUser(RegistrationRequest request);
}
