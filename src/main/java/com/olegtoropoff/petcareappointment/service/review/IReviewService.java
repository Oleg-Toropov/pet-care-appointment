package com.olegtoropoff.petcareappointment.service.review;

import com.olegtoropoff.petcareappointment.model.Review;
import com.olegtoropoff.petcareappointment.projection.VeterinarianReviewProjection;
import com.olegtoropoff.petcareappointment.exception.*;

import java.util.List;
import java.util.Map;

/**
 * Interface for managing reviews in the application.
 * Provides methods for creating, deleting, and retrieving reviews,
 * as well as calculating average ratings for veterinarians.
 */
public interface IReviewService {

    /**
     * Saves a new review for a veterinarian.
     *
     * @param review        the review to save.
     * @param reviewerId    the ID of the patient submitting the review.
     * @param veterinarianId the ID of the veterinarian being reviewed.
     * @return the saved review.
     * @throws IllegalArgumentException if the reviewer is the veterinarian.
     * @throws AlreadyExistsException if the patient has already reviewed the veterinarian.
     * @throws ResourceNotFoundException if the veterinarian or patient does not exist.
     * @throws IllegalStateException if the patient has no completed appointments with the veterinarian.
     */
    Review saveReview(Review review, Long reviewerId, Long veterinarianId);

    /**
     * Retrieves all reviews associated with a specific user.
     *
     * @param userId the ID of the user whose reviews are to be retrieved.
     * @return a {@link List} of {@link Review} containing all reviews for the specified user.
     */
    List<Review> findAllReviewsByUserId(Long userId);

    /**
     * Deletes a review by its ID.
     *
     * @param reviewerId the ID of the review to delete.
     * @throws ResourceNotFoundException if the review does not exist.
     */
    void deleteReview(Long reviewerId);

    /**
     * Retrieves a mapping of veterinarian IDs to their aggregated review data.
     * <p>
     * The returned map contains information such as average ratings and the total number of reviewers
     * for each veterinarian.
     * </p>
     *
     * @return a {@link Map} where the keys are veterinarian IDs (as {@link Long}) and the values are
     *         {@link VeterinarianReviewProjection} objects containing review data.
     */
    Map<Long, VeterinarianReviewProjection> getAverageRatingsAndTotalReviews();

    /**
     * Retrieves a mapping of veterinarian IDs to their aggregated review data, filtered by specialization.
     * <p>
     * The returned map contains information such as average ratings and the total number of reviewers
     * for each veterinarian who matches the specified specialization.
     * </p>
     *
     * @param specialization the specialization of veterinarians to filter by (e.g., "Surgery", "Dentistry").
     * @return a {@link Map} where the keys are veterinarian IDs (as {@link Long}) and the values are
     *         {@link VeterinarianReviewProjection} objects containing review data for veterinarians
     *         with the specified specialization.
     */
    Map<Long, VeterinarianReviewProjection> getAverageRatingsAndTotalReviewsBySpecialization(String specialization);
}
