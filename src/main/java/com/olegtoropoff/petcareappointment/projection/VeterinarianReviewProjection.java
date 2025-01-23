package com.olegtoropoff.petcareappointment.projection;

/**
 * Projection interface for retrieving aggregated review data for veterinarians.
 * <p>
 * This interface is typically used in Spring Data JPA to project specific fields
 * from query results without needing to retrieve entire entities.
 */
public interface VeterinarianReviewProjection {

    /**
     * Retrieves the unique identifier of the veterinarian.
     *
     * @return the ID of the veterinarian.
     */
    Long getVeterinarianId();

    /**
     * Retrieves the average rating given to the veterinarian.
     *
     * @return the average rating as a {@link Double}.
     */
    Double getAverageRating();

    /**
     * Retrieves the total number of reviewers who have rated the veterinarian.
     *
     * @return the total number of reviewers as a {@link Long}.
     */
    Long getTotalReviewers();
}