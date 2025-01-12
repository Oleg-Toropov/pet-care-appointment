package com.olegtoropoff.petcareappointment.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a pet's details.
 * This class is used to transfer pet-related data between different layers of the application,
 * ensuring separation between the persistence and presentation layers.
 */
@Data
public class PetDto {
    /**
     * The unique identifier of the pet.
     */
    private Long id;

    /**
     * The name of the pet.
     */
    private String name;

    /**
     * The type of the pet (e.g., Dog, Cat, Bird).
     */
    private String type;

    /**
     * The color of the pet.
     */
    private String color;

    /**
     * The breed of the pet.
     */
    private String breed;

    /**
     * The age of the pet in years.
     */
    private int age;
}
