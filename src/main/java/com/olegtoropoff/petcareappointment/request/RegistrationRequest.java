package com.olegtoropoff.petcareappointment.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Request object for registering a new user.
 * <p>
 * This class is used to encapsulate the information required for creating
 * a new user account in the system.
 */
@Data
public class RegistrationRequest {

    /**
     * The unique identifier of the user.
     * <p>
     * Typically, generated automatically during user creation and not set by the client.
     */
    private long id;

    /**
     * The first name of the user.
     */
    private String firstName;

    /**
     * The last name of the user.
     */
    private String lastName;

    /**
     * The gender of the user.
     * <p>
     * Example values: "Male" or "Female".
     */
    private String gender;

    /**
     * The user's phone number.
     * <p>
     * This should be validated for format and completeness.
     */
    private String phoneNumber;

    /**
     * The email address of the user.
     * <p>
     * Used as the primary identifier for login and communication.
     */
    private String email;

    /**
     * The password chosen by the user.
     * <p>
     * It is recommended to validate this field for complexity and length.
     */
    private String password;

    /**
     * The type of user being registered.
     * <p>
     * Example values: "VET", "PATIENT".
     */
    private String userType;

    /**
     * Indicates whether the user account is enabled.
     * <p>
     * This flag can be used to determine if the user has completed email verification
     * or other registration steps.
     */
    private boolean isEnabled;

    /**
     * The specialization of the user, if applicable.
     * <p>
     * For example, this field may be used for veterinarians to specify their area
     * of expertise (e.g., "Surgery", "Dentistry").
     */
    private String specialization;

    /**
     * The cost of the veterinary appointment.
     * This field represents the price a veterinarian charges for an appointment.
     */
    private BigDecimal appointmentCost;

    /**
     * The address where the veterinarian conducts appointments.
     * This field stores the location of the clinic or private practice.
     */
    private String clinicAddress;
}
