package com.olegtoropoff.petcareappointment.service.review;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.olegtoropoff.petcareappointment.enums.AppointmentStatus;
import com.olegtoropoff.petcareappointment.exception.AlreadyExistsException;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Review;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.AppointmentRepository;
import com.olegtoropoff.petcareappointment.repository.ReviewRepository;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@Tag("unit")
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveReview_Success() {
        Long reviewerId = 1L;
        Long veterinarianId = 2L;

        Review review = new Review();
        User veterinarian = new User();
        User patient = new User();

        when(reviewRepository.findByVeterinarianIdAndPatientId(veterinarianId, reviewerId)).thenReturn(Optional.empty());
        when(userRepository.findById(veterinarianId)).thenReturn(Optional.of(veterinarian));
        when(userRepository.findById(reviewerId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.existsByVeterinarianIdAndPatientIdAndStatus(veterinarianId, reviewerId, AppointmentStatus.COMPLETED)).thenReturn(true);
        when(reviewRepository.save(review)).thenReturn(review);

        Review savedReview = reviewService.saveReview(review, reviewerId, veterinarianId);

        assertNotNull(savedReview);
        verify(reviewRepository).save(review);
    }

    @Test
    void saveReview_ThrowsIllegalArgumentException_WhenReviewerIsVeterinarian() {
        Long reviewerId = 1L;
        Long veterinarianId = 1L;

        Review review = new Review();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reviewService.saveReview(review, reviewerId, veterinarianId));

        assertEquals(FeedBackMessage.CANNOT_REVIEW, exception.getMessage());
    }

    @Test
    void saveReview_ThrowsAlreadyExistsException_WhenReviewAlreadyExists() {
        Long reviewerId = 1L;
        Long veterinarianId = 2L;

        Review review = new Review();
        when(reviewRepository.findByVeterinarianIdAndPatientId(veterinarianId, reviewerId)).thenReturn(Optional.of(review));

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class,
                () -> reviewService.saveReview(review, reviewerId, veterinarianId));

        assertEquals(FeedBackMessage.ALREADY_REVIEWED, exception.getMessage());
    }

    @Test
    void saveReview_ThrowsResourceNotFoundException_WhenVeterinarianNotFound() {
        Long reviewerId = 1L;
        Long veterinarianId = 2L;

        Review review = new Review();
        when(reviewRepository.findByVeterinarianIdAndPatientId(veterinarianId, reviewerId)).thenReturn(Optional.empty());
        when(userRepository.findById(veterinarianId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.saveReview(review, reviewerId, veterinarianId));

        assertEquals(FeedBackMessage.VET_OR_PATIENT_NOT_FOUND, exception.getMessage());
    }

    @Test
    void deleteReview_Success() {
        Long reviewId = 1L;
        Review review = new Review();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        reviewService.deleteReview(reviewId);

        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    void deleteReview_ThrowsResourceNotFoundException_WhenReviewNotFound() {
        Long reviewId = 1L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.deleteReview(reviewId));

        assertEquals(FeedBackMessage.REVIEW_NOT_FOUND, exception.getMessage());
    }

    @Test
    void findAllReviewsByUserId_Success() {
        Long userId = 1L;
        int page = 0;
        int size = 10;

        Page<Review> reviewsPage = new PageImpl<>(List.of(new Review()));
        when(reviewRepository.findAllByUserId(userId, PageRequest.of(page, size))).thenReturn(reviewsPage);

        Page<Review> result = reviewService.findAllReviewsByUserId(userId, page, size);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getAverageRatingForVet_Success() {
        Long veterinarianId = 1L;

        Review review1 = new Review();
        review1.setStars(5);
        Review review2 = new Review();
        review2.setStars(4);
        List<Review> reviews = List.of(review1, review2);
        when(reviewRepository.findByVeterinarianId(veterinarianId)).thenReturn(reviews);

        double averageRating = reviewService.getAverageRatingForVet(veterinarianId);

        assertEquals(4.5, averageRating);
    }

    @Test
    void getAverageRatingForVet_ReturnsZero_WhenNoReviews() {
        Long veterinarianId = 1L;

        when(reviewRepository.findByVeterinarianId(veterinarianId)).thenReturn(List.of());

        double averageRating = reviewService.getAverageRatingForVet(veterinarianId);

        assertEquals(0.0, averageRating);
    }
}
