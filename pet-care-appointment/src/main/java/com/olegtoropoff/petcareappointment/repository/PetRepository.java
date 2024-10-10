package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
