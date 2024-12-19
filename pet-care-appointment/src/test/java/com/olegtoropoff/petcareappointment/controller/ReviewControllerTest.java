package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.exception.AlreadyExistsException;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Review;
import com.olegtoropoff.petcareappointment.response.ApiResponse;
import com.olegtoropoff.petcareappointment.service.review.IReviewService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class ReviewControllerTest {

    @InjectMocks
    private ReviewController reviewController;

    @Mock
    private IReviewService reviewService;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void saveReview_ReturnsSuccessResponse() {
        Review review = new Review();
        Review savedReview = new Review();
        savedReview.setId(1L);
        Long reviewerId = 1L;
        Long veterinarianId = 2L;

        when(reviewService.saveReview(review, reviewerId, veterinarianId)).thenReturn(savedReview);

        ResponseEntity<ApiResponse> response = reviewController.saveReview(review, reviewerId, veterinarianId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.REVIEW_SUBMIT_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        verify(reviewService, times(1)).saveReview(review, reviewerId, veterinarianId);
    }

    @Test
    void saveReview_ThrowsIllegalArgumentException() {
        Review review = new Review();
        Long reviewerId = 2L;
        Long veterinarianId = 2L;
        String errorMessage = FeedBackMessage.CANNOT_REVIEW;

        when(reviewService.saveReview(review, reviewerId, veterinarianId))
                .thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<ApiResponse> response = reviewController.saveReview(review, reviewerId, veterinarianId);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(reviewService, times(1)).saveReview(review, reviewerId, veterinarianId);
    }

    @Test
    void saveReview_ThrowsIllegalStateException() {
        Review review = new Review();
        Long reviewerId = 1L;
        Long veterinarianId = 2L;
        String errorMessage =  FeedBackMessage.REVIEW_NOT_ALLOWED;
        when(reviewService.saveReview(review, reviewerId, veterinarianId))
                .thenThrow(new IllegalStateException(errorMessage));

        ResponseEntity<ApiResponse> response = reviewController.saveReview(review, reviewerId, veterinarianId);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(reviewService, times(1)).saveReview(review, reviewerId, veterinarianId);
    }

    @Test
    void saveReview_ThrowsAlreadyExistsException() {
        Review review = new Review();
        Long reviewerId = 1L;
        Long veterinarianId = 2L;
        String errorMessage =  FeedBackMessage.ALREADY_REVIEWED;
        when(reviewService.saveReview(review, reviewerId, veterinarianId))
                .thenThrow(new AlreadyExistsException(errorMessage));

        ResponseEntity<ApiResponse> response = reviewController.saveReview(review, reviewerId, veterinarianId);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(reviewService, times(1)).saveReview(review, reviewerId, veterinarianId);
    }

    @Test
    void saveReview_ThrowsResourceNotFoundException() {
        Review review = new Review();
        Long reviewerId = 1L;
        Long veterinarianId = 2L;
        String errorMessage =  FeedBackMessage.VET_OR_PATIENT_NOT_FOUND;
        when(reviewService.saveReview(review, reviewerId, veterinarianId))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        ResponseEntity<ApiResponse> response = reviewController.saveReview(review, reviewerId, veterinarianId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(reviewService, times(1)).saveReview(review, reviewerId, veterinarianId);
    }

    @Test
    void deleteReview_ReturnsSuccessResponse() {
        Long reviewId = 1L;

        doNothing().when(reviewService).deleteReview(reviewId);

        ResponseEntity<ApiResponse> response = reviewController.deleteReview(reviewId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.REVIEW_DELETE_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        verify(reviewService, times(1)).deleteReview(reviewId);
    }

    @Test
    void deleteReview_ThrowsResourceNotFoundException() {
        Long reviewId = 1L;
        String errorMessage =  FeedBackMessage.REVIEW_NOT_FOUND;
        doThrow(new ResourceNotFoundException(errorMessage)).when(reviewService).deleteReview(reviewId);

        ResponseEntity<ApiResponse> response = reviewController.deleteReview(reviewId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(reviewService, times(1)).deleteReview(reviewId);
    }
}