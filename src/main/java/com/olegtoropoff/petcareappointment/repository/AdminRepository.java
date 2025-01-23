package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Admin} entities.
 * Provides CRUD operations and allows additional query method definitions.
 */
public interface AdminRepository extends JpaRepository<Admin, Long> {
}
