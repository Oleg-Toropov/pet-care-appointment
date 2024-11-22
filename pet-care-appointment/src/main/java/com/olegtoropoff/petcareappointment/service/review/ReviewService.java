package com.olegtoropoff.petcareappointment.service.review;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Review;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.AppointmentRepository;
import com.olegtoropoff.petcareappointment.repository.ReviewRepository;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.request.ReviewUpdateRequest;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Override
    public Review saveReview(Review review, Long reviewerId, Long veterinarianId) {
        if (veterinarianId.equals(reviewerId)) {
            throw new IllegalArgumentException(FeedBackMessage.CANNOT_REVIEW);
        }
// todo uncomment
//        Optional<Review> existingReview = reviewRepository.findByVeterinarianIdAndPatientId(veterinarianId, reviewerId);
//        if (existingReview.isPresent()) {
//            throw new AlreadyExistsException(FeedBackMessage.ALREADY_REVIEWED);
//        }

        User veterinarian = userRepository.findById(veterinarianId).orElseThrow(() ->
                new ResourceNotFoundException(FeedBackMessage.VET_OR_PATIENT_NOT_FOUND));

        User patient = userRepository.findById(reviewerId).orElseThrow(() ->
                new ResourceNotFoundException(FeedBackMessage.VET_OR_PATIENT_NOT_FOUND));

//        boolean hadCompletedAppointments =
//                appointmentRepository.existsByVeterinarianIdAndPatientIdAndStatus(veterinarianId, reviewerId, AppointmentStatus.COMPLETED);
//        if (!hadCompletedAppointments) {
//            throw new IllegalStateException(FeedBackMessage.REVIEW_NOT_ALLOWED);
//        }

        review.setVeterinarian(veterinarian);
        review.setPatient(patient);

        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Long reviewId, ReviewUpdateRequest review) {
        return reviewRepository.findById(reviewId)
                .map(existingReview -> {
                    existingReview.setStars(review.getStars());
                    existingReview.setFeedback(review.getFeedback());
                    return reviewRepository.save(existingReview);
                }).orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.REVIEW_NOT_FOUND));
    }

    @Override
    public void deleteReview(Long reviewId) {
        reviewRepository.findById(reviewId).ifPresentOrElse(Review::removeRelationShip, () -> {
            throw new ResourceNotFoundException(FeedBackMessage.REVIEW_NOT_FOUND);
        });
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public Page<Review> findAllReviewsByUserId(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return reviewRepository.findAllByUserId(userId, pageRequest);
    }

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
