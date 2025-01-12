package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Photo} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations.
 */
public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
