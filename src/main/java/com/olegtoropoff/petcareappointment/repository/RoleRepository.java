package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Role} entities.
 * Extends {@link JpaRepository} to provide basic CRUD operations and custom queries.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by its name.
     *
     * @param roleName the name of the role to find
     * @return an {@link Optional} containing the role if found, or {@link Optional#empty()} if not found
     */
    Optional<Role> findByName(String roleName);
}