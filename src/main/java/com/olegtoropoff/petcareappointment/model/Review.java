package com.olegtoropoff.petcareappointment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

/**
 * Represents a review entity that allows patients to provide feedback and ratings for veterinarians.
 * <p>
 * Each review is linked to a veterinarian and a patient, capturing feedback details and star ratings.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"veterinarian", "patient"})
public class Review {

    /**
     * Unique identifier for the review.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Feedback provided by the patient in text format.
     * <p>
     * This field is stored as a {@code TEXT} in the database to accommodate longer comments.
     */
    @Column(columnDefinition = "TEXT")
    private String feedback;

    /**
     * Star rating given by the patient (e.g., 1 to 5).
     * <p>
     * Represents the quality of service as perceived by the patient.
     */
    private int stars;

    /**
     * The veterinarian who is being reviewed.
     */
    @ManyToOne
    @JoinColumn(name = "veterinarian_id")
    private User veterinarian;

    /**
     * The patient who provided the review.
     */
    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User patient;

    /**
     * Removes the association of this review with both the veterinarian and the patient.
     * <p>
     * Ensures that the review is no longer linked to either entity by removing it
     * from their respective review lists, if applicable.
     */
    public void removeRelationShip() {
        Optional.ofNullable(veterinarian).ifPresent(vet -> vet.getReviews().remove(this));
        Optional.ofNullable(patient).ifPresent(pat -> pat.getReviews().remove(this));
    }
}
