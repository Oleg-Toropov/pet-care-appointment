package com.olegtoropoff.petcareappointment.service.review;

import com.olegtoropoff.petcareappointment.enums.AppointmentStatus;
import com.olegtoropoff.petcareappointment.exception.AlreadyExistsException;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Review;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.AppointmentRepository;
import com.olegtoropoff.petcareappointment.repository.ReviewRepository;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
     * Retrieves a paginated list of reviews for a specific user.
     *
     * @param userId the ID of the user.
     * @param page   the page number.
     * @param size   the page size.
     * @return a paginated list of reviews.
     */
    @Override
    public Page<Review> findAllReviewsByUserId(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return reviewRepository.findAllByUserId(userId, pageRequest);
    }

    /**
     * Calculates the average rating for a veterinarian.
     *
     * @param veterinarianId the ID of the veterinarian.
     * @return the average rating, or 0 if the veterinarian has no reviews.
     */
    @Transactional
    @Override
    public double getAverageRatingForVet(Long veterinarianId) {
        List<Review> reviews = reviewRepository.findByVeterinarianId(veterinarianId);
        return reviews.isEmpty() ? 0 : reviews.stream()
                .mapToInt(Review::getStars)
                .average()
                .orElse(0.0);
    }
}
