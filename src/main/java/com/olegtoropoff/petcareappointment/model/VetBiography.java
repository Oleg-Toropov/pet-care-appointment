package com.olegtoropoff.petcareappointment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a biography for a veterinarian.
 * This entity contains information about a veterinarian's professional background,
 * experience, and qualifications. Each veterinarian can have only one associated biography.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"veterinarian"})
public class VetBiography {

    /**
     * Unique identifier for the biography.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The textual content of the veterinarian's biography.
     * <p>
     * This field stores detailed information such as experience, education, and specialties.
     */
    @Column(columnDefinition = "TEXT")
    private String biography;

    /**
     * The veterinarian associated with this biography.
     * <p>
     * Establishes a one-to-one relationship with the {@link Veterinarian} entity.
     */
    @OneToOne
    @JoinColumn(name = "veterinarian_id")
    private Veterinarian veterinarian;
}
