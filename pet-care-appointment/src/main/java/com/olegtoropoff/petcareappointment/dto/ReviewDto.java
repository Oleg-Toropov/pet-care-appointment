package com.olegtoropoff.petcareappointment.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for representing review details.
 * This class is used to transfer review-related data between different layers of the application,
 * ensuring separation between the persistence and presentation layers.
 */
@Data
public class ReviewDto {

    /**
     * The unique identifier of the review.
     */
    private Long id;

    /**
     * The rating given in the review, represented as a number of stars.
     * Typically ranges from 1 to 5.
     */
    private int stars;

    /**
     * The textual feedback provided by the reviewer.
     */
    private String feedback;

    /**
     * The unique identifier of the veterinarian being reviewed.
     */
    private Long veterinarianId;

    /**
     * The full name of the veterinarian being reviewed.
     */
    private String veterinarianName;

    /**
     * The unique identifier of the patient who provided the review.
     */
    private Long patientId;

    /**
     * The full name of the patient who provided the review.
     */
    private String patientName;

    /**
     * The image of the patient who provided the review, stored as a byte array.
     */
    private byte[] patientImage;

    /**
     * The image of the veterinarian being reviewed, stored as a byte array.
     */
    private byte[] veterinarianImage;
}
