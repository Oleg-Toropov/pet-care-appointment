package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.Review;
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
     * Retrieves all reviews for a specific user, including reviews written by the user
     * and reviews of the user as a veterinarian.
     *
     * @param userId the ID of the user.
     * @return a list of reviews.
     */
    @Query("SELECT r FROM Review r WHERE r.patient.id =:userId OR r.veterinarian.id =:userId")
    List<Review> findAllByUserId(@Param("userId") Long userId);

    /**
     * Retrieves a review by the veterinarian and patient IDs, if it exists.
     *
     * @param veterinarianId the ID of the veterinarian.
     * @param reviewerId     the ID of the patient who wrote the review.
     * @return an optional containing the review if it exists, or empty if not.
     */
    Optional<Review> findByVeterinarianIdAndPatientId(Long veterinarianId, Long reviewerId);

    /**
     * Retrieves a list of average ratings and total review counts for all veterinarians
     * who are enabled (i.e., {@code isEnabled = true}). The query aggregates the data
     * for veterinarians by calculating the average rating and the total number of reviews
     * for each veterinarian.

     * This query only includes veterinarians whose {@code isEnabled} field is set to {@code true}.
     * The results are grouped by veterinarian ID.
     *
     * @return a {@link List} of {@link VeterinarianReviewProjection}, where each element contains:
     * <ul>
     *     <li>The veterinarian's ID</li>
     *     <li>The average rating of the veterinarian</li>
     *     <li>The total number of reviews for the veterinarian</li>
     * </ul>
     * Only veterinarians with {@code isEnabled = true} are included in the result.
     */
    @Query("SELECT r.veterinarian.id AS veterinarianId, AVG(r.stars) AS averageRating, COUNT(r.id) AS totalReviewers " +
           "FROM Review r " +
           "WHERE r.veterinarian.isEnabled = true " +
           "GROUP BY r.veterinarian.id")
    List<VeterinarianReviewProjection> findAllAverageRatingsAndTotalReviews();

    /**
     * Retrieves a list of average ratings and total review counts for veterinarians
     * who are enabled (i.e., {@code isEnabled = true}) and have the specified specialization.
     * The query aggregates the data for veterinarians by calculating the average rating
     * and the total number of reviews for each veterinarian that matches the given specialization.

     * This query only includes veterinarians whose {@code isEnabled} field is set to {@code true}
     * and whose {@code specialization} matches the provided value. The results are grouped by veterinarian ID.
     *
     * @param specialization the specialization of the veterinarians to filter by (e.g., "Surgery", "Dentistry").
     *                       Only veterinarians with this specialization will be included in the results.
     *
     * @return a {@link List} of {@link VeterinarianReviewProjection}, where each element contains:
     * <ul>
     *     <li>The veterinarian's ID</li>
     *     <li>The average rating of the veterinarian</li>
     *     <li>The total number of reviews for the veterinarian</li>
     * </ul>
     * Only veterinarians with {@code isEnabled = true} and the specified {@code specialization} are included in the result.
     */
    @Query("SELECT r.veterinarian.id AS veterinarianId, AVG(r.stars) AS averageRating, COUNT(r.id) AS totalReviewers " +
           "FROM Review r " +
           "WHERE r.veterinarian.isEnabled = true " +
           "AND  r.veterinarian.id IN (SELECT v.id FROM Veterinarian v WHERE v.specialization = :specialization)" +
           "GROUP BY r.veterinarian.id")
    List<VeterinarianReviewProjection> findAllAverageRatingsAndTotalReviewsBySpecialization(@Param("specialization") String specialization);
}
