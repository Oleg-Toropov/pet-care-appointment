package com.olegtoropoff.petcareappointment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a patient in the system.
 * <p>
 * This entity extends the {@link User} class, adding specific details or behavior for patients.
 * Patients are users who book appointments for their pets.
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "patient_id")
public class Patient extends User {

    /**
     * Unique identifier for the patient.
     * <p>
     * This field is explicitly defined to override or enhance the {@link User} class's ID field.
     */
    private Long id;
}
