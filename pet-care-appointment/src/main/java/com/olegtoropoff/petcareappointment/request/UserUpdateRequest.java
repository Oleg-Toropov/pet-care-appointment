package com.olegtoropoff.petcareappointment.request;

import lombok.Data;

/**
 * Request object for updating user details.
 * <p>
 * This class is used to encapsulate the information required for updating
 * the details of an existing user in the system.
 */
@Data
public class UserUpdateRequest {

    /**
     * The updated first name of the user.
     * <p>
     * Optional field. If provided, it will replace the user's current first name.
     */
    private String firstName;

    /**
     * The updated last name of the user.
     * <p>
     * Optional field. If provided, it will replace the user's current last name.
     */
    private String lastName;

    /**
     * The updated gender of the user.
     * <p>
     * Example values: "Male", "Female".
     * Optional field.
     */
    private String gender;

    /**
     * The updated phone number of the user.
     * <p>
     * Optional field. Should be validated for format and correctness.
     */
    private String phoneNumber;

    /**
     * The updated specialization of the user, if applicable.
     * <p>
     * This field is relevant for certain user types, such as veterinarians.
     */
    private String specialization;
}
