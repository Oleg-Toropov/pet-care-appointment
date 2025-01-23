package com.olegtoropoff.petcareappointment.service.review;

import com.olegtoropoff.petcareappointment.enums.AppointmentStatus;
import com.olegtoropoff.petcareappointment.exception.AlreadyExistsException;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Review;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.AppointmentRepository;
import com.olegtoropoff.petcareappointment.repository.ReviewRepository;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.projection.VeterinarianReviewProjection;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
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
     *
     * @param review the review to save.
     * @param reviewerId the ID of the patient submitting the review.
     * @param veterinarianId the ID of the veterinarian being reviewed.
     * @return the saved review.
     * @throws IllegalArgumentException if the reviewer is the veterinarian.
     * @throws AlreadyExistsException  if the patient has already reviewed the veterinarian.
     * @throws ResourceNotFoundException if the veterinarian or patient does not exist.
     * @throws IllegalStateException if the patient has no completed appointments with the veterinarian.
     */
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
     *
     * @param reviewId the ID of the review to delete.
     * @throws ResourceNotFoundException if the review does not exist.
     */
    @Override
    public void deleteReview(Long reviewId) {
        reviewRepository.findById(reviewId).ifPresentOrElse(Review::removeRelationShip, () -> {
            throw new ResourceNotFoundException(FeedBackMessage.REVIEW_NOT_FOUND);
        });
        reviewRepository.deleteById(reviewId);
    }

    /**
     * Retrieves all reviews associated with a specific user.
     *
     * @param userId the ID of the user whose reviews are to be retrieved.
     * @return a {@link List} of {@link Review} containing all reviews for the specified user.
     */
    @Override
    public List<Review> findAllReviewsByUserId(Long userId) {
        return reviewRepository.findAllByUserId(userId);
    }


    /**
     * Retrieves a map containing the average ratings and total review counts for all veterinarians
     * who are enabled (i.e., {@code isEnabled = true}). The data is fetched from the database
     * using a repository query and returned as a map, where the key is the veterinarian's ID,
     * and the value is a projection containing the average rating and the total number of reviews.
     * <p>
     * This method is now part of the {@code ReviewService}, focusing on aggregated statistics
     * for veterinarians based on their reviews. It filters veterinarians whose {@code isEnabled}
     * field is set to {@code true}.
     *
     * @return a {@link Map} where the key is the veterinarian ID ({@link Long}) and the value is
     *         a {@link VeterinarianReviewProjection}, which contains:
     *         <ul>
     *             <li>Average rating of the veterinarian</li>
     *             <li>Total number of reviews for the veterinarian</li>
     *         </ul>
     *         Only veterinarians with {@code isEnabled = true} are included in the map.
     */
    @Override
    public Map<Long, VeterinarianReviewProjection> getAverageRatingsAndTotalReviews() {
        List<VeterinarianReviewProjection> averageRatingsAndTotalReviews = reviewRepository.findAllAverageRatingsAndTotalReviews();
        return averageRatingsAndTotalReviews.stream()
                .collect(Collectors.toMap(VeterinarianReviewProjection::getVeterinarianId, stats -> stats));
    }

    /**
     * Retrieves a map containing the average ratings and total review counts for all veterinarians
     * who are enabled (i.e., {@code isEnabled = true}) and have the specified specialization.
     * The data is fetched from the database using a repository query and returned as a map,
     * where the key is the veterinarian's ID, and the value is a projection containing the average rating
     * and the total number of reviews.
     * <p>
     * This method is part of the {@code ReviewService} and provides aggregated review statistics for veterinarians
     * filtered by both their {@code isEnabled} status and the specified {@code specialization}.
     *
     * @param specialization the specialization of the veterinarians to filter by (e.g., "Surgery", "Dentistry").
     *                       Only veterinarians with this specialization will be included.
     *
     * @return a {@link Map} where the key is the veterinarian ID ({@link Long}) and the value is
     *         a {@link VeterinarianReviewProjection}, which contains:
     *         <ul>
     *             <li>Average rating of the veterinarian</li>
     *             <li>Total number of reviews for the veterinarian</li>
     *         </ul>
     *         Only veterinarians with {@code isEnabled = true} and the specified {@code specialization} are included in the map.
     */
    @Override
    public Map<Long, VeterinarianReviewProjection> getAverageRatingsAndTotalReviewsBySpecialization(String specialization) {
        List<VeterinarianReviewProjection> averageRatingsAndTotalReviews = reviewRepository.findAllAverageRatingsAndTotalReviewsBySpecialization(specialization);
        return averageRatingsAndTotalReviews.stream()
                .collect(Collectors.toMap(VeterinarianReviewProjection::getVeterinarianId, stats -> stats));
    }
}
