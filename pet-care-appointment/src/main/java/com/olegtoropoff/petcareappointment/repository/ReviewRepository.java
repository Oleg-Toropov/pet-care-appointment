package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Review entities.
 * Provides methods for querying reviews by patient, veterinarian, and other related data.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Retrieves a paginated list of reviews for a specific user, including reviews written by the user
     * and reviews of the user as a veterinarian.
     *
     * @param userId   the ID of the user.
     * @param pageable the pagination details.
     * @return a paginated list of reviews.
     */
    @Query("SELECT r FROM Review r WHERE r.patient.id =:userId OR r.veterinarian.id =:userId ")
    Page<Review> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Retrieves all reviews for a specific user, including reviews written by the user
     * and reviews of the user as a veterinarian.
     *
     * @param userId the ID of the user.
     * @return a list of reviews.
     */
    @Query("SELECT r FROM Review r WHERE r.patient.id =:userId OR r.veterinarian.id =:userId")
    List<Review> findAllByUserId(@Param("userId") Long userId);

    /**
     * Retrieves all reviews for a specific veterinarian.
     *
     * @param veterinarianId the ID of the veterinarian.
     * @return a list of reviews for the veterinarian.
     */
    List<Review> findByVeterinarianId(Long veterinarianId);

    /**
     * Retrieves a review by the veterinarian and patient IDs, if it exists.
     *
     * @param veterinarianId the ID of the veterinarian.
     * @param reviewerId     the ID of the patient who wrote the review.
     * @return an optional containing the review if it exists, or empty if not.
     */
    Optional<Review> findByVeterinarianIdAndPatientId(Long veterinarianId, Long reviewerId);

    /**
     * Counts the number of reviews for a specific veterinarian.
     *
     * @param id the ID of the veterinarian.
     * @return the number of reviews for the veterinarian.
     */
    Long countByVeterinarianId(long id);
}
