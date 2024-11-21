package com.olegtoropoff.petcareappointment.service.review;

import com.olegtoropoff.petcareappointment.dto.ReviewDto;
import com.olegtoropoff.petcareappointment.model.Review;
import com.olegtoropoff.petcareappointment.request.ReviewUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IReviewService {
    Review saveReview(Review review, Long reviewerId, Long veterinarianId);

    double getAverageRatingForVet(Long veterinarianId);

    Review updateReview(Long reviewerId, ReviewUpdateRequest review);

    Page<ReviewDto> findAllReviewsByUserId(Long userId, Pageable pageable);

    void deleteReview(Long reviewerId);
}
