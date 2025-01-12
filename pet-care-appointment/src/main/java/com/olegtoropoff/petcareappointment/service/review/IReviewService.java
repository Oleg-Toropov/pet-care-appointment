package com.olegtoropoff.petcareappointment.service.review;

import com.olegtoropoff.petcareappointment.model.Review;
import org.springframework.data.domain.Page;

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
     * Calculates the average rating for a veterinarian.
     *
     * @param veterinarianId the ID of the veterinarian.
     * @return the average rating, or 0 if the veterinarian has no reviews.
     */
    double getAverageRatingForVet(Long veterinarianId);

    /**
     * Retrieves a paginated list of reviews for a specific user.
     *
     * @param userId the ID of the user.
     * @param page   the page number.
     * @param size   the page size.
     * @return a paginated list of reviews.
     */
    Page<Review> findAllReviewsByUserId(Long userId, int page, int size);

    /**
     * Deletes a review by its ID.
     *
     * @param reviewerId the ID of the review to delete.
     * @throws com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException if the review does not exist.
     */
    void deleteReview(Long reviewerId);
}
