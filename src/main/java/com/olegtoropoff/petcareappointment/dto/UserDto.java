package com.olegtoropoff.petcareappointment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Data Transfer Object (DTO) for representing user details.
 * This class is used to transfer user-related data between different layers of the application,
 * ensuring separation between the persistence and presentation layers.
 */
@Data
public class UserDto {

    /**
     * The unique identifier of the user.
     */
    private Long id;

    /**
     * The first name of the user.
     */
    private String firstName;

    /**
     * The last name of the user.
     */
    private String lastName;

    /**
     * The gender of the user (e.g., "Male", "Female").
     */
    private String gender;

    /**
     * The phone number of the user.
     */
    private String phoneNumber;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The type of user (e.g., "PATIENT", "VET", "ADMIN").
     */
    private String userType;

    /**
     * Indicates whether the user account is enabled.
     */
    private boolean isEnabled;

    /**
     * The specialization of the user, applicable for veterinarians.
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

    /**
     * The date the user was created in the system.
     */
    private LocalDate createdAt;

    /**
     * A list of appointments associated with the user.
     */
    private List<AppointmentDto> appointments;

    /**
     * A list of reviews associated with the user.
     */
    private List<ReviewDto> reviews;

    /**
     * The ID of the photo associated with the user.
     */
    private long photoId;

    /**
     * The URL of the photo associated with the user.
     */
    private String photoUrl;

    /**
     * The average rating of the user (applicable for veterinarians).
     */
    private double averageRating;

    /**
     * The roles assigned to the user (e.g., "ROLE_ADMIN", "ROLE_USER").
     */
    private Set<String> roles;

    /**
     * The total number of reviewers who have reviewed the user (applicable for veterinarians).
     */
    private Long totalReviewers;
}
