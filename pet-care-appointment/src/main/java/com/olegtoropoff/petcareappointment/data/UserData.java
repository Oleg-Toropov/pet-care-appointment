package com.olegtoropoff.petcareappointment.data;

import lombok.Data;

/**
 * Represents user data for initializing default users in the application.
 * This class is used to store user-related information such as
 * personal details, credentials, and role information.
 * Annotated with {@link lombok.Data} to generate boilerplate code like getters,
 * setters, equals, hashCode, and toString methods automatically.
 */
@Data
public class UserData {

    /**
     * The first name of the user.
     */
    private String firstName;

    /**
     * The last name of the user.
     */
    private String lastName;

    /**
     * The gender of the user (e.g., Male, Female).
     */
    private String gender;

    /**
     * The phone number of the user.
     */
    private String phoneNumber;

    /**
     * The email address of the user, which acts as a unique identifier.
     */
    private String email;

    /**
     * The encrypted password for the user's account.
     */
    private String password;

    /**
     * The type of user in the system (e.g., ADMIN, PATIENT, VET).
     */
    private String userType;

    /**
     * The role assigned to the user (e.g., ROLE_ADMIN, ROLE_PATIENT, ROLE_VET).
     */
    private String role;
}
