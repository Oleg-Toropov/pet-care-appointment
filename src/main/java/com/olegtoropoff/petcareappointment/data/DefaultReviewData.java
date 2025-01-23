package com.olegtoropoff.petcareappointment.data;

import lombok.Data;

import java.util.List;

/**
 * Represents the default review data for initialization purposes.
 * <p>
 * This class is used to load predefined reviews into the system, typically during
 * application startup or testing. It contains a list of review data that includes
 * patient and veterinarian information, feedback, and ratings.
 */
@Data
public class DefaultReviewData {

    /**
     * List of reviews to be initialized.
     */
    private List<ReviewData> reviews;

    /**
     * Represents a single review with feedback, stars, and related user information.
     */
    @Data
    public static class ReviewData {

        /**
         * Feedback provided by the patient about the veterinarian.
         */
        private String feedback;

        /**
         * The rating provided by the patient, represented as stars (e.g., 1-5).
         */
        private int stars;

        /**
         * Email of the patient who submitted the review.
         */
        private String patientEmail;

        /**
         * Email of the veterinarian to whom the review is addressed.
         */
        private String veterinarianEmail;
    }
}
