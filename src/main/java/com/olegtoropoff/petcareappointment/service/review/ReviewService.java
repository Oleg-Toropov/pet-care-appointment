package com.olegtoropoff.petcareappointment.service.review;

import com.olegtoropoff.petcareappointment.enums.AppointmentStatus;
import com.olegtoropoff.petcareappointment.exception.AlreadyExistsException;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Review;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.projection.VeterinarianReviewProjection;
import com.olegtoropoff.petcareappointment.repository.AppointmentRepository;
import com.olegtoropoff.petcareappointment.repository.ReviewRepository;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing reviews.
 * Handles the logic for creating, deleting, and retrieving reviews,
 * as well as calculating average ratings for veterinarians.
 */
@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    /**
     * Saves a new review for a veterinarian.
     * <p>
     * This method ensures that:
     * <ul>
     *     <li>The reviewer is not the veterinarian.</li>
     *     <li>The reviewer has not already reviewed the veterinarian.</li>
     *     <li>Both the veterinarian and the patient exist in the database.</li>
     *     <li>The patient has at least one completed appointment with the veterinarian.</li>
     * </ul>
     * If all conditions are met, the review is saved and associated with both the veterinarian and the patient.
     * <p>
     * <b>Cache Eviction:</b>
     * - Clears `veterinarians_with_details` caches to ensure fresh data.
     *
     * @param review         the review to save.
     * @param reviewerId     the ID of the patient submitting the review.
     * @param veterinarianId the ID of the veterinarian being reviewed.
     * @return the saved review.
     * @throws IllegalArgumentException  if the reviewer is the veterinarian.
     * @throws AlreadyExistsException    if the patient has already reviewed the veterinarian.
     * @throws ResourceNotFoundException if the veterinarian or patient does not exist.
     * @throws IllegalStateException     if the patient has no completed appointments with the veterinarian.
     */
    @Caching(evict = {
            @CacheEvict(value = "veterinarians_with_details", allEntries = true),
    })
    @Transactional
    @Override
    public Review saveReview(Review review, Long reviewerId, Long veterinarianId) {
        if (veterinarianId.equals(reviewerId)) {
            throw new IllegalArgumentException(FeedBackMessage.CANNOT_REVIEW);
        }

        Optional<Review> existingReview = reviewRepository.findByVeterinarianIdAndPatientId(veterinarianId, reviewerId);
        if (existingReview.isPresent()) {
            throw new AlreadyExistsException(FeedBackMessage.ALREADY_REVIEWED);
        }

        User veterinarian = userRepository.findById(veterinarianId).orElseThrow(() ->
                new ResourceNotFoundException(FeedBackMessage.VET_OR_PATIENT_NOT_FOUND));

        User patient = userRepository.findById(reviewerId).orElseThrow(() ->
                new ResourceNotFoundException(FeedBackMessage.VET_OR_PATIENT_NOT_FOUND));

        boolean hadCompletedAppointments =
                appointmentRepository.existsByVeterinarianIdAndPatientIdAndStatus(veterinarianId, reviewerId, AppointmentStatus.COMPLETED);
        if (!hadCompletedAppointments) {
            throw new IllegalStateException(FeedBackMessage.REVIEW_NOT_ALLOWED);
        }

        review.setVeterinarian(veterinarian);
        review.setPatient(patient);

        return reviewRepository.save(review);
    }

    /**
     * Deletes a review by its ID.
     * <p>
     * <b>Cache Eviction:</b>
     * - Clears `veterinarians_with_details` caches.
     *
     * @param reviewId the ID of the review to delete.
     * @throws ResourceNotFoundException if the review does not exist.
     */
    @CacheEvict(value = "veterinarians_with_details", allEntries = true)
    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.REVIEW_NOT_FOUND));
        review.removeRelationShip();
        reviewRepository.deleteById(reviewId);
    }

    /**
     * Retrieves a map containing the average ratings and total review counts for all veterinarians
     * who are enabled (i.e., {@code isEnabled = true}).
     * <p>
     * This data is fetched from the database using a repository query and returned as a map,
     * where:
     * <ul>
     *     <li>The key is the veterinarian's ID ({@link Long}).</li>
     *     <li>The value is a {@link VeterinarianReviewProjection} containing:
     *         <ul>
     *             <li>Average rating of the veterinarian</li>
     *             <li>Total number of reviews for the veterinarian</li>
     *         </ul>
     *     </li>
     * </ul>
     * <p>
     * @return a {@link Map} containing the average rating and review count for each veterinarian.
     */
    @Override
    public Map<Long, VeterinarianReviewProjection> getAverageRatingsAndTotalReviews() {
        List<VeterinarianReviewProjection> averageRatingsAndTotalReviews = reviewRepository.findAllAverageRatingsAndTotalReviews();
        return averageRatingsAndTotalReviews.stream()
                .collect(Collectors.toMap(VeterinarianReviewProjection::getVeterinarianId, stats -> stats));
    }

    /**
     * Retrieves all reviews associated with a specific user.
     * <p>
     * This method queries all reviews where the given user is either the reviewer or the veterinarian.
     * <p>
     *
     * @param userId the ID of the user whose reviews are to be retrieved.
     * @return a {@link List} of {@link Review} containing all reviews for the specified user.
     */
    @Override
    public List<Review> findAllReviewsByUserId(Long userId) {
        return reviewRepository.findAllByUserId(userId);
    }
}
