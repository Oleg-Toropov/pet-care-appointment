package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for managing Pet entities.
 * Provides methods for retrieving distinct pet attributes and performing CRUD operations on Pet entities.
 */
public interface PetRepository extends JpaRepository<Pet, Long> {

    /**
     * Retrieves a list of distinct pet types.
     *
     * @return a list of unique pet types
     */
    @Query("SELECT DISTINCT p.type FROM Pet p")
    List<String> getDistinctPetTypes();

    /**
     * Retrieves a list of distinct pet colors.
     *
     * @return a list of unique pet colors
     */
    @Query("SELECT DISTINCT p.color FROM Pet p")
    List<String> getDistinctPetColors();

    /**
     * Retrieves a list of distinct pet breeds for a specific pet type.
     *
     * @param petType the type of pet to filter breeds by
     * @return a list of unique pet breeds for the specified pet type
     */
    @Query("SELECT DISTINCT p.breed FROM Pet p WHERE p.type = :petType")
    List<String> getDistinctPetBreedsByPetType(String petType);
}
