package com.olegtoropoff.petcareappointment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Represents a veterinarian in the system.
 * <p>
 * This entity extends the {@link User} class, adding specific attributes and behavior for veterinarians.
 * Veterinarians handle appointments and provide medical care for pets.
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "veterinarian_id")
public class Veterinarian extends User{

    /**
     * Unique identifier for the veterinarian.
     * <p>
     * This field is explicitly defined to override or enhance the {@link User} class's ID field.
     */
    private Long id;

    /**
     * Specialization of the veterinarian (e.g., "Surgery", "Dentistry", "General Practice").
     */
    private String specialization;

    /**
     * The cost of the veterinary appointment.
     * This field represents the price a veterinarian charges for an appointment.
     */
    private BigDecimal appointmentCost;

    /**
     * The address where the veterinarian conducts appointments.
     * This field stores the location of the clinic or private practice.
     */
    private String clinicAddress;

    /**
     * Biography of the veterinarian.
     * <p>
     * This field references the {@link VetBiography} entity and provides additional
     * details about the veterinarian's qualifications, experience, and background.
     * <p>
     * The relationship is configured as a one-to-one mapping with cascade operations and orphan removal enabled.
     */
    @OneToOne(mappedBy = "veterinarian", cascade =  CascadeType.ALL, orphanRemoval = true)
    private VetBiography vetBiography ;
}
