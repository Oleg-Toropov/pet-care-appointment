package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.exception.AlreadyExistsException;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Review;
import com.olegtoropoff.petcareappointment.response.CustomApiResponse;
import com.olegtoropoff.petcareappointment.service.review.IReviewService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

/**
 * Controller for managing reviews in the system.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.REVIEWS)
public class ReviewController {
    private final IReviewService reviewService;

    /**
     * Submits a new review for a veterinarian by a patient.
     *
     * @param review         the review to be saved
     * @param reviewerId     the ID of the patient submitting the review
     * @param veterinarianId the ID of the veterinarian being reviewed
     * @return a response containing the ID of the saved review or an error message
     */
    @PostMapping(UrlMapping.SUBMIT_REVIEW)
    public ResponseEntity<CustomApiResponse> saveReview(@RequestBody Review review, @RequestParam Long reviewerId,
                                                        @RequestParam Long veterinarianId) {
        try {
            Review savedReview = reviewService.saveReview(review, reviewerId, veterinarianId);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.REVIEW_SUBMIT_SUCCESS, savedReview.getId()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new CustomApiResponse(e.getMessage(), null));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new CustomApiResponse(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        }
    }

    /**
     * Deletes a review by its ID.
     *
     * @param reviewId the ID of the review to be deleted
     * @return a response indicating success or an error message
     */
    @DeleteMapping(UrlMapping.DELETE_REVIEW)
    public ResponseEntity<CustomApiResponse> deleteReview(@PathVariable Long reviewId) {
        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.REVIEW_DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        }
    }
}
