package com.olegtoropoff.petcareappointment.service.review;

import com.olegtoropoff.petcareappointment.model.Review;
import com.olegtoropoff.petcareappointment.repository.VeterinarianReviewProjection;

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
     * @throws com.olegtoropoff.petcareappointment.exception.AlreadyExistsException if the patient has already reviewed the veterinarian.
     * @throws com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException if the veterinarian or patient does not exist.
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
     * @throws com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException if the review does not exist.
     */
    void deleteReview(Long reviewerId);

    Map<Long, VeterinarianReviewProjection> getAverageRatingsAndTotalReviews();

    Map<Long, VeterinarianReviewProjection> getAverageRatingsAndTotalReviewsBySpecialization(String specialization);
}
