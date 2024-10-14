package com.olegtoropoff.petcareappointment.service.review;

import com.olegtoropoff.petcareappointment.model.Review;
import com.olegtoropoff.petcareappointment.request.ReviewUpdateRequest;
import org.springframework.data.domain.Page;

public interface IReviewService {
    Review saveReview(Review review, Long reviewerId, Long veterinarianId);
    double getAverageRatingForVet(Long veterinarianId);
    Review updateReview(Long reviewerId, ReviewUpdateRequest review);
    Page<Review> findAllReviewsByUserId(Long userId, int page, int size);

    void deleteReview(Long reviewerId);
}
