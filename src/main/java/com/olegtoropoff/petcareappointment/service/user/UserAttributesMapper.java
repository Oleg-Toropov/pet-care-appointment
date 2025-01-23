package com.olegtoropoff.petcareappointment.service.user;

import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import org.springframework.stereotype.Component;

/**
 * Utility class for mapping user attributes from a {@link RegistrationRequest} to a {@link User}.
 * Used to set common user attributes during the user registration process.
 */
@Component
public class UserAttributesMapper {

    /**
     * Maps common attributes from the {@link RegistrationRequest} to the {@link User}.
     *
     * @param source the source registration request containing user input
     * @param target the target user entity to populate with mapped attributes
     */
    public void setCommonAttributes(RegistrationRequest source, User target) {
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setGender(source.getGender());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setEmail(source.getEmail());
        target.setPassword(source.getPassword());
        target.setUserType(source.getUserType());
        target.setEnabled(source.isEnabled());
    }
}
