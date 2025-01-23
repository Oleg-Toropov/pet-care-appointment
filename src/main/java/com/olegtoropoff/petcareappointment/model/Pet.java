package com.olegtoropoff.petcareappointment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a pet entity associated with a veterinary appointment.
 * <p>
 * A pet has details such as name, type, color, breed, and age.
 * It is linked to a specific appointment through a many-to-one relationship.
 * <p>
 * This class is part of the persistence layer and is annotated with JPA annotations for ORM mapping.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"appointment"})
public class Pet {

    /**
     * Unique identifier for the pet.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the pet.
     */
    private String name;

    /**
     * Type of the pet (e.g., "Dog", "Cat").
     */
    private String type;

    /**
     * Color of the pet.
     */
    private String color;

    /**
     * Breed of the pet.
     */
    private String breed;

    /**
     * Age of the pet in years.
     */
    private int age;

    /**
     * The appointment to which the pet is linked.
     * <p>
     * This field establishes a many-to-one relationship with the {@link Appointment} entity.
     * It is ignored in JSON serialization to avoid circular references.
     */
    @ManyToOne
    private Appointment appointment;
}
